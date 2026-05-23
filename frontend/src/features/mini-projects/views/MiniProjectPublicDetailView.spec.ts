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
import MiniProjectPublicDetailView from './MiniProjectPublicDetailView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/mini-projects/public', component: { template: '<div>Mini projetos</div>' } },
      { path: '/mini-projects/public/:attemptId', component: MiniProjectPublicDetailView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountDetail(waitForLoad = true) {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push('/mini-projects/public/attempt-1')
  await router.isReady()

  const wrapper = mount(MiniProjectPublicDetailView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })

  if (waitForLoad) {
    await flushPromises()
  }

  return { wrapper, router }
}

describe('MiniProjectPublicDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows loading before rendering public mini-project details', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () =>
        HttpResponse.json(makeDetail()),
      ),
    )

    const { wrapper } = await mountDetail(false)

    expect(wrapper.text()).toContain('Carregando detalhe do mini-projeto...')

    await flushPromises()

    expect(wrapper.text()).toContain('API REST para pedidos')
    expect(wrapper.text()).toContain('Implementar autenticação JWT e cadastro de pedidos.')
  })

  it('renders prompt, submitted delivery and vote summary', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () =>
        HttpResponse.json(makeDetail()),
      ),
    )

    const { wrapper } = await mountDetail()

    expect(wrapper.text()).toContain('Avaliação de Desenvolvimento de Software')
    expect(wrapper.text()).toContain('Ana Silva')
    expect(wrapper.text()).toContain('18/05/2026')
    expect(wrapper.text()).toContain('Desenvolvimento de Software')
    expect(wrapper.text()).toContain('Média')
    expect(wrapper.text()).toContain('Alta')
    expect(wrapper.text()).toContain('API REST para pedidos')
    expect(wrapper.text()).toContain('Implementar autenticação JWT e cadastro de pedidos.')
    expect(wrapper.text()).toContain('Aprovações')
    expect(wrapper.text()).toContain('3')
    expect(wrapper.text()).toContain('Rejeições')
    expect(wrapper.text()).toContain('1')
    expect(wrapper.get('[data-test="mini-project-delivery-link"]').attributes('href')).toBe(
      'https://github.com/ana/orders-api',
    )
    expect(wrapper.text()).toContain('README com instruções de execução e collection HTTP.')
  })

  it('casts an approve vote and reloads details', async () => {
    let voteBody: unknown
    let detailRequests = 0

    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () => {
        detailRequests += 1
        return HttpResponse.json(makeDetail())
      }),
      http.post('*/api/v1/assessment-attempts/attempt-1/vote', async ({ request }) => {
        voteBody = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    const { wrapper } = await mountDetail()

    await wrapper.get('[data-test="approve-mini-project"]').trigger('click')
    await flushPromises()

    expect(voteBody).toEqual({ voteType: 'APPROVE' })
    expect(detailRequests).toBe(2)
    expect(wrapper.text()).toContain('Voto registrado com sucesso.')
  })

  it('submits feedback and reloads details', async () => {
    let feedbackBody: unknown
    let detailRequests = 0

    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () => {
        detailRequests += 1
        return HttpResponse.json(makeDetail())
      }),
      http.post('*/api/v1/assessment-attempts/attempt-1/feedback', async ({ request }) => {
        feedbackBody = await request.json()
        return new HttpResponse(null, { status: 201 })
      }),
    )

    const { wrapper } = await mountDetail()

    await wrapper.get('[data-test="mini-project-feedback-comment"]').setValue('Entrega clara e bem documentada.')
    await wrapper.get('[data-test="send-mini-project-feedback"]').trigger('click')
    await flushPromises()

    expect(feedbackBody).toEqual({ comment: 'Entrega clara e bem documentada.' })
    expect(detailRequests).toBe(2)
    expect(wrapper.text()).toContain('Feedback enviado com sucesso.')
  })

  it('validates empty feedback locally', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () =>
        HttpResponse.json(makeDetail()),
      ),
    )

    const { wrapper } = await mountDetail()

    await wrapper.get('[data-test="send-mini-project-feedback"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Informe um comentário para enviar o feedback.')
  })

  it('reacts to an existing feedback and reloads details', async () => {
    let reactionBody: unknown
    let detailRequests = 0

    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () => {
        detailRequests += 1
        return HttpResponse.json(makeDetail())
      }),
      http.post('*/api/v1/assessment-feedbacks/feedback-1/react', async ({ request }) => {
        reactionBody = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    const { wrapper } = await mountDetail()

    await wrapper.get('[data-test="like-mini-project-feedback-feedback-1"]').trigger('click')
    await flushPromises()

    expect(reactionBody).toEqual({ reactionType: 'LIKE' })
    expect(detailRequests).toBe(2)
    expect(wrapper.text()).toContain('Reação registrada.')
  })

  it('shows an empty feedback state', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () =>
        HttpResponse.json(makeDetail({ feedbacks: [] })),
      ),
    )

    const { wrapper } = await mountDetail()

    expect(wrapper.text()).toContain('Ainda não há feedbacks para este mini-projeto.')
  })

  it('shows an error state when loading fails', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () =>
        HttpResponse.json({ message: 'Mini-projeto não encontrado.' }, { status: 404 }),
      ),
    )

    const { wrapper } = await mountDetail()

    expect(wrapper.text()).toContain('Mini-projeto não encontrado.')
  })
})

function makeDetail(overrides: Record<string, unknown> = {}) {
  return {
    attemptId: 'attempt-1',
    assessmentTitle: 'Avaliação de SOFTWARE_DEVELOPMENT',
    authorName: 'Ana Silva',
    finishedAt: '2026-05-18T14:30:00',
    repositoryLink: 'https://github.com/ana/orders-api',
    textResponse: 'README com instruções de execução e collection HTTP.',
    question: {
      id: 'question-1',
      title: 'API REST para pedidos',
      description: 'Implementar autenticação JWT e cadastro de pedidos.',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
      difficulty: 'MEDIUM',
      relevance: 'FOUR',
    },
    voteSummary: {
      approves: 3,
      rejects: 1,
    },
    feedbacks: [
      {
        id: 'feedback-1',
        comment: 'Boa organização do projeto.',
        author: {
          id: 'user-2',
          name: 'Roberto Lima',
        },
        submissionDate: '2026-05-19T09:00:00',
        reactions: [
          {
            id: 'reaction-1',
            userId: 'user-3',
            userName: 'Maria Souza',
            type: 'LIKE',
            createdAt: '2026-05-19T09:30:00',
          },
        ],
      },
    ],
    ...overrides,
  }
}
