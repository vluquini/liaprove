import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { writeStoredSession } from '@/shared/utils/session'
import { makeAuthResponse } from '@/test/factories/auth'
import AdminHomeView from './AdminHomeView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', component: { template: '<div>Login</div>' } },
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Avaliacoes</div>' } },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/mini-projects/public', component: { template: '<div>Mini projetos</div>' } },
      { path: '/recruiter', component: { template: '<div>Recrutador</div>' } },
      { path: '/admin', component: AdminHomeView },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
      { path: '/admin/questions', component: { template: '<div>Questoes admin</div>' } },
      { path: '/admin/assessments/attempts', component: { template: '<div>Tentativas admin</div>' } },
      { path: '/admin/algorithms/genetic', component: { template: '<div>Algoritmo genetico</div>' } },
    ],
  })
}

describe('AdminHomeView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('renders admin entry cards for etapa 7 flows', async () => {
    writeStoredSession(makeAuthResponse('ADMIN'))
    const router = makeRouter()
    await router.push('/admin')
    await router.isReady()

    const wrapper = mount(AdminHomeView, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    expect(wrapper.text()).toContain('Administração')
    expect(wrapper.text()).toContain('Usuários')
    expect(wrapper.text()).toContain('Questões')
    expect(wrapper.text()).toContain('Tentativas')
    expect(wrapper.text()).toContain('Algoritmo genético')
    expect(wrapper.get('a[href="/admin/users"]').text()).toContain('Abrir')
    expect(wrapper.get('a[href="/admin/questions"]').text()).toContain('Abrir')
    expect(wrapper.get('a[href="/admin/assessments/attempts"]').text()).toContain('Abrir')
    expect(wrapper.get('a[href="/admin/algorithms/genetic"]').text()).toContain('Abrir')
  })
})
