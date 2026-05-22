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
import MiniProjectsPublicView from './MiniProjectsPublicView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/mini-projects/public', component: MiniProjectsPublicView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView() {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push('/mini-projects/public')
  await router.isReady()

  const wrapper = mount(MiniProjectsPublicView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('MiniProjectsPublicView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('loads public mini-project attempts and shows delivery links', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public', () =>
        HttpResponse.json([
          {
            attemptId: 'attempt-1',
            assessmentTitle: 'Avaliação de SOFTWARE_DEVELOPMENT',
            authorName: 'Ana Silva',
            repositoryLink: 'https://github.com/ana/orders-api',
            finishedAt: '2026-05-18T14:30:00',
          },
        ]),
      ),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Mini-projetos públicos')
    expect(wrapper.text()).toContain('Avaliação de Desenvolvimento de Software')
    expect(wrapper.text()).not.toContain('Avaliação de SOFTWARE_DEVELOPMENT')
    expect(wrapper.text()).toContain('Ana Silva')
    expect(wrapper.text()).toContain('18/05/2026')
    expect(wrapper.get('[data-test="mini-project-link-attempt-1"]').attributes('href')).toBe(
      'https://github.com/ana/orders-api',
    )
  })

  it('shows an empty state when no public mini-projects are available', async () => {
    server.use(http.get('*/api/v1/assessment-attempts/mini-project/public', () => HttpResponse.json([])))

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Nenhuma entrega pública disponível.')
  })

  it('shows an error state when loading fails', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public', () =>
        HttpResponse.json({ message: 'Falha ao carregar mini-projetos.' }, { status: 500 }),
      ),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Falha ao carregar mini-projetos.')
  })

  it('sends votes and feedback for a public mini-project attempt', async () => {
    const calls: Record<string, unknown> = {}

    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public', () =>
        HttpResponse.json([
          {
            attemptId: 'attempt-1',
            assessmentTitle: 'API de pedidos',
            authorName: 'Ana Silva',
            repositoryLink: null,
            finishedAt: '2026-05-18T14:30:00',
          },
        ]),
      ),
      http.post('*/api/v1/assessment-attempts/attempt-1/vote', async ({ request }) => {
        calls.vote = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
      http.post('*/api/v1/assessment-attempts/attempt-1/feedback', async ({ request }) => {
        calls.feedback = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="approve-mini-project-attempt-1"]').trigger('click')
    await flushPromises()

    await wrapper.get('[data-test="feedback-mini-project-attempt-1"]').setValue('Boa entrega e README claro.')
    await wrapper.get('[data-test="submit-feedback-mini-project-attempt-1"]').trigger('click')
    await flushPromises()

    expect(calls.vote).toEqual({ voteType: 'APPROVE' })
    expect(calls.feedback).toEqual({ comment: 'Boa entrega e README claro.' })
    expect(wrapper.text()).toContain('Voto registrado.')
    expect(wrapper.text()).toContain('Feedback enviado.')
  })
})
