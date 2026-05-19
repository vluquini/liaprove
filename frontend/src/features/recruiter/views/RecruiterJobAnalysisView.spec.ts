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
import RecruiterJobAnalysisView from './RecruiterJobAnalysisView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/job-analysis', component: RecruiterJobAnalysisView },
      { path: '/recruiter/assessments/new', component: { template: '<div>Criar avaliacao</div>' } },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountJobAnalysis() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter/job-analysis')
  await router.isReady()

  const wrapper = mount(RecruiterJobAnalysisView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterJobAnalysisView', () => {
  beforeEach(() => {
    sessionStorage.clear()
    setActivePinia(createPinia())
  })

  it('validates non-empty job description', async () => {
    const { wrapper } = await mountJobAnalysis()

    await wrapper.get('[data-test="analyze-job-description"]').trigger('click')

    expect(wrapper.text()).toContain('Informe a descrição da vaga')
  })

  it('posts job description, renders analysis and persists it for assessment creation', async () => {
    let payload: unknown
    server.use(
      http.post('*/api/v1/assessments/personalized/job-description-analysis', async ({ request }) => {
        payload = await request.json()
        return HttpResponse.json(makeAnalysis())
      }),
    )

    const { wrapper, router } = await mountJobAnalysis()

    await wrapper.get('[data-test="job-description"]').setValue('Vaga Java com Spring e SQL')
    await wrapper.get('[data-test="analyze-job-description"]').trigger('click')
    await flushPromises()

    expect(payload).toEqual({ jobDescription: 'Vaga Java com Spring e SQL' })
    expect(wrapper.text()).toContain('SOFTWARE_DEVELOPMENT')
    expect(wrapper.text()).toContain('Java')
    expect(wrapper.text()).toContain('Comunicacao')
    expect(wrapper.text()).toContain('60%')

    await wrapper.get('[data-test="use-analysis"]').trigger('click')
    await flushPromises()

    const saved = JSON.parse(sessionStorage.getItem('liaprove:recruiter:last-job-analysis') ?? '{}')
    expect(saved.originalJobDescription).toBe('Vaga Java com Spring e SQL')
    expect(router.currentRoute.value.path).toBe('/recruiter/assessments/new')
  })
})

function makeAnalysis() {
  return {
    originalJobDescription: 'Vaga Java com Spring e SQL',
    suggestedKnowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
    suggestedHardSkills: ['Java', 'Spring', 'SQL'],
    suggestedSoftSkills: ['Comunicacao', 'Colaboracao'],
    suggestedCriteriaWeights: {
      hardSkillsWeight: 60,
      softSkillsWeight: 20,
      experienceWeight: 20,
    },
  }
}
