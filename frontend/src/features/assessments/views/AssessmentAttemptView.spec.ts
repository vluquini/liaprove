import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import { writeStoredSession } from '@/shared/utils/session'
import { readAssessmentResult, saveCurrentAssessmentAttempt } from '../utils/assessmentSession'
import type { AssessmentAttemptResponse } from '../services/assessmentService'
import AssessmentAttemptView from './AssessmentAttemptView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Inicio</div>' } },
      { path: '/assessments/attempts/:attemptId', component: AssessmentAttemptView },
      { path: '/assessments/attempts/:attemptId/result', component: { template: '<div>Resultado</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountAttempt(path: string) {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push(path)
  await router.isReady()

  const wrapper = mount(AssessmentAttemptView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AssessmentAttemptView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  afterEach(() => {
    sessionStorage.clear()
  })

  it('renders a persisted attempt and submits selected alternatives', async () => {
    let body: unknown
    saveCurrentAssessmentAttempt(makeAttempt())
    server.use(
      http.post('*/api/v1/assessments/attempt-1/submit', async ({ request }) => {
        body = await request.json()
        return HttpResponse.json({
          status: 'APPROVED',
          accuracyRate: 80,
          certificateUrl: '/certificates/CERT-123',
          message: 'Aprovado',
        })
      }),
    )

    const { wrapper, router } = await mountAttempt('/assessments/attempts/attempt-1')

    expect(wrapper.text()).toContain('Avaliação de Desenvolvimento de Software')
    expect(wrapper.text()).toContain('Como validar transacoes?')

    await wrapper.get('[data-test="answer-question-1-alt-1"]').setValue(true)
    await wrapper.get('[data-test="submit-assessment"]').trigger('click')
    await flushPromises()

    expect(body).toEqual({
      answers: [{ questionId: 'question-1', selectedAlternativeId: 'alt-1', projectUrl: null, textResponse: null }],
    })
    expect(readAssessmentResult('attempt-1')?.status).toBe('APPROVED')
    expect(router.currentRoute.value.path).toBe('/assessments/attempts/attempt-1/result')
  })

  it('asks the user to start a new assessment when local attempt is missing', async () => {
    const { wrapper } = await mountAttempt('/assessments/attempts/attempt-1')
    expect(wrapper.text()).toContain('Não foi possível carregar esta tentativa neste dispositivo.')
  })

  it('requires every multiple-choice question to be answered', async () => {
    saveCurrentAssessmentAttempt(makeAttempt())
    const { wrapper } = await mountAttempt('/assessments/attempts/attempt-1')

    await wrapper.get('[data-test="submit-assessment"]').trigger('click')

    expect(wrapper.text()).toContain('Responda todas as questões antes de enviar.')
  })
})

function makeAttempt(): AssessmentAttemptResponse {
  return {
    attemptId: 'attempt-1',
    assessmentTitle: 'Avaliação de SOFTWARE_DEVELOPMENT',
    startedAt: '2026-05-15T10:00:00',
    evaluationTimerMinutes: 30,
    questions: [
      {
        id: 'question-1',
        title: 'Como validar transacoes?',
        description: 'Escolha a alternativa correta.',
        alternatives: [
          { id: 'alt-1', text: 'Usar transacoes no caso de uso.' },
          { id: 'alt-2', text: 'Ignorar rollback.' },
        ],
      },
    ],
  }
}
