import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import RegisterView from './RegisterView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/register', component: RegisterView },
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

describe('RegisterView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('shows a validation message when required fields are empty', async () => {
    const router = makeRouter()
    await router.push('/register')
    await router.isReady()

    const wrapper = mount(RegisterView, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    await wrapper.get('form').trigger('submit')

    expect(wrapper.text()).toContain('Informe nome, email e senha.')
  })

  it('submits professional registration payload and redirects to dashboard', async () => {
    server.use(
      http.post('*/api/auth/register', async ({ request }) => {
        expect(await request.json()).toMatchObject({
          name: 'Ana Silva',
          email: 'ana@example.com',
          password: 'secret123',
          role: 'PROFESSIONAL',
          occupation: 'Desenvolvedora Java',
          hardSkills: ['Java', 'Spring Boot'],
          softSkills: ['Comunicação', 'liderança'],
        })

        return HttpResponse.json(makeAuthResponse('PROFESSIONAL'))
      }),
    )

    const router = makeRouter()
    await router.push('/register')
    await router.isReady()

    const wrapper = mount(RegisterView, {
      global: {
        plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
      },
    })

    await wrapper.get('input[autocomplete="name"]').setValue('Ana Silva')
    await wrapper.get('input[autocomplete="email"]').setValue('ana@example.com')
    await wrapper.get('input[type="password"]').setValue('secret123')
    await wrapper.get('input[placeholder="Desenvolvedor Java"]').setValue('Desenvolvedora Java')
    await wrapper.get('input[placeholder="Java, Spring Boot, SQL"]').setValue('Java, Spring Boot')
    await wrapper.get('input[placeholder="Comunicação, liderança"]').setValue('Comunicação, liderança')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/dashboard')
  })
})
