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
import AssessmentStartView from './AssessmentStartView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: AssessmentStartView },
      { path: '/assessments/attempts/:attemptId', component: { template: '<div>Tentativa</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountStart() {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push('/assessments/start')
  await router.isReady()

  const wrapper = mount(AssessmentStartView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AssessmentStartView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  afterEach(() => {
    sessionStorage.clear()
  })

  it('starts a multiple-choice assessment and redirects to the attempt', async () => {
    let body: unknown
    server.use(
      http.post('*/api/v1/assessments/start-system', async ({ request }) => {
        body = await request.json()
        return HttpResponse.json(makeAttempt(), { status: 201 })
      }),
    )

    const { wrapper, router } = await mountStart()

    await wrapper.get('[data-test="knowledge-area"]').setValue('SOFTWARE_DEVELOPMENT')
    await wrapper.get('[data-test="difficulty-level"]').setValue('MEDIUM')
    await wrapper.get('[data-test="system-assessment-type"]').setValue('MULTIPLE_CHOICE')
    await wrapper.get('[data-test="start-assessment"]').trigger('click')
    await flushPromises()

    expect(body).toEqual({
      knowledgeArea: 'SOFTWARE_DEVELOPMENT',
      difficultyLevel: 'MEDIUM',
      type: 'MULTIPLE_CHOICE',
    })
    expect(router.currentRoute.value.path).toBe('/assessments/attempts/attempt-1')
    expect(readCurrentAssessmentAttempt('attempt-1')?.questions).toHaveLength(1)
  })

  it('shows backend errors when starting fails', async () => {
    server.use(
      http.post('*/api/v1/assessments/start-system', () =>
        HttpResponse.json({ message: 'Nao ha questoes suficientes para a avaliacao.' }, { status: 409 }),
      ),
    )

    const { wrapper } = await mountStart()

    await wrapper.get('[data-test="start-assessment"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Nao ha questoes suficientes para a avaliacao.')
  })

  it('renders assessment options with pt-BR accents', async () => {
    const { wrapper } = await mountStart()

    expect(wrapper.text()).toContain('Iniciar avaliação')
    expect(wrapper.text()).toContain('Área de conhecimento')
    expect(wrapper.text()).toContain('Cibersegurança')
    expect(wrapper.text()).toContain('Inteligência Artificial')
    expect(wrapper.text()).toContain('Fácil')
    expect(wrapper.text()).toContain('Média')
    expect(wrapper.text()).toContain('Difícil')
    expect(wrapper.text()).toContain('Múltipla escolha')
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
