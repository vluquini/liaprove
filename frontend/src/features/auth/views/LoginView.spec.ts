import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import LoginView from './LoginView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', component: LoginView },
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/register', component: { template: '<div>Register</div>' } },
    ],
  })
}

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows a validation message when required fields are empty', async () => {
    const router = makeRouter()
    await router.push('/login')
    await router.isReady()

    const wrapper = mount(LoginView, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    await wrapper.get('form').trigger('submit')

    expect(wrapper.text()).toContain('Informe email e senha.')
  })

  it('submits credentials and redirects to dashboard', async () => {
    server.use(
      http.post('*/api/auth/login', async ({ request }) => {
        expect(await request.json()).toEqual({
          email: 'ana@example.com',
          password: 'secret123',
        })

        return HttpResponse.json(makeAuthResponse('PROFESSIONAL'))
      }),
    )

    const router = makeRouter()
    await router.push('/login')
    await router.isReady()

    const wrapper = mount(LoginView, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    await wrapper.get('input[type="email"]').setValue('ana@example.com')
    await wrapper.get('input[type="password"]').setValue('secret123')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/dashboard')
  })
})
