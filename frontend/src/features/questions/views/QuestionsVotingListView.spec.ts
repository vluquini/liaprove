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
import QuestionsVotingListView from './QuestionsVotingListView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/questions/voting', component: QuestionsVotingListView },
      { path: '/questions/:id/voting', component: { template: '<div>Detalhe</div>' } },
      { path: '/questions/new', component: { template: '<div>Nova questao</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView() {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push('/questions/voting')
  await router.isReady()

  const wrapper = mount(QuestionsVotingListView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('QuestionsVotingListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads voting questions and links to the detail page', async () => {
    server.use(
      http.get('*/api/v1/questions/voting', ({ request }) => {
        const url = new URL(request.url)
        expect(url.searchParams.get('page')).toBe('0')
        expect(url.searchParams.get('size')).toBe('10')

        return HttpResponse.json([
          {
            id: 'question-1',
            authorId: 'author-1',
            title: 'Como validar transacoes em APIs REST?',
            knowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
            submissionDate: '2026-05-14T10:00:00',
          },
        ])
      }),
    )

    const { wrapper, router } = await mountView()

    expect(wrapper.text()).toContain('Questões em votação')
    expect(wrapper.text()).toContain('Como validar transacoes em APIs REST?')
    expect(wrapper.text()).toContain('SOFTWARE_DEVELOPMENT')
    expect(wrapper.text()).toContain('DATABASE')

    await wrapper.get('[data-test="open-question-question-1"]').trigger('click')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/questions/question-1/voting')
  })

  it('shows an empty state when there are no voting questions', async () => {
    server.use(http.get('*/api/v1/questions/voting', () => HttpResponse.json([])))

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Nenhuma questão em votação no momento.')
  })

  it('shows an error state when loading fails', async () => {
    server.use(
      http.get('*/api/v1/questions/voting', () =>
        HttpResponse.json({ message: 'Falha ao carregar questões.' }, { status: 500 }),
      ),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Falha ao carregar questões.')
  })
})
