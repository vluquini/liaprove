import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import { writeStoredSession } from '@/shared/utils/session'
import AdminUsersView from './AdminUsersView.vue'

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
      { path: '/admin/users', component: AdminUsersView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView() {
  writeStoredSession(makeAuthResponse('ADMIN'))
  const router = makeRouter()
  await router.push('/admin/users')
  await router.isReady()

  const wrapper = mount(AdminUsersView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AdminUsersView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.restoreAllMocks()
  })

  it('loads users and renders profile, company and skill summaries', async () => {
    server.use(
      http.get('*/api/v1/admin/users', () =>
        HttpResponse.json([
          makeProfessionalUser(),
          makeRecruiterUser(),
        ]),
      ),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Usuários')
    expect(wrapper.text()).toContain('Carlos Silva')
    expect(wrapper.text()).toContain('carlos.silva@example.com')
    expect(wrapper.text()).toContain('PROFESSIONAL')
    expect(wrapper.text()).toContain('Java, Spring')
    expect(wrapper.text()).toContain('Ana Pereira')
    expect(wrapper.text()).toContain('RECRUITER')
    expect(wrapper.text()).toContain('TechRecruit')
    expect(wrapper.text()).toContain('contact@techrecruit.com')
  })

  it('submits name and role filters to the admin users endpoint', async () => {
    const calls: string[] = []

    server.use(
      http.get('*/api/v1/admin/users', ({ request }) => {
        const url = new URL(request.url)
        calls.push(url.search)

        return HttpResponse.json([makeRecruiterUser()])
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-user-filter-name"]').setValue('Ana')
    await wrapper.get('[data-test="admin-user-filter-role"]').setValue('RECRUITER')
    await wrapper.get('[data-test="admin-user-apply-filters"]').trigger('click')
    await flushPromises()

    expect(calls[calls.length - 1]).toContain('name=Ana')
    expect(calls[calls.length - 1]).toContain('role=RECRUITER')
    expect(calls[calls.length - 1]).toContain('page=0')
    expect(calls[calls.length - 1]).toContain('size=20')
  })

  it('activates, deactivates and deletes users then reloads the list', async () => {
    const calls: string[] = []
    vi.spyOn(window, 'confirm').mockReturnValue(true)

    server.use(
      http.get('*/api/v1/admin/users', () => {
        calls.push('list')
        return HttpResponse.json([makeProfessionalUser(), makeRecruiterUser()])
      }),
      http.patch('*/api/v1/admin/users/user-1/activate', () => {
        calls.push('activate')
        return new HttpResponse(null, { status: 200 })
      }),
      http.patch('*/api/v1/admin/users/user-1/deactivate', () => {
        calls.push('deactivate')
        return new HttpResponse(null, { status: 200 })
      }),
      http.delete('*/api/v1/admin/users/user-2', () => {
        calls.push('delete')
        return new HttpResponse(null, { status: 204 })
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="activate-user-user-1"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="deactivate-user-user-1"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="delete-user-user-2"]').trigger('click')
    await flushPromises()

    expect(calls).toEqual(['list', 'activate', 'list', 'deactivate', 'list', 'delete', 'list'])
    expect(wrapper.text()).toContain('Usuário ativado com sucesso.')
    expect(wrapper.text()).toContain('Usuário desativado com sucesso.')
    expect(wrapper.text()).toContain('Usuário removido com sucesso.')
  })

  it('shows empty and error states', async () => {
    server.use(http.get('*/api/v1/admin/users', () => HttpResponse.json([])))

    const empty = await mountView()
    expect(empty.wrapper.text()).toContain('Nenhum usuário encontrado.')

    empty.wrapper.unmount()
    localStorage.clear()
    setActivePinia(createPinia())
    server.use(
      http.get('*/api/v1/admin/users', () =>
        HttpResponse.json({ message: 'Falha ao carregar usuários.' }, { status: 500 }),
      ),
    )

    const error = await mountView()
    expect(error.wrapper.text()).toContain('Falha ao carregar usuários.')
  })
})

function makeProfessionalUser() {
  return {
    id: 'user-1',
    name: 'Carlos Silva',
    email: 'carlos.silva@example.com',
    occupation: 'Senior Java Developer',
    bio: 'Backend engineer.',
    experienceLevel: 'SENIOR',
    hardSkills: ['Java', 'Spring'],
    softSkills: ['Mentoria'],
    role: 'PROFESSIONAL',
    companyName: null,
    companyEmail: null,
  }
}

function makeRecruiterUser() {
  return {
    id: 'user-2',
    name: 'Ana Pereira',
    email: 'ana.p@techrecruit.com',
    occupation: 'Tech recruiter',
    bio: null,
    experienceLevel: 'SENIOR',
    hardSkills: null,
    softSkills: null,
    role: 'RECRUITER',
    companyName: 'TechRecruit',
    companyEmail: 'contact@techrecruit.com',
  }
}
