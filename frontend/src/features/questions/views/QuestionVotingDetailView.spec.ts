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
import QuestionVotingDetailView from './QuestionVotingDetailView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/questions/:id/voting', component: QuestionVotingDetailView },
      { path: '/questions/new', component: { template: '<div>Nova questao</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountDetail(path = '/questions/question-1/voting') {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push(path)
  await router.isReady()

  const wrapper = mount(QuestionVotingDetailView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('QuestionVotingDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads detail and casts an approve vote', async () => {
    let voteBody: unknown
    server.use(
      http.get('*/api/v1/questions/question-1/voting-details', () => HttpResponse.json(makeDetail())),
      http.post('*/api/v1/questions/question-1/vote', async ({ request }) => {
        voteBody = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    const { wrapper } = await mountDetail()

    expect(wrapper.text()).toContain('Como validar transacoes em APIs REST?')
    expect(wrapper.text()).toContain('Usar transacoes no caso de uso.')
    expect(wrapper.text()).toContain('Aprovações')
    expect(wrapper.text()).toContain('2')

    await wrapper.get('[data-test="approve-question"]').trigger('click')
    await flushPromises()

    expect(voteBody).toEqual({ voteType: 'APPROVE' })
    expect(wrapper.text()).toContain('Voto registrado com sucesso.')
  })

  it('submits question feedback with selected metadata', async () => {
    let feedbackBody: unknown
    server.use(
      http.get('*/api/v1/questions/question-1/voting-details', () => HttpResponse.json(makeDetail())),
      http.post('*/api/v1/questions/question-1/feedback', async ({ request }) => {
        feedbackBody = await request.json()
        return new HttpResponse(null, { status: 201 })
      }),
    )

    const { wrapper } = await mountDetail()

    await wrapper.get('[data-test="feedback-comment"]').setValue('Boa questao, mas precisa de um enunciado mais direto.')
    await wrapper.get('[data-test="send-feedback"]').trigger('click')
    await flushPromises()

    expect(feedbackBody).toEqual({
      comment: 'Boa questao, mas precisa de um enunciado mais direto.',
      difficultyLevel: 'MEDIUM',
      knowledgeArea: 'SOFTWARE_DEVELOPMENT',
      relevanceLevel: 'FOUR',
    })
    expect(wrapper.text()).toContain('Feedback enviado com sucesso.')
  })

  it('reacts to an existing feedback', async () => {
    let reactionBody: unknown
    server.use(
      http.get('*/api/v1/questions/question-1/voting-details', () => HttpResponse.json(makeDetail())),
      http.post('*/api/v1/feedbacks/feedback-1/react', async ({ request }) => {
        reactionBody = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    const { wrapper } = await mountDetail()

    await wrapper.get('[data-test="like-feedback-feedback-1"]').trigger('click')
    await flushPromises()

    expect(reactionBody).toEqual({ reactionType: 'LIKE' })
    expect(wrapper.text()).toContain('Reação registrada.')
  })

  it('shows an error state when detail loading fails', async () => {
    server.use(
      http.get('*/api/v1/questions/question-1/voting-details', () =>
        HttpResponse.json({ message: 'Questão não encontrada.' }, { status: 404 }),
      ),
    )

    const { wrapper } = await mountDetail()

    expect(wrapper.text()).toContain('Questão não encontrada.')
  })
})

function makeDetail() {
  return {
    id: 'question-1',
    authorId: 'author-1',
    author: { id: 'author-1', name: 'Ana Silva' },
    title: 'Como validar transacoes em APIs REST?',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    submissionDate: '2026-05-14T10:00:00',
    description: 'Qual abordagem melhora a consistencia de dados em uma API REST com banco relacional?',
    alternatives: [
      { id: 'alt-1', text: 'Usar transacoes no caso de uso.' },
      { id: 'alt-2', text: 'Ignorar rollback.' },
    ],
    voteSummary: { approves: 2, rejects: 1 },
    feedbacks: [
      {
        id: 'feedback-1',
        comment: 'Boa questao.',
        author: { id: 'author-2', name: 'Bruno' },
        submissionDate: '2026-05-14T11:00:00',
        reactions: [{ id: 'reaction-1', userId: 'user-2', userName: 'Bruno', type: 'LIKE', createdAt: '2026-05-14T11:10:00' }],
      },
    ],
    relevanceByLLM: 'FOUR',
  }
}
