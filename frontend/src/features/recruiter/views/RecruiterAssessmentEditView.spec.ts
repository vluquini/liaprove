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
import RecruiterAssessmentEditView from './RecruiterAssessmentEditView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/assessments/:assessmentId', component: { template: '<div>Detalhe</div>' } },
      { path: '/recruiter/assessments/:assessmentId/edit', component: RecruiterAssessmentEditView },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountEdit() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter/assessments/assessment-1/edit')
  await router.isReady()

  const wrapper = mount(RecruiterAssessmentEditView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterAssessmentEditView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads detail before editing and updates supported fields', async () => {
    let updatePayload: unknown
    server.use(
      http.get('*/api/v1/assessments/personalized/assessment-1', () => HttpResponse.json(makeAssessment())),
      http.patch('*/api/v1/assessments/personalized/assessment-1', async ({ request }) => {
        updatePayload = await request.json()
        return HttpResponse.json({
          assessmentId: 'assessment-1',
          expirationDate: '2026-07-01T12:00:00',
          maxAttempts: 3,
          status: 'DEACTIVATED',
          criteriaWeights: { hardSkillsWeight: 50, softSkillsWeight: 20, experienceWeight: 30 },
        })
      }),
    )

    const { wrapper, router } = await mountEdit()

    expect(wrapper.text()).toContain('Java Backend')

    await wrapper.get('[data-test="edit-expiration"]').setValue('2026-07-01T12:00')
    await wrapper.get('[data-test="edit-max-attempts"]').setValue('3')
    await wrapper.get('[data-test="edit-status"]').setValue('DEACTIVATED')
    await wrapper.get('[data-test="edit-weight-hard"]').setValue('50')
    await wrapper.get('[data-test="edit-weight-soft"]').setValue('20')
    await wrapper.get('[data-test="edit-weight-experience"]').setValue('30')
    await wrapper.get('[data-test="save-assessment"]').trigger('click')
    await flushPromises()

    expect(updatePayload).toEqual({
      expirationDate: '2026-07-01T12:00',
      maxAttempts: 3,
      status: 'DEACTIVATED',
      hardSkillsWeight: 50,
      softSkillsWeight: 20,
      experienceWeight: 30,
    })
    expect(router.currentRoute.value.path).toBe('/recruiter/assessments/assessment-1')
  })

  it('validates criteria weights sum before updating', async () => {
    server.use(http.get('*/api/v1/assessments/personalized/assessment-1', () => HttpResponse.json(makeAssessment())))

    const { wrapper } = await mountEdit()

    await wrapper.get('[data-test="edit-weight-experience"]').setValue('10')
    await wrapper.get('[data-test="save-assessment"]').trigger('click')

    expect(wrapper.text()).toContain('Os pesos devem somar 100.')
  })
})

function makeAssessment() {
  return {
    id: 'assessment-1',
    title: 'Java Backend',
    description: 'Avaliacao para backend Java e Spring',
    creationDate: '2026-05-19T10:00:00',
    evaluationTimerMinutes: 45,
    expirationDate: '2026-06-01T12:00:00',
    totalAttempts: 1,
    maxAttempts: 2,
    shareableToken: 'token-1',
    status: 'ACTIVE',
    createdBy: { id: 'recruiter-1', name: 'Ana Silva', email: 'ana@example.com', role: 'RECRUITER' },
    criteriaWeights: { hardSkillsWeight: 60, softSkillsWeight: 20, experienceWeight: 20 },
    jobDescriptionAnalysis: null,
    questions: [],
  }
}
