import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import {
  activateAdminUser,
  adjustGeneticWeights,
  deactivateAdminUser,
  deleteAdminUser,
  getAdminQuestion,
  getRecruiterMultiplier,
  getRoleMultiplier,
  listAdminAssessmentAttempts,
  listAdminQuestions,
  listAdminUsers,
  listQuestionFeedbacks,
  listQuestionVotes,
  listRecruiterVoteWeights,
  moderateAdminQuestion,
  setRecruiterMultiplier,
  setRecruiterVoteWeight,
  setRoleMultiplier,
  updateAdminQuestion,
} from './adminService'

describe('adminService', () => {
  it('lists admin users with filters and sends moderation requests', async () => {
    const calls: Record<string, boolean> = {}

    server.use(
      http.get('*/api/v1/admin/users', ({ request }) => {
        const url = new URL(request.url)

        expect(url.searchParams.get('name')).toBe('ana')
        expect(url.searchParams.get('role')).toBe('RECRUITER')
        expect(url.searchParams.get('page')).toBe('2')
        expect(url.searchParams.get('size')).toBe('20')

        return HttpResponse.json([makeAdminUser()])
      }),
      http.patch('*/api/v1/admin/users/user-1/activate', () => {
        calls.activate = true
        return new HttpResponse(null, { status: 200 })
      }),
      http.patch('*/api/v1/admin/users/user-1/deactivate', () => {
        calls.deactivate = true
        return new HttpResponse(null, { status: 200 })
      }),
      http.delete('*/api/v1/admin/users/user-1', () => {
        calls.delete = true
        return new HttpResponse(null, { status: 204 })
      }),
    )

    const users = await listAdminUsers({ name: 'ana', role: 'RECRUITER', page: 2, size: 20 })
    await activateAdminUser('user-1')
    await deactivateAdminUser('user-1')
    await deleteAdminUser('user-1')

    expect(users).toMatchObject([{ id: 'user-1', name: 'Ana Pereira', role: 'RECRUITER' }])
    expect(calls).toEqual({ activate: true, deactivate: true, delete: true })
  })

  it('lists admin questions with repeated knowledge area filters', async () => {
    server.use(
      http.get('*/api/v1/admin/questions', ({ request }) => {
        const url = new URL(request.url)

        expect(url.searchParams.getAll('knowledgeAreas')).toEqual(['SOFTWARE_DEVELOPMENT', 'DATABASE'])
        expect(url.searchParams.get('difficultyLevel')).toBe('MEDIUM')
        expect(url.searchParams.get('status')).toBe('APPROVED')
        expect(url.searchParams.get('authorName')).toBe('Ana Pereira')
        expect(url.searchParams.get('authorId')).toBe('author-1')
        expect(url.searchParams.get('page')).toBe('1')
        expect(url.searchParams.get('size')).toBe('10')

        return HttpResponse.json([makeQuestion()])
      }),
    )

    const questions = await listAdminQuestions({
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
      difficultyLevel: 'MEDIUM',
      status: 'APPROVED',
      authorName: 'Ana Pereira',
      authorId: 'author-1',
      page: 1,
      size: 10,
    })

    expect(questions).toMatchObject([{ id: 'question-1', title: 'Transacoes em APIs' }])
  })

  it('loads, updates and moderates an admin question', async () => {
    const calls: Record<string, unknown> = {}

    server.use(
      http.get('*/api/v1/admin/questions/question-1', () => HttpResponse.json(makeQuestion())),
      http.put('*/api/v1/admin/questions/question-1', async ({ request }) => {
        calls.update = await request.json()
        return HttpResponse.json({ ...makeQuestion(), title: 'Transacoes atualizadas' })
      }),
      http.patch('*/api/v1/admin/questions/question-1/moderate', async ({ request }) => {
        calls.moderate = await request.json()
        return HttpResponse.json({ ...makeQuestion(), status: 'REJECTED' })
      }),
    )

    const question = await getAdminQuestion('question-1')
    const updated = await updateAdminQuestion('question-1', {
      title: 'Transacoes atualizadas',
      description: 'Nova descricao',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
      alternatives: [{ id: 'alternative-1', text: 'Usar transacao' }],
    })
    const moderated = await moderateAdminQuestion('question-1', 'REJECTED')

    expect(question.status).toBe('APPROVED')
    expect(calls.update).toEqual({
      title: 'Transacoes atualizadas',
      description: 'Nova descricao',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
      alternatives: [{ id: 'alternative-1', text: 'Usar transacao' }],
    })
    expect(calls.moderate).toEqual({ newStatus: 'REJECTED' })
    expect(updated.title).toBe('Transacoes atualizadas')
    expect(moderated.status).toBe('REJECTED')
  })

  it('loads question votes and feedbacks for admin audit', async () => {
    server.use(
      http.get('*/api/v1/admin/questions/question-1/votes', () =>
        HttpResponse.json([
          {
            id: 'vote-1',
            user: makeAdminUser(),
            voteType: 'APPROVE',
            createdAt: '2026-05-24T12:00:00',
          },
        ]),
      ),
      http.get('*/api/v1/admin/questions/question-1/feedbacks', () =>
        HttpResponse.json([
          {
            id: 'feedback-1',
            comment: 'Questao clara.',
            author: { id: 'user-2', name: 'Carlos Silva' },
            submissionDate: '2026-05-24T12:10:00',
            reactions: [],
          },
        ]),
      ),
    )

    const votes = await listQuestionVotes('question-1')
    const feedbacks = await listQuestionFeedbacks('question-1')

    expect(votes[0]).toMatchObject({ id: 'vote-1', voteType: 'APPROVE' })
    expect(feedbacks[0]).toMatchObject({ id: 'feedback-1', comment: 'Questao clara.' })
  })

  it('lists admin assessment attempts with filters', async () => {
    server.use(
      http.get('*/api/v1/admin/assessments/attempts', ({ request }) => {
        const url = new URL(request.url)

        expect(url.searchParams.get('isPersonalized')).toBe('true')
        expect(url.searchParams.get('startDate')).toBe('2026-05-01T00:00:00')
        expect(url.searchParams.get('endDate')).toBe('2026-05-31T23:59:59')
        expect(url.searchParams.getAll('statuses')).toEqual(['COMPLETED', 'APPROVED'])

        return HttpResponse.json([makeAssessmentAttempt()])
      }),
    )

    const attempts = await listAdminAssessmentAttempts({
      isPersonalized: true,
      startDate: '2026-05-01T00:00:00',
      endDate: '2026-05-31T23:59:59',
      statuses: ['COMPLETED', 'APPROVED'],
    })

    expect(attempts[0]).toMatchObject({
      attemptId: 'attempt-1',
      candidate: { name: 'Carlos Silva' },
      assessment: { title: 'Java Backend' },
    })
  })

  it('lists recruiter vote weights and updates genetic algorithm controls', async () => {
    const calls: Record<string, unknown> = {}

    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', ({ request }) => {
        const url = new URL(request.url)

        expect(url.searchParams.get('name')).toBe('ana')
        expect(url.searchParams.get('page')).toBe('0')
        expect(url.searchParams.get('size')).toBe('20')

        return HttpResponse.json([
          {
            id: 'recruiter-1',
            name: 'Ana Pereira',
            email: 'ana.p@techrecruit.com',
            companyName: 'TechRecruit',
            companyEmail: 'contact@techrecruit.com',
            voteWeight: 10,
            multiplier: 1.75,
          },
        ])
      }),
      http.post('*/api/v1/admin/algorithms/genetic/adjust', async ({ request }) => {
        calls.adjust = await request.json()
        return HttpResponse.json({ 'recruiter-1': 8 })
      }),
      http.get('*/api/v1/admin/algorithms/genetic/roles/RECRUITER/multiplier', () =>
        HttpResponse.json(2.5),
      ),
      http.patch('*/api/v1/admin/algorithms/genetic/roles/RECRUITER/multiplier', async ({ request }) => {
        calls.roleMultiplier = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/recruiter-1/multiplier', () =>
        HttpResponse.json(1.75),
      ),
      http.patch('*/api/v1/admin/algorithms/genetic/recruiters/recruiter-1/multiplier', async ({ request }) => {
        calls.recruiterMultiplier = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
      http.patch('*/api/v1/admin/algorithms/genetic/recruiters/recruiter-1/vote-weight', async ({ request }) => {
        calls.voteWeight = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    const recruiters = await listRecruiterVoteWeights({ name: 'ana', page: 0, size: 20 })
    const adjusted = await adjustGeneticWeights(true)
    const roleMultiplier = await getRoleMultiplier('RECRUITER')
    await setRoleMultiplier('RECRUITER', 2.5)
    const recruiterMultiplier = await getRecruiterMultiplier('recruiter-1')
    await setRecruiterMultiplier('recruiter-1', 1.75)
    await setRecruiterVoteWeight('recruiter-1', 8)

    expect(recruiters[0]).toMatchObject({ id: 'recruiter-1', voteWeight: 10, multiplier: 1.75 })
    expect(adjusted).toEqual({ 'recruiter-1': 8 })
    expect(roleMultiplier).toBe(2.5)
    expect(recruiterMultiplier).toBe(1.75)
    expect(calls).toEqual({
      adjust: { dryRun: true },
      roleMultiplier: { multiplier: 2.5 },
      recruiterMultiplier: { multiplier: 1.75 },
      voteWeight: { weight: 8 },
    })
  })
})

function makeAdminUser() {
  return {
    id: 'user-1',
    name: 'Ana Pereira',
    email: 'ana.p@techrecruit.com',
    occupation: 'Tech recruiter',
    bio: null,
    experienceLevel: 'SENIOR',
    hardSkills: null,
    softSkills: null,
    role: 'RECRUITER',
    companyName: 'TechRecruit',
    companyEmail: 'contact@techrecruit.com',
  }
}

function makeQuestion() {
  return {
    type: 'MULTIPLE_CHOICE',
    id: 'question-1',
    authorId: 'author-1',
    title: 'Transacoes em APIs',
    description: 'Como garantir consistencia?',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    difficultyByCommunity: 'MEDIUM',
    relevanceByCommunity: 'FOUR',
    relevanceByLLM: null,
    submissionDate: '2026-05-24T10:00:00',
    status: 'APPROVED',
    alternatives: [{ id: 'alternative-1', text: 'Usar transacao' }],
  }
}

function makeAssessmentAttempt() {
  return {
    attemptId: 'attempt-1',
    status: 'COMPLETED',
    accuracyRate: 85,
    startedAt: '2026-05-24T10:00:00',
    finishedAt: '2026-05-24T10:45:00',
    assessment: {
      id: 'assessment-1',
      title: 'Java Backend',
      personalized: true,
      criteriaWeights: null,
      jobDescriptionAnalysis: null,
    },
    candidate: {
      ...makeAdminUser(),
      id: 'candidate-1',
      name: 'Carlos Silva',
      email: 'carlos.silva@example.com',
      role: 'PROFESSIONAL',
      companyName: null,
      companyEmail: null,
    },
  }
}
