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
import RecruiterAssessmentAttemptsView from './RecruiterAssessmentAttemptsView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/assessments/:assessmentId/attempts', component: RecruiterAssessmentAttemptsView },
      { path: '/recruiter/attempts/:attemptId', component: { template: '<div>Tentativa</div>' } },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountAttempts() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter/assessments/assessment-1/attempts')
  await router.isReady()

  const wrapper = mount(RecruiterAssessmentAttemptsView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterAssessmentAttemptsView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads attempts and links each row to the attempt detail', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/assessment-1/attempts', () =>
        HttpResponse.json([makeAttempt()]),
      ),
    )

    const { wrapper } = await mountAttempts()

    expect(wrapper.text()).toContain('Maria Souza')
    expect(wrapper.text()).toContain('COMPLETED')
    expect(wrapper.text()).toContain('75%')
    expect(wrapper.get('a[href="/recruiter/attempts/attempt-1"]').text()).toContain('Abrir')
  })

  it('shows empty state when there are no attempts', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/assessment-1/attempts', () => HttpResponse.json([])),
    )

    const { wrapper } = await mountAttempts()

    expect(wrapper.text()).toContain('Nenhuma tentativa registrada')
  })
})

function makeAttempt() {
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
