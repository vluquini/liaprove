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
import RecruiterAssessmentDetailView from './RecruiterAssessmentDetailView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/assessments/:assessmentId', component: RecruiterAssessmentDetailView },
      { path: '/recruiter/assessments/:assessmentId/edit', component: { template: '<div>Editar</div>' } },
      { path: '/recruiter/assessments/:assessmentId/attempts', component: { template: '<div>Tentativas</div>' } },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountDetail(path = '/recruiter/assessments/assessment-1') {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push(path)
  await router.isReady()

  const wrapper = mount(RecruiterAssessmentDetailView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterAssessmentDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads assessment detail and shows share link, questions, weights and job analysis', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/assessment-1', () => HttpResponse.json(makeAssessment())),
    )

    const { wrapper } = await mountDetail()

    expect(wrapper.text()).toContain('Java Backend')
    expect(wrapper.text()).toContain('/assessments/personalized/token-1/start')
    expect(wrapper.text()).toContain('Como validar transacoes?')
    expect(wrapper.text()).toContain('60%')
    expect(wrapper.text()).toContain('Vaga Java com Spring e SQL')
    expect(wrapper.get('a[href="/recruiter/assessments/assessment-1/attempts"]').text()).toContain('Tentativas')
  })

  it('shows an error state when assessment cannot be loaded', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/missing', () =>
        HttpResponse.json({ message: 'Assessment not found' }, { status: 404 }),
      ),
    )

    const { wrapper } = await mountDetail('/recruiter/assessments/missing')

    expect(wrapper.text()).toContain('Assessment not found')
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
    jobDescriptionAnalysis: {
      originalJobDescription: 'Vaga Java com Spring e SQL',
      suggestedKnowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
      suggestedHardSkills: ['Java', 'Spring'],
      suggestedSoftSkills: ['Comunicacao'],
      suggestedCriteriaWeights: { hardSkillsWeight: 60, softSkillsWeight: 20, experienceWeight: 20 },
    },
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
