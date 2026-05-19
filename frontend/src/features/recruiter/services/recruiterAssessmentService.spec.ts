import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import {
  analyzeJobDescription,
  createOpenQuestion,
  createPersonalizedAssessment,
  deletePersonalizedAssessment,
  evaluateAssessmentAttempt,
  generateAttemptPreAnalysis,
  getAssessmentAttemptDetails,
  getPersonalizedAssessment,
  listAssessmentAttempts,
  listPersonalizedAssessments,
  listSuggestedQuestions,
  updatePersonalizedAssessment,
} from './recruiterAssessmentService'

describe('recruiterAssessmentService', () => {
  it('lists personalized assessments', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized', () => HttpResponse.json([makeAssessment()])),
    )

    await expect(listPersonalizedAssessments()).resolves.toMatchObject([
      { id: 'assessment-1', title: 'Java Backend' },
    ])
  })

  it('loads a personalized assessment by id', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/assessment-1', () => HttpResponse.json(makeAssessment())),
    )

    await expect(getPersonalizedAssessment('assessment-1')).resolves.toMatchObject({
      id: 'assessment-1',
      shareableToken: 'token-1',
    })
  })

  it('analyzes a job description', async () => {
    let payload: unknown
    server.use(
      http.post('*/api/v1/assessments/personalized/job-description-analysis', async ({ request }) => {
        payload = await request.json()
        return HttpResponse.json(makeJobAnalysis())
      }),
    )

    const analysis = await analyzeJobDescription('Vaga Java com Spring e SQL')

    expect(payload).toEqual({ jobDescription: 'Vaga Java com Spring e SQL' })
    expect(analysis.suggestedKnowledgeAreas).toEqual(['SOFTWARE_DEVELOPMENT', 'DATABASE'])
  })

  it('lists suggested questions with backend-compatible query parameters', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', ({ request }) => {
        const url = new URL(request.url)

        expect(url.searchParams.getAll('knowledgeAreas')).toEqual(['SOFTWARE_DEVELOPMENT', 'DATABASE'])
        expect(url.searchParams.getAll('difficultyLevels')).toEqual(['MEDIUM'])
        expect(url.searchParams.getAll('questionTypes')).toEqual(['MULTIPLE_CHOICE'])
        expect(url.searchParams.getAll('excludeIds')).toEqual(['question-2'])
        expect(url.searchParams.get('page')).toBe('2')
        expect(url.searchParams.get('pageSize')).toBe('5')

        return HttpResponse.json(makeSuggestions())
      }),
    )

    const suggestions = await listSuggestedQuestions({
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
      difficultyLevels: ['MEDIUM'],
      questionTypes: ['MULTIPLE_CHOICE'],
      excludeIds: ['question-2'],
      page: 2,
      pageSize: 5,
    })

    expect(suggestions.questions[0].id).toBe('question-1')
  })

  it('normalizes suggested questions returned by the backend page contract', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', () =>
        HttpResponse.json({
          content: [
            {
              id: 'question-1',
              title: 'Como validar transacoes?',
              description: 'Escolha a alternativa correta.',
              knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
              difficultyLevel: 'MEDIUM',
              score: 0.91,
            },
          ],
          page: 1,
          size: 10,
          totalElements: 1,
          totalPages: 1,
          last: true,
        }),
      ),
    )

    await expect(listSuggestedQuestions({ page: 1, pageSize: 10 })).resolves.toMatchObject({
      questions: [
        {
          id: 'question-1',
          difficultyByCommunity: 'MEDIUM',
        },
      ],
      pageSize: 10,
    })
  })

  it('creates a personalized assessment with a job description snapshot', async () => {
    let payload: unknown
    server.use(
      http.post('*/api/v1/assessments/personalized', async ({ request }) => {
        payload = await request.json()
        return HttpResponse.json(
          { id: 'assessment-1', title: 'Java Backend', shareableToken: 'token-1', status: 'ACTIVE' },
          { status: 201 },
        )
      }),
    )

    const assessment = await createPersonalizedAssessment({
      title: 'Java Backend',
      description: 'Avaliacao para backend',
      questionIds: ['question-1'],
      expirationDate: '2026-06-01T12:00:00',
      maxAttempts: 2,
      evaluationTimerMinutes: 45,
      hardSkillsWeight: 60,
      softSkillsWeight: 20,
      experienceWeight: 20,
      jobDescriptionAnalysis: {
        originalJobDescription: 'Vaga Java com Spring e SQL',
        suggestedKnowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
        suggestedHardSkills: ['Java', 'Spring'],
        suggestedSoftSkills: ['Comunicacao'],
        suggestedHardSkillsWeight: 60,
        suggestedSoftSkillsWeight: 20,
        suggestedExperienceWeight: 20,
      },
    })

    expect(payload).toMatchObject({
      title: 'Java Backend',
      questionIds: ['question-1'],
      hardSkillsWeight: 60,
      softSkillsWeight: 20,
      experienceWeight: 20,
      jobDescriptionAnalysis: {
        originalJobDescription: 'Vaga Java com Spring e SQL',
        suggestedHardSkillsWeight: 60,
      },
    })
    expect(assessment.shareableToken).toBe('token-1')
  })

  it('updates and deletes a personalized assessment', async () => {
    const calls: Record<string, unknown> = {}
    server.use(
      http.patch('*/api/v1/assessments/personalized/assessment-1', async ({ request }) => {
        calls.update = await request.json()
        return HttpResponse.json({
          id: 'assessment-1',
          expirationDate: '2026-07-01T12:00:00',
          maxAttempts: 3,
          status: 'DEACTIVATED',
        })
      }),
      http.delete('*/api/v1/assessments/personalized/assessment-1', () =>
        HttpResponse.json({ id: 'assessment-1', deleted: true }),
      ),
    )

    await updatePersonalizedAssessment('assessment-1', {
      expirationDate: '2026-07-01T12:00:00',
      maxAttempts: 3,
      status: 'DEACTIVATED',
      hardSkillsWeight: 50,
      softSkillsWeight: 20,
      experienceWeight: 30,
    })
    const deleted = await deletePersonalizedAssessment('assessment-1')

    expect(calls.update).toEqual({
      expirationDate: '2026-07-01T12:00:00',
      maxAttempts: 3,
      status: 'DEACTIVATED',
      hardSkillsWeight: 50,
      softSkillsWeight: 20,
      experienceWeight: 30,
    })
    expect(deleted).toEqual({ id: 'assessment-1', deleted: true })
  })

  it('lists attempts for a personalized assessment', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/assessment-1/attempts', () =>
        HttpResponse.json([makeAttemptSummary()]),
      ),
    )

    await expect(listAssessmentAttempts('assessment-1')).resolves.toMatchObject([
      { attemptId: 'attempt-1', candidateName: 'Maria Souza' },
    ])
  })

  it('loads attempt details, generates pre-analysis and evaluates the attempt', async () => {
    const calls: Record<string, unknown> = {}
    server.use(
      http.get('*/api/v1/assessments/attempts/attempt-1', () => HttpResponse.json(makeAttemptDetails())),
      http.post('*/api/v1/assessments/attempts/attempt-1/pre-analysis', () =>
        HttpResponse.json(makePreAnalysis()),
      ),
      http.post('*/api/v1/assessments/attempt-1/evaluate', async ({ request }) => {
        calls.evaluate = await request.json()
        return HttpResponse.json({ attemptId: 'attempt-1', finalStatus: 'APPROVED' })
      }),
    )

    const detail = await getAssessmentAttemptDetails('attempt-1')
    const preAnalysis = await generateAttemptPreAnalysis('attempt-1')
    const evaluation = await evaluateAssessmentAttempt('attempt-1', 'APPROVED')

    expect(detail.attemptId).toBe('attempt-1')
    expect(preAnalysis.analysis.summary).toBe('Bom desempenho tecnico.')
    expect(calls.evaluate).toEqual({ finalStatus: 'APPROVED' })
    expect(evaluation.finalStatus).toBe('APPROVED')
  })

  it('creates a recruiter open question', async () => {
    let payload: unknown
    server.use(
      http.post('*/api/v1/questions/open', async ({ request }) => {
        payload = await request.json()
        return HttpResponse.json({ id: 'question-3', title: 'Explique transacoes em APIs' }, { status: 201 })
      }),
    )

    const question = await createOpenQuestion({
      title: 'Explique transacoes em APIs',
      description: 'Descreva como voce garantiria consistencia em uma API REST.',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
      difficultyByCommunity: 'MEDIUM',
      relevanceByCommunity: 'FOUR',
      guideline: 'Avaliar clareza, consistencia e exemplos praticos.',
      visibility: 'SHARED',
    })

    expect(payload).toMatchObject({
      type: 'OPEN',
      title: 'Explique transacoes em APIs',
      visibility: 'SHARED',
    })
    expect(question.id).toBe('question-3')
  })
})

