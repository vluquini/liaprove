import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import { writeStoredSession } from '@/shared/utils/session'
import RecruiterAttemptDetailView from './RecruiterAttemptDetailView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/attempts/:attemptId', component: RecruiterAttemptDetailView },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountAttempt() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter/attempts/attempt-1')
  await router.isReady()

  const wrapper = mount(RecruiterAttemptDetailView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterAttemptDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads attempt detail and renders candidate, explainability, questions and answers', async () => {
    server.use(
      http.get('*/api/v1/assessments/attempts/attempt-1', () => HttpResponse.json(makeAttemptDetails())),
    )

    const { wrapper } = await mountAttempt()

    expect(wrapper.text()).toContain('Maria Souza')
    expect(wrapper.text()).toContain('Java Backend')
    expect(wrapper.text()).toContain('COMPLETED')
    expect(wrapper.text()).toContain('4 questões')
    expect(wrapper.text()).toContain('3 respondidas')
    expect(wrapper.text()).toContain('Como validar transacoes?')
    expect(wrapper.text()).toContain('Usar transacoes no caso de uso.')
    expect(wrapper.text()).toContain('Explique rollback')
    expect(wrapper.text()).toContain('O rollback desfaz alterações quando ocorre erro.')
  })

  it('generates pre-analysis and renders ignored project metadata', async () => {
    server.use(
      http.get('*/api/v1/assessments/attempts/attempt-1', () => HttpResponse.json(makeAttemptDetails())),
      http.post('*/api/v1/assessments/attempts/attempt-1/pre-analysis', () =>
        HttpResponse.json(makePreAnalysis()),
      ),
    )

    const { wrapper } = await mountAttempt()

    await wrapper.get('[data-test="generate-pre-analysis"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Bom desempenho tecnico.')
    expect(wrapper.text()).toContain('Conhece Spring')
    expect(wrapper.text()).toContain('Revisar SQL')
    expect(wrapper.text()).toContain('Candidato adequado para entrevista tecnica.')
    expect(wrapper.text()).toContain('PROJECT')
  })

  it('posts approved and failed final statuses', async () => {
    const payloads: unknown[] = []
    server.use(
      http.get('*/api/v1/assessments/attempts/attempt-1', () => HttpResponse.json(makeAttemptDetails())),
      http.post('*/api/v1/assessments/attempt-1/evaluate', async ({ request }) => {
        payloads.push(await request.json())
        return HttpResponse.json({
          attemptId: 'attempt-1',
          status: payloads.length === 1 ? 'APPROVED' : 'FAILED',
          message: 'Avaliação final registrada.',
        })
      }),
    )

    const { wrapper } = await mountAttempt()

    await wrapper.get('[data-test="approve-candidate"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="fail-candidate"]').trigger('click')
    await flushPromises()

    expect(payloads).toEqual([{ finalStatus: 'APPROVED' }, { finalStatus: 'FAILED' }])
    expect(wrapper.text()).toContain('Avaliação final registrada.')
  })

  it('handles backend error', async () => {
    server.use(
      http.get('*/api/v1/assessments/attempts/attempt-1', () =>
        HttpResponse.json({ message: 'Attempt not found' }, { status: 404 }),
      ),
    )

    const { wrapper } = await mountAttempt()

    expect(wrapper.text()).toContain('Attempt not found')
  })
})

function makeAttemptDetails() {
  return {
    attemptId: 'attempt-1',
    status: 'COMPLETED',
    accuracyRate: 75,
    startedAt: '2026-05-19T11:00:00',
    finishedAt: '2026-05-19T11:30:00',
    assessment: {
      id: 'assessment-1',
      title: 'Java Backend',
      description: 'Avaliacao para backend',
      evaluationTimerMinutes: 45,
      criteriaWeights: { hardSkillsWeight: 60, softSkillsWeight: 20, experienceWeight: 20 },
      jobDescriptionAnalysis: null,
    },
    candidate: {
      id: 'candidate-1',
      name: 'Maria Souza',
      email: 'maria@example.com',
      role: 'PROFESSIONAL',
      experienceLevel: 'JUNIOR',
      hardSkills: ['Java'],
      softSkills: ['Comunicacao'],
    },
    explainability: {
      totalQuestions: 4,
      answeredQuestions: 3,
      multipleChoiceQuestions: 2,
      openQuestions: 1,
      projectQuestions: 1,
      candidateExperienceLevel: 'JUNIOR',
      candidateHardSkills: ['Java'],
      candidateSoftSkills: ['Comunicacao'],
      criteriaWeights: { hardSkillsWeight: 60, softSkillsWeight: 20, experienceWeight: 20 },
    },
    questions: [
      {
        id: 'question-1',
        title: 'Como validar transacoes?',
        description: 'Escolha a alternativa correta.',
        guideline: null,
        alternatives: [{ id: 'alt-1', text: 'Usar transacoes no caso de uso.' }],
        answer: { questionId: 'question-1', selectedAlternativeId: 'alt-1', projectUrl: null, textResponse: null },
      },
      {
        id: 'question-2',
        title: 'Explique rollback',
        description: 'Responda com suas palavras.',
        guideline: 'Avaliar clareza e completude.',
        alternatives: [],
        answer: {
          questionId: 'question-2',
          selectedAlternativeId: null,
          projectUrl: null,
          textResponse: 'O rollback desfaz alterações quando ocorre erro.',
        },
      },
    ],
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
