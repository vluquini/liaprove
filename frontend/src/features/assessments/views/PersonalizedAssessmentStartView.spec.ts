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
import { readCurrentAssessmentAttempt } from '../utils/assessmentSession'
import PersonalizedAssessmentStartView from './PersonalizedAssessmentStartView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Iniciar</div>' } },
      { path: '/assessments/personalized/:token/start', component: PersonalizedAssessmentStartView },
      { path: '/assessments/attempts/:attemptId', component: { template: '<div>Tentativa</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountStart(token = 'token-1') {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push(`/assessments/personalized/${token}/start`)
  await router.isReady()

  const wrapper = mount(PersonalizedAssessmentStartView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('PersonalizedAssessmentStartView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  afterEach(() => {
    sessionStorage.clear()
  })

  it('starts a personalized assessment using the route token and redirects to the attempt', async () => {
    server.use(
      http.post('*/api/v1/assessments/start-personalized/token-1', () =>
        HttpResponse.json(makeAttempt(), { status: 201 }),
      ),
    )

    const { wrapper, router } = await mountStart()

    expect(wrapper.text()).not.toContain('Como validar transacoes?')

    await wrapper.get('[data-test="start-personalized-assessment"]').trigger('click')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/assessments/attempts/attempt-1')
    expect(readCurrentAssessmentAttempt('attempt-1')?.questions).toHaveLength(1)
  })

  it('shows an error when the token is invalid', async () => {
    server.use(
      http.post('*/api/v1/assessments/start-personalized/invalid-token', () =>
        HttpResponse.json({ message: 'Token invalido.' }, { status: 404 }),
      ),
    )

    const { wrapper, router } = await mountStart('invalid-token')

    await wrapper.get('[data-test="start-personalized-assessment"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Token invalido.')
    expect(router.currentRoute.value.path).toBe('/assessments/personalized/invalid-token/start')
  })
})

function makeAttempt() {
  return {
    attemptId: 'attempt-1',
    assessmentTitle: 'Avaliacao Java',
    startedAt: '2026-05-15T10:00:00',
    evaluationTimerMinutes: 30,
    questions: [
      {
        id: 'question-1',
        title: 'Como validar transacoes?',
        description: 'Escolha a alternativa correta.',
        alternatives: [{ id: 'alt-1', text: 'Usar transacoes no caso de uso.' }],
      },
    ],
  }
}
