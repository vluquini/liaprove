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
import RecruiterHomeView from './RecruiterHomeView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: RecruiterHomeView },
      { path: '/recruiter/job-analysis', component: { template: '<div>Analise</div>' } },
      { path: '/recruiter/assessments/new', component: { template: '<div>Criar avaliacao</div>' } },
      { path: '/recruiter/assessments/:assessmentId', component: { template: '<div>Detalhe</div>' } },
      { path: '/recruiter/questions/open/new', component: { template: '<div>Questao aberta</div>' } },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountHome() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter')
  await router.isReady()

  const wrapper = mount(RecruiterHomeView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterHomeView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads personalized assessments and shows operational links', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized', () => HttpResponse.json([makeAssessment()])),
    )

    const { wrapper } = await mountHome()

    expect(wrapper.text()).toContain('Área do recrutador')
    expect(wrapper.text()).toContain('Java Backend')
    expect(wrapper.text()).toContain('ACTIVE')
    expect(wrapper.text()).toContain('1/2')
    expect(wrapper.get('a[href="/recruiter/job-analysis"]').text()).toContain('Analisar vaga')
    expect(wrapper.get('a[href="/recruiter/assessments/new"]').text()).toContain('Criar avaliação')
    expect(wrapper.get('a[href="/recruiter/questions/open/new"]').text()).toContain('Criar questão aberta')
    expect(wrapper.get('a[href="/recruiter/assessments/assessment-1"]').text()).toContain('Detalhes')
  })

  it('shows an empty state when there are no personalized assessments', async () => {
    server.use(http.get('*/api/v1/assessments/personalized', () => HttpResponse.json([])))

    const { wrapper } = await mountHome()

    expect(wrapper.text()).toContain('Nenhuma avaliação personalizada criada')
    expect(wrapper.text()).toContain('Criar avaliação')
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
    jobDescriptionAnalysis: null,
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
