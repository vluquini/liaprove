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
import AdminAssessmentAttemptsView from './AdminAssessmentAttemptsView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/mini-projects/public', component: { template: '<div>Mini projetos</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/admin', component: { template: '<div>Admin</div>' } },
      { path: '/admin/assessments/attempts', component: AdminAssessmentAttemptsView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView() {
  writeStoredSession(makeAuthResponse('ADMIN'))
  const router = makeRouter()
  await router.push('/admin/assessments/attempts')
  await router.isReady()

  const wrapper = mount(AdminAssessmentAttemptsView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AdminAssessmentAttemptsView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('loads attempts and renders candidate, assessment and result data', async () => {
    server.use(
      http.get('*/api/v1/admin/assessments/attempts', () => HttpResponse.json([makeAttempt()])),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Tentativas')
    expect(wrapper.text()).toContain('Carlos Silva')
    expect(wrapper.text()).toContain('carlos.silva@example.com')
    expect(wrapper.text()).toContain('Java Backend')
    expect(wrapper.text()).toContain('Personalizada')
    expect(wrapper.text()).toContain('COMPLETED')
    expect(wrapper.text()).toContain('85%')
    expect(wrapper.text()).toContain('24/05/2026')
  })

  it('submits type, status and date filters', async () => {
    const calls: string[] = []
    server.use(
      http.get('*/api/v1/admin/assessments/attempts', ({ request }) => {
        const url = new URL(request.url)
        calls.push(url.search)
        return HttpResponse.json([makeAttempt()])
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-attempt-filter-type"]').setValue('true')
    await wrapper.get('[data-test="admin-attempt-filter-status"]').setValue('APPROVED')
    await wrapper.get('[data-test="admin-attempt-filter-start"]').setValue('2026-05-01T00:00:00')
    await wrapper.get('[data-test="admin-attempt-filter-end"]').setValue('2026-05-31T23:59:59')
    await wrapper.get('[data-test="admin-attempt-apply-filters"]').trigger('click')
    await flushPromises()

    const lastCall = calls[calls.length - 1]
    expect(lastCall).toContain('isPersonalized=true')
    expect(lastCall).toContain('statuses=APPROVED')
    expect(lastCall).toContain('startDate=2026-05-01T00%3A00%3A00')
    expect(lastCall).toContain('endDate=2026-05-31T23%3A59%3A59')
  })

  it('shows empty and error states', async () => {
    server.use(http.get('*/api/v1/admin/assessments/attempts', () => HttpResponse.json([])))

    const empty = await mountView()
    expect(empty.wrapper.text()).toContain('Nenhuma tentativa encontrada.')

    empty.wrapper.unmount()
    localStorage.clear()
    setActivePinia(createPinia())
    server.use(
      http.get('*/api/v1/admin/assessments/attempts', () =>
        HttpResponse.json({ message: 'Falha ao carregar tentativas.' }, { status: 500 }),
      ),
    )

    const error = await mountView()
    expect(error.wrapper.text()).toContain('Falha ao carregar tentativas.')
  })
})

function makeAttempt() {
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
      criteriaWeights: {
        hardSkillsWeight: 60,
        softSkillsWeight: 20,
        experienceWeight: 20,
      },
      jobDescriptionAnalysis: null,
    },
    candidate: {
      id: 'candidate-1',
      name: 'Carlos Silva',
      email: 'carlos.silva@example.com',
      occupation: 'Backend Developer',
      bio: null,
      experienceLevel: 'SENIOR',
      hardSkills: ['Java'],
      softSkills: ['Comunicação'],
      role: 'PROFESSIONAL',
      companyName: null,
      companyEmail: null,
    },
  }
}
