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
import AdminQuestionsView from './AdminQuestionsView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/mini-projects/public', component: { template: '<div>Mini projetos</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/admin', component: { template: '<div>Admin</div>' } },
      { path: '/admin/questions', component: AdminQuestionsView },
      { path: '/admin/questions/:questionId', name: 'admin-question-detail', component: { template: '<div>Detalhe</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView() {
  writeStoredSession(makeAuthResponse('ADMIN'))
  const router = makeRouter()
  await router.push('/admin/questions')
  await router.isReady()

  const wrapper = mount(AdminQuestionsView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AdminQuestionsView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('loads admin questions and links to detail pages', async () => {
    server.use(
      http.get('*/api/v1/admin/questions', ({ request }) => {
        const url = new URL(request.url)
        expect(url.searchParams.get('page')).toBe('0')
        expect(url.searchParams.get('size')).toBe('10')

        return HttpResponse.json([makeQuestion()])
      }),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Questões')
    expect(wrapper.text()).toContain('Transacoes em APIs')
    expect(wrapper.text()).toContain('MULTIPLE_CHOICE')
    expect(wrapper.text()).toContain('APPROVED')
    expect(wrapper.text()).toContain('SOFTWARE_DEVELOPMENT')
    expect(wrapper.get('[data-test="open-admin-question-question-1"]').attributes('href')).toBe(
      '/admin/questions/question-1',
    )
  })

  it('uses only backend-supported question status filter options', async () => {
    server.use(http.get('*/api/v1/admin/questions', () => HttpResponse.json([makeQuestion()])))

    const { wrapper } = await mountView()
    const statusOptions = wrapper
      .findAll('[data-test="admin-question-filter-status"] option')
      .map((option) => option.text())

    expect(statusOptions).toEqual(['Todos', 'VOTING', 'APPROVED', 'FINISHED', 'REJECTED'])
    expect(statusOptions).not.toContain('SUBMITTED')
    expect(statusOptions).not.toContain('PENDING_REVIEW')
    expect(statusOptions).not.toContain('NEEDS_REVISION')
  })

  it('shows author name before author id in the filter row', async () => {
    server.use(http.get('*/api/v1/admin/questions', () => HttpResponse.json([makeQuestion()])))

    const { wrapper } = await mountView()
    const filters = wrapper.findAll('[data-test^="admin-question-filter-"]')
    const filterOrder = filters.map((filter) => filter.attributes('data-test'))

    expect(filterOrder).toEqual([
      'admin-question-filter-area',
      'admin-question-filter-difficulty',
      'admin-question-filter-status',
      'admin-question-filter-author-name',
      'admin-question-filter-author-id',
    ])
    expect(wrapper.text()).toContain('Nome do autor')
    expect(wrapper.text()).toContain('ID do autor')
  })

  it('submits question filters to the admin questions endpoint', async () => {
    const calls: string[] = []
    server.use(
      http.get('*/api/v1/admin/questions', ({ request }) => {
        const url = new URL(request.url)
        calls.push(url.search)
        return HttpResponse.json([makeQuestion()])
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-question-filter-area"]').setValue('DATABASE')
    await wrapper.get('[data-test="admin-question-filter-difficulty"]').setValue('MEDIUM')
    await wrapper.get('[data-test="admin-question-filter-status"]').setValue('APPROVED')
    await wrapper.get('[data-test="admin-question-filter-author-name"]').setValue('Ana Pereira')
    await wrapper.get('[data-test="admin-question-filter-author-id"]').setValue('author-1')
    await wrapper.get('[data-test="admin-question-apply-filters"]').trigger('click')
    await flushPromises()

    const lastCall = calls[calls.length - 1]
    expect(lastCall).toContain('knowledgeAreas=DATABASE')
    expect(lastCall).toContain('difficultyLevel=MEDIUM')
    expect(lastCall).toContain('status=APPROVED')
    expect(lastCall).toContain('authorName=Ana+Pereira')
    expect(lastCall).toContain('authorId=author-1')
    expect(lastCall).toContain('page=0')
    expect(lastCall).toContain('size=10')
  })

  it('shows empty and error states', async () => {
    server.use(http.get('*/api/v1/admin/questions', () => HttpResponse.json([])))

    const empty = await mountView()
    expect(empty.wrapper.text()).toContain('Nenhuma questão encontrada.')

    empty.wrapper.unmount()
    localStorage.clear()
    setActivePinia(createPinia())
    server.use(
      http.get('*/api/v1/admin/questions', () =>
        HttpResponse.json({ message: 'Falha ao carregar questões.' }, { status: 500 }),
      ),
    )

    const error = await mountView()
    expect(error.wrapper.text()).toContain('Falha ao carregar questões.')
  })
})

function makeQuestion() {
  return {
    type: 'MULTIPLE_CHOICE',
    id: 'question-1',
    authorId: 'author-1',
    title: 'Transacoes em APIs',
    description: 'Como garantir consistencia?',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    difficultyByCommunity: 'MEDIUM',
    relevanceByCommunity: 'FOUR',
    relevanceByLLM: null,
    submissionDate: '2026-05-24T10:00:00',
    status: 'APPROVED',
    alternatives: [{ id: 'alternative-1', text: 'Usar transacao' }],
  }
}