function makeAssessment() {
  return {
    id: 'assessment-1',
    title: 'Java Backend',
    description: 'Avaliacao para backend',
    creationDate: '2026-05-19T10:00:00',
    evaluationTimerMinutes: 45,
    expirationDate: '2026-06-01T12:00:00',
    totalAttempts: 1,
    maxAttempts: 2,
    shareableToken: 'token-1',
    status: 'ACTIVE',
    createdBy: { id: 'recruiter-1', name: 'Ana Silva', email: 'ana@example.com', role: 'RECRUITER' },
    criteriaWeights: { hardSkillsWeight: 60, softSkillsWeight: 20, experienceWeight: 20 },
    jobDescriptionAnalysis: makeJobAnalysis(),
    questions: [
      {
        id: 'question-1',
        title: 'Como validar transacoes?',
        knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
        difficultyByCommunity: 'MEDIUM',
        submissionDate: '2026-05-18T10:00:00',
      },
    ],
  }
}

function makeJobAnalysis() {
  return {
    originalJobDescription: 'Vaga Java com Spring e SQL',
    suggestedKnowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
    suggestedHardSkills: ['Java', 'Spring', 'SQL'],
    suggestedSoftSkills: ['Comunicacao'],
    suggestedCriteriaWeights: {
      hardSkillsWeight: 60,
      softSkillsWeight: 20,
      experienceWeight: 20,
    },
  }
}

