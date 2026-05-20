import { flushPromises, mount, type VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import { writeStoredSession } from '@/shared/utils/session'
import RecruiterAssessmentCreateView from './RecruiterAssessmentCreateView.vue'

const LAST_JOB_ANALYSIS_KEY = 'liaprove:recruiter:last-job-analysis'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/assessments/new', component: RecruiterAssessmentCreateView },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountCreateView() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter/assessments/new')
  await router.isReady()

  const wrapper = mount(RecruiterAssessmentCreateView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

async function fillValidAssessmentData(wrapper: VueWrapper) {
  await wrapper.get('[data-test="assessment-title"]').setValue('Java Backend')
  await wrapper.get('[data-test="assessment-description"]').setValue('Avaliacao para backend Java e Spring')
  await wrapper.get('[data-test="assessment-expiration"]').setValue('2026-06-01T12:00')
  await wrapper.get('[data-test="assessment-max-attempts"]').setValue('2')
  await wrapper.get('[data-test="assessment-timer"]').setValue('45')
  await wrapper.get('[data-test="weight-hard"]').setValue('60')
  await wrapper.get('[data-test="weight-soft"]').setValue('20')
  await wrapper.get('[data-test="weight-experience"]').setValue('20')
}

describe('RecruiterAssessmentCreateView', () => {
  beforeEach(() => {
    sessionStorage.clear()
    setActivePinia(createPinia())
  })

  it('validates required fields and criteria weights before creation', async () => {
    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', () => HttpResponse.json(makeSuggestions())),
    )

    const { wrapper } = await mountCreateView()

    await wrapper.get('[data-test="create-assessment"]').trigger('click')

    expect(wrapper.text()).toContain('Informe título, descrição, expiração futura, tentativas, tempo e ao menos uma questão.')

    await fillValidAssessmentData(wrapper)
    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="select-question-question-1"]').setValue(true)
    await wrapper.get('[data-test="weight-experience"]').setValue('10')
    await wrapper.get('[data-test="create-assessment"]').trigger('click')

    expect(wrapper.text()).toContain('Os pesos devem somar 100.')
  })

  it('loads saved job analysis and fetches suggestions with selected criteria', async () => {
    sessionStorage.setItem(LAST_JOB_ANALYSIS_KEY, JSON.stringify(makeAnalysis()))
    let suggestionUrl: URL | undefined

    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', ({ request }) => {
        suggestionUrl = new URL(request.url)
        return HttpResponse.json(makeSuggestions())
      }),
    )

    const { wrapper } = await mountCreateView()

    expect(wrapper.text()).toContain('Vaga Java com Spring e SQL')
    expect(wrapper.text()).toContain('Java')
    expect(wrapper.text()).toContain('60%')

    await wrapper.get('[data-test="difficulty-MEDIUM"]').setValue(true)
    await wrapper.get('[data-test="question-type-MULTIPLE_CHOICE"]').setValue(true)
    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()

    expect(suggestionUrl?.searchParams.getAll('knowledgeAreas')).toEqual(['SOFTWARE_DEVELOPMENT', 'DATABASE'])
    expect(suggestionUrl?.searchParams.getAll('difficultyLevels')).toEqual(['MEDIUM'])
    expect(suggestionUrl?.searchParams.getAll('questionTypes')).toEqual(['MULTIPLE_CHOICE'])
    expect(wrapper.text()).toContain('Como validar transacoes?')

    await wrapper.get('[data-test="select-question-question-1"]').setValue(true)
    expect(wrapper.text()).toContain('1 questão selecionada')
  })

  it('keeps selected questions visible when loading more suggestions', async () => {
    let requestCount = 0
    const suggestionUrls: URL[] = []

    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', ({ request }) => {
        suggestionUrls.push(new URL(request.url))
        requestCount += 1

        return HttpResponse.json(requestCount === 1
          ? makeSuggestions()
          : makeSuggestions([
              {
                id: 'question-2',
                title: 'Como proteger APIs?',
                description: 'Escolha a alternativa correta.',
                knowledgeAreas: ['CYBERSECURITY'],
                difficultyLevel: 'EASY',
                score: 0.84,
              },
            ]))
      }),
    )

    const { wrapper } = await mountCreateView()

    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="select-question-question-1"]').setValue(true)

    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()

    expect(suggestionUrls[1]?.searchParams.getAll('excludeIds')).toEqual(['question-1'])
    expect(wrapper.text()).toContain('Questões selecionadas')
    expect(wrapper.text()).toContain('Como validar transacoes?')
    expect(wrapper.text()).toContain('Novas sugestões')
    expect(wrapper.text()).toContain('Como proteger APIs?')
    expect(wrapper.find('[data-test="selected-question-question-1"]').exists()).toBe(true)
    expect(wrapper.find('[data-test="select-question-question-1"]').exists()).toBe(false)
  })

  it('limits the assessment to ten selected questions', async () => {
    let requestCount = 0

    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', () => {
        requestCount += 1

        return HttpResponse.json(requestCount === 1
          ? makeSuggestions(Array.from({ length: 10 }, (_, index) => ({
              id: `question-${index + 1}`,
              title: `Questao ${index + 1}`,
              description: 'Escolha a alternativa correta.',
              knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
              difficultyLevel: 'MEDIUM',
              score: 0.9 - (index / 100),
            })))
          : makeSuggestions([
              {
                id: 'question-11',
                title: 'Questao 11',
                description: 'Escolha a alternativa correta.',
                knowledgeAreas: ['DATABASE'],
                difficultyLevel: 'MEDIUM',
                score: 0.7,
              },
            ]))
      }),
    )

    const { wrapper } = await mountCreateView()

    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()

    for (let index = 1; index <= 10; index += 1) {
      await wrapper.get(`[data-test="select-question-question-${index}"]`).setValue(true)
    }

    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('10 questões selecionadas de 10')
    expect(wrapper.get('[data-test="select-question-question-11"]').attributes('disabled')).toBeDefined()
  })

  it('creates personalized assessment and displays shareable link', async () => {
    sessionStorage.setItem(LAST_JOB_ANALYSIS_KEY, JSON.stringify(makeAnalysis()))
    let createPayload: unknown

    server.use(
      http.get('*/api/v1/assessments/personalized/suggestions', () => HttpResponse.json(makeSuggestions())),
      http.post('*/api/v1/assessments/personalized', async ({ request }) => {
        createPayload = await request.json()
        return HttpResponse.json(
          { id: 'assessment-1', title: 'Java Backend', shareableToken: 'token-1', status: 'ACTIVE' },
          { status: 201 },
        )
      }),
    )

    const { wrapper } = await mountCreateView()

    await fillValidAssessmentData(wrapper)
    await wrapper.get('[data-test="load-suggestions"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="select-question-question-1"]').setValue(true)
    await wrapper.get('[data-test="create-assessment"]').trigger('click')
    await flushPromises()

    expect(createPayload).toMatchObject({
      title: 'Java Backend',
      description: 'Avaliacao para backend Java e Spring',
      questionIds: ['question-1'],
      expirationDate: '2026-06-01T12:00',
      maxAttempts: 2,
      evaluationTimerMinutes: 45,
      hardSkillsWeight: 60,
      softSkillsWeight: 20,
      experienceWeight: 20,
      jobDescriptionAnalysis: {
        originalJobDescription: 'Vaga Java com Spring e SQL',
        suggestedHardSkillsWeight: 60,
      },
    })
    expect(wrapper.text()).toContain('/assessments/personalized/token-1/start')
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

function makeSuggestions(content = [
  {
    id: 'question-1',
    title: 'Como validar transacoes?',
    description: 'Escolha a alternativa correta.',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    difficultyLevel: 'MEDIUM',
    score: 0.91,
  },
]) {
  return {
    content,
    page: 1,
    size: 10,
    totalElements: content.length,
    totalPages: 1,
    last: true,
  }
}
