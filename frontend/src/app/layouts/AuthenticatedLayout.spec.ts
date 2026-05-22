import { mount } from '@vue/test-utils'
import { flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { writeStoredSession } from '@/shared/utils/session'
import { makeAuthResponse } from '@/test/factories/auth'
import AuthenticatedLayout from './AuthenticatedLayout.vue'

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
      { path: '/recruiter/job-analysis', component: { template: '<div>Analise</div>' } },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
    ],
  })
}

describe('AuthenticatedLayout', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows common navigation and logs out the current user', async () => {
    writeStoredSession(makeAuthResponse('PROFESSIONAL'))
    const router = makeRouter()
    await router.push('/dashboard')
    await router.isReady()

    const wrapper = mount(AuthenticatedLayout, {
      slots: { default: '<h1>Conteudo protegido</h1>' },
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    expect(wrapper.text()).toContain('Ana Silva')
    expect(wrapper.text()).toContain('Dashboard')
    expect(wrapper.text()).toContain('Perfil')
    expect(wrapper.text()).toContain('Avaliações')
    expect(wrapper.text()).toContain('Questões')
    expect(wrapper.text()).toContain('Mini-projetos')
    expect(wrapper.text()).toContain('Conteudo protegido')
    expect(wrapper.get('a[href="/mini-projects/public"]').text()).toContain('Mini-projetos')

    await wrapper.get('[data-test="logout-button"]').trigger('click')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/login')
    expect(localStorage.getItem('liaprove.auth.session')).toBeNull()
  })

  it('shows recruiter and admin navigation only for allowed roles', async () => {
    writeStoredSession(makeAuthResponse('RECRUITER'))
    const router = makeRouter()
    await router.push('/dashboard')
    await router.isReady()

    const recruiterWrapper = mount(AuthenticatedLayout, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    expect(recruiterWrapper.text()).toContain('Recrutador')
    expect(recruiterWrapper.text()).not.toContain('Admin')
    expect(recruiterWrapper.get('a[href="/recruiter"]').text()).toContain('Recrutador')

    recruiterWrapper.unmount()
    localStorage.clear()
    setActivePinia(createPinia())
    writeStoredSession(makeAuthResponse('ADMIN'))

    const adminWrapper = mount(AuthenticatedLayout, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    expect(adminWrapper.text()).toContain('Recrutador')
    expect(adminWrapper.text()).toContain('Admin')
  })
})
