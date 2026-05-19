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
import RecruiterOpenQuestionCreateView from './RecruiterOpenQuestionCreateView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/recruiter/questions/open/new', component: RecruiterOpenQuestionCreateView },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountOpenQuestion() {
  writeStoredSession(makeAuthResponse('RECRUITER'))
  const router = makeRouter()
  await router.push('/recruiter/questions/open/new')
  await router.isReady()

  const wrapper = mount(RecruiterOpenQuestionCreateView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('RecruiterOpenQuestionCreateView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('validates required open question fields', async () => {
    const { wrapper } = await mountOpenQuestion()

    await wrapper.get('[data-test="create-open-question"]').trigger('click')

    expect(wrapper.text()).toContain('Informe título, descrição, área, dificuldade, relevância e critério de avaliação.')
  })

  it('creates an open question with visibility and shows success', async () => {
    let payload: unknown
    server.use(
      http.post('*/api/v1/questions/open', async ({ request }) => {
        payload = await request.json()
        return HttpResponse.json(
          { id: 'question-1', title: 'Explique transacoes em APIs', type: 'OPEN', visibility: 'SHARED' },
          { status: 201 },
        )
      }),
    )

    const { wrapper } = await mountOpenQuestion()

    await wrapper.get('[data-test="open-title"]').setValue('Explique transacoes em APIs')
    await wrapper
      .get('[data-test="open-description"]')
      .setValue('Descreva como voce garantiria consistencia em uma API REST com banco relacional.')
    await wrapper.get('[data-test="area-SOFTWARE_DEVELOPMENT"]').setValue(true)
    await wrapper.get('[data-test="open-difficulty"]').setValue('MEDIUM')
    await wrapper.get('[data-test="open-relevance"]').setValue('FOUR')
    await wrapper.get('[data-test="open-guideline"]').setValue('Avaliar clareza, consistencia e exemplos praticos.')
    await wrapper.get('[data-test="open-visibility"]').setValue('SHARED')
    await wrapper.get('[data-test="create-open-question"]').trigger('click')
    await flushPromises()

    expect(payload).toMatchObject({
      type: 'OPEN',
      title: 'Explique transacoes em APIs',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
      difficultyByCommunity: 'MEDIUM',
      relevanceByCommunity: 'FOUR',
      guideline: 'Avaliar clareza, consistencia e exemplos praticos.',
      visibility: 'SHARED',
    })
    expect(wrapper.text()).toContain('Questão criada: Explique transacoes em APIs')
  })
})
