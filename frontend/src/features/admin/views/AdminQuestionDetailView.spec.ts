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
import AdminQuestionDetailView from './AdminQuestionDetailView.vue'

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
      { path: '/admin/questions', component: { template: '<div>Questoes admin</div>' } },
      { path: '/admin/questions/:questionId', component: AdminQuestionDetailView },
      { path: '/admin/metrics/questions/:questionId', component: AdminQuestionDetailView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView(path = '/admin/questions/question-1') {
  writeStoredSession(makeAuthResponse('ADMIN'))
  const router = makeRouter()
  await router.push(path)
  await router.isReady()

  const wrapper = mount(AdminQuestionDetailView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AdminQuestionDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    server.use(
      http.get('*/api/v1/admin/questions/question-1', () => HttpResponse.json(makeQuestion())),
      http.get('*/api/v1/admin/questions/question-1/votes', () =>
        HttpResponse.json([
          {
            id: 'vote-1',
            user: {
              id: 'user-1',
              name: 'Ana Pereira',
              email: 'ana@example.com',
              role: 'RECRUITER',
            },
            voteType: 'APPROVE',
            createdAt: '2026-05-24T12:00:00',
          },
        ]),
      ),
      http.get('*/api/v1/admin/questions/question-1/feedbacks', () =>
        HttpResponse.json([
          {
            id: 'feedback-1',
            comment: 'Questao clara.',
            author: { id: 'user-2', name: 'Carlos Silva' },
            submissionDate: '2026-05-24T12:10:00',
            reactions: [{ id: 'reaction-1', userId: 'user-3', userName: 'Mariana', type: 'LIKE', createdAt: '2026-05-24T12:20:00' }],
          },
        ]),
      ),
    )
  })

  it('loads question details, votes and feedbacks', async () => {
    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Transacoes em APIs')
    expect(wrapper.text()).toContain('Como garantir consistencia?')
    expect(wrapper.text()).toContain('APPROVED')
    expect(wrapper.text()).toContain('MULTIPLE_CHOICE')
    expect(wrapper.text()).toContain('Usar transacao')
    expect(wrapper.text()).toContain('Ana Pereira')
    expect(wrapper.text()).toContain('APPROVE')
    expect(wrapper.text()).toContain('Questao clara.')
    expect(wrapper.text()).toContain('Carlos Silva')
  })

  it('also loads the audit view from the metrics route', async () => {
    const { wrapper } = await mountView('/admin/metrics/questions/question-1')

    expect(wrapper.text()).toContain('Auditoria da questão')
    expect(wrapper.text()).toContain('Votos')
    expect(wrapper.text()).toContain('Feedbacks')
  })

  it('uses only backend-supported question moderation status options', async () => {
    const { wrapper } = await mountView()
    const statusOptions = wrapper
      .findAll('[data-test="admin-question-status"] option')
      .map((option) => option.text())

    expect(statusOptions).toEqual(['VOTING', 'APPROVED', 'FINISHED', 'REJECTED'])
    expect(statusOptions).not.toContain('SUBMITTED')
    expect(statusOptions).not.toContain('PENDING_REVIEW')
    expect(statusOptions).not.toContain('NEEDS_REVISION')
  })

  it('updates question fields and moderates status', async () => {
    const calls: Record<string, unknown> = {}
    server.use(
      http.put('*/api/v1/admin/questions/question-1', async ({ request }) => {
        calls.update = await request.json()
        return HttpResponse.json({ ...makeQuestion(), title: 'Transacoes atualizadas' })
      }),
      http.patch('*/api/v1/admin/questions/question-1/moderate', async ({ request }) => {
        calls.moderate = await request.json()
        return HttpResponse.json({ ...makeQuestion(), status: 'REJECTED' })
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-question-title"]').setValue('Transacoes atualizadas')
    await wrapper.get('[data-test="admin-question-description"]').setValue('Descricao atualizada')
    await wrapper.get('[data-test="admin-question-area"]').setValue('DATABASE')
    await wrapper.get('[data-test="save-admin-question"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="admin-question-status"]').setValue('REJECTED')
    await wrapper.get('[data-test="moderate-admin-question"]').trigger('click')
    await flushPromises()

    expect(calls.update).toEqual({
      title: 'Transacoes atualizadas',
      description: 'Descricao atualizada',
      knowledgeAreas: ['DATABASE'],
      alternatives: [{ id: 'alternative-1', text: 'Usar transacao' }],
    })
    expect(calls.moderate).toEqual({ newStatus: 'REJECTED' })
    expect(wrapper.text()).toContain('Questão atualizada com sucesso.')
    expect(wrapper.text()).toContain('Questão moderada com sucesso.')
  })

  it('shows empty metrics and error states', async () => {
    server.use(
      http.get('*/api/v1/admin/questions/question-1/votes', () => HttpResponse.json([])),
      http.get('*/api/v1/admin/questions/question-1/feedbacks', () => HttpResponse.json([])),
    )

    const empty = await mountView()
    expect(empty.wrapper.text()).toContain('Nenhum voto registrado.')
    expect(empty.wrapper.text()).toContain('Nenhum feedback registrado.')

    empty.wrapper.unmount()
    localStorage.clear()
    setActivePinia(createPinia())
    server.use(
      http.get('*/api/v1/admin/questions/question-1', () =>
        HttpResponse.json({ message: 'Questão não encontrada.' }, { status: 404 }),
      ),
    )

    const error = await mountView()
    expect(error.wrapper.text()).toContain('Questão não encontrada.')
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
