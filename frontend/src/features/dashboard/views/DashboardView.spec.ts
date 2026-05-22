import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { writeStoredSession } from '@/shared/utils/session'
import { makeAuthResponse } from '@/test/factories/auth'
import DashboardView from './DashboardView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', component: { template: '<div>Login</div>' } },
      { path: '/dashboard', component: DashboardView },
      { path: '/assessments/start', component: { template: '<div>Avaliacao</div>' } },
      { path: '/questions/new', component: { template: '<div>Questao</div>' } },
      { path: '/mini-projects/public', component: { template: '<div>Mini projetos</div>' } },
      { path: '/recruiter/job-analysis', component: { template: '<div>Vaga</div>' } },
      { path: '/admin/users', component: { template: '<div>Usuarios</div>' } },
    ],
  })
}

async function mountDashboard(role: 'PROFESSIONAL' | 'RECRUITER' | 'ADMIN') {
  writeStoredSession(makeAuthResponse(role))
  const router = makeRouter()
  await router.push('/dashboard')
  await router.isReady()

  return mount(DashboardView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
}

describe('DashboardView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows professional quick actions', async () => {
    const wrapper = await mountDashboard('PROFESSIONAL')

    expect(wrapper.text()).toContain('Olá, Ana Silva')
    expect(wrapper.text()).toContain('Iniciar avaliação')
    expect(wrapper.text()).toContain('Submeter questão')
    expect(wrapper.text()).toContain('Votar em questões')
    expect(wrapper.text()).toContain('Avaliar mini-projetos')
  })

  it('shows recruiter quick actions and company context', async () => {
    const wrapper = await mountDashboard('RECRUITER')

    expect(wrapper.text()).toContain('Iniciar avaliação')
    expect(wrapper.text()).toContain('Submeter questão')
    expect(wrapper.text()).toContain('Votar em questões')
    expect(wrapper.text()).toContain('Avaliar mini-projetos')
    expect(wrapper.text()).toContain('Analisar vaga')
    expect(wrapper.text()).toContain('Criar avaliação personalizada')
    expect(wrapper.text()).toContain('Questão aberta')
    expect(wrapper.text()).not.toContain('Gerenciar usuários')
  })

  it('shows admin moderation actions', async () => {
    const wrapper = await mountDashboard('ADMIN')

    expect(wrapper.text()).toContain('Gerenciar usuários')
    expect(wrapper.text()).toContain('Moderar questões')
    expect(wrapper.text()).toContain('Algoritmo genético')
  })
})