function makeSuggestions() {
  return {
    questions: [
      {
        id: 'question-1',
        title: 'Como validar transacoes?',
        knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
        difficultyByCommunity: 'MEDIUM',
        score: 0.91,
      },
    ],
    page: 2,
    pageSize: 5,
    totalElements: 1,
    totalPages: 1,
    last: true,
  }
}

function makeAttemptSummary() {
  return {
    attemptId: 'attempt-1',
    candidateId: 'candidate-1',
    candidateName: 'Maria Souza',
    candidateEmail: 'maria@example.com',
    assessmentId: 'assessment-1',
    assessmentTitle: 'Java Backend',
    status: 'COMPLETED',
    accuracyRate: 75,
    startedAt: '2026-05-19T11:00:00',
    submittedAt: '2026-05-19T11:30:00',
  }
}

function makeAttemptDetails() {
  return {
    attemptId: 'attempt-1',
    candidate: { id: 'candidate-1', name: 'Maria Souza', email: 'maria@example.com' },
    assessment: { id: 'assessment-1', title: 'Java Backend' },
    status: 'COMPLETED',
    accuracyRate: 75,
    answers: [],
    explainability: {
      correctAnswers: 3,
      totalQuestions: 4,
      approvalThreshold: 70,
    },
  }
}

function makePreAnalysis() {
  return {
    metadata: {
      attemptId: 'attempt-1',
      generatedAt: '2026-05-19T12:00:00',
      ignoredQuestionTypes: ['PROJECT'],
    },
    analysis: {
      summary: 'Bom desempenho tecnico.',
      strengths: ['Conhece Spring'],
      attentionPoints: ['Revisar SQL'],
      finalExplanation: 'Candidato adequado para entrevista tecnica.',
    },
  }
}
