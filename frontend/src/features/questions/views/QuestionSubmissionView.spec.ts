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
import QuestionSubmissionView from './QuestionSubmissionView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/questions/new', component: QuestionSubmissionView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountSubmission() {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push('/questions/new')
  await router.isReady()

  const wrapper = mount(QuestionSubmissionView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

async function fillValidQuestionForm(wrapper: VueWrapper) {
  await wrapper.get('[data-test="question-title"]').setValue('Como validar transacoes em APIs REST?')
  await wrapper
    .get('[data-test="question-description"]')
    .setValue('Qual abordagem melhora a consistencia de dados em uma API REST com banco relacional?')
  await wrapper.get('[data-test="area-SOFTWARE_DEVELOPMENT"]').setValue(true)
  await wrapper.get('[data-test="alternative-0"]').setValue('Usar transacoes no caso de uso.')
  await wrapper.get('[data-test="alternative-1"]').setValue('Ignorar rollback em erros.')
  await wrapper.get('[data-test="alternative-2"]').setValue('Persistir parcialmente os dados.')
}

async function fillValidProjectQuestionForm(wrapper: VueWrapper) {
  await wrapper.get('[data-test="question-type-PROJECT"]').trigger('click')
  await wrapper.get('[data-test="question-title"]').setValue('Mini-projeto para uma API REST versionada')
  await wrapper
    .get('[data-test="question-description"]')
    .setValue('Implemente uma API REST com validacao de dados e persistencia transacional.')
  await wrapper.get('[data-test="area-SOFTWARE_DEVELOPMENT"]').setValue(true)
}

describe('QuestionSubmissionView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('validates required fields before pre-analysis', async () => {
    const { wrapper } = await mountSubmission()

    await wrapper.get('[data-test="pre-analyze-question"]').trigger('click')

    expect(wrapper.text()).toContain('Informe título, descrição, área, dificuldade, relevância e 3 alternativas.')
  })

  it('runs pre-analysis and submits accepted suggestions', async () => {
    let preAnalysisBody: unknown
    let submitBody: unknown

    server.use(
      http.post('*/api/v1/questions/pre-analysis', async ({ request }) => {
        preAnalysisBody = await request.json()
        return HttpResponse.json({
          languageSuggestions: ['Deixar o enunciado mais direto.'],
          biasOrAmbiguityWarnings: ['Evitar termo ambíguo.'],
          distractorSuggestions: ['Melhorar alternativa incorreta.'],
          difficultyLevelByLLM: 'MEDIUM',
          topicConsistencyNotes: ['Tema coerente com desenvolvimento.'],
        })
      }),
      http.post('*/api/v1/questions', async ({ request }) => {
        submitBody = await request.json()
        return HttpResponse.json({ id: 'question-2', title: 'Nova questão', status: 'VOTING' }, { status: 201 })
      }),
    )

    const { wrapper, router } = await mountSubmission()

    await fillValidQuestionForm(wrapper)
    await wrapper.get('[data-test="pre-analyze-question"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Deixar o enunciado mais direto.')

    await wrapper.get('[data-test="accept-language-0"]').setValue(true)
    await wrapper.get('[data-test="submit-question"]').trigger('click')
    await flushPromises()

    expect(preAnalysisBody).toMatchObject({
      type: 'MULTIPLE_CHOICE',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    })
    expect(submitBody).toMatchObject({
      type: 'MULTIPLE_CHOICE',
      acceptedLanguageSuggestions: ['Deixar o enunciado mais direto.'],
    })
    expect(router.currentRoute.value.path).toBe('/questions/voting')
  })

  it('pre-analyzes and submits mini projects without alternatives', async () => {
    let preAnalysisBody: unknown
    let submitBody: unknown

    server.use(
      http.post('*/api/v1/questions/pre-analysis', async ({ request }) => {
        preAnalysisBody = await request.json()
        return HttpResponse.json({
          languageSuggestions: [],
          biasOrAmbiguityWarnings: [],
          distractorSuggestions: [],
          difficultyLevelByLLM: null,
          topicConsistencyNotes: [],
        })
      }),
      http.post('*/api/v1/questions', async ({ request }) => {
        submitBody = await request.json()
        return HttpResponse.json({ id: 'project-1', title: 'Novo mini-projeto', status: 'VOTING' }, { status: 201 })
      }),
    )

    const { wrapper, router } = await mountSubmission()

    await fillValidProjectQuestionForm(wrapper)

    expect(wrapper.find('[data-test="alternative-0"]').exists()).toBe(false)

    await wrapper.get('[data-test="pre-analyze-question"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="submit-question"]').trigger('click')
    await flushPromises()

    expect(preAnalysisBody).toMatchObject({ type: 'PROJECT' })
    expect(preAnalysisBody).not.toHaveProperty('alternatives')
    expect(submitBody).toMatchObject({ type: 'PROJECT' })
    expect(submitBody).not.toHaveProperty('alternatives')
    expect(router.currentRoute.value.path).toBe('/questions/voting')
  })
})
