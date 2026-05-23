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
      { path: '/mini-projects/public/:attemptId', name: 'mini-project-public-detail', component: { template: '<div>Detalhe</div>' } },
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

  it('loads public mini-project attempts and shows detail links', async () => {
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
    expect(wrapper.get('[data-test="view-mini-project-details-attempt-1"]').text()).toContain('Ver detalhes')
    expect(wrapper.get('[data-test="view-mini-project-details-attempt-1"]').attributes('href')).toBe(
      '/mini-projects/public/attempt-1',
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

  it('does not expose voting, feedback or delivery actions in the listing', async () => {
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
    )

    const { wrapper } = await mountView()

    expect(wrapper.find('[data-test="mini-project-link-attempt-1"]').exists()).toBe(false)
    expect(wrapper.find('[data-test="approve-mini-project-attempt-1"]').exists()).toBe(false)
    expect(wrapper.find('[data-test="reject-mini-project-attempt-1"]').exists()).toBe(false)
    expect(wrapper.find('[data-test="feedback-mini-project-attempt-1"]').exists()).toBe(false)
    expect(wrapper.find('[data-test="submit-feedback-mini-project-attempt-1"]').exists()).toBe(false)
    expect(wrapper.text()).not.toContain('Abrir entrega')
    expect(wrapper.text()).not.toContain('Aprovar')
    expect(wrapper.text()).not.toContain('Rejeitar')
    expect(wrapper.text()).not.toContain('Enviar feedback')
  })
})
