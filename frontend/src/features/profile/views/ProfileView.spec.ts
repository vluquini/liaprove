import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import { makeAuthResponse, makeUser } from '@/test/factories/auth'
import { writeStoredSession } from '@/shared/utils/session'
import ProfileView from './ProfileView.vue'

const userId = 'f66a1a44-40f4-4430-9b1b-eeb1df2e2eb0'

function makeProfileResponse() {
  return {
    ...makeUser('PROFESSIONAL'),
    occupation: 'Desenvolvedora Java',
    bio: 'Atua com backend e qualidade.',
    experienceLevel: 'PLENO',
    hardSkills: ['Java', 'Spring'],
    softSkills: ['Comunicação'],
  }
}

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login', component: { template: '<div>Login</div>' } },
      { path: '/profile', component: ProfileView },
    ],
  })
}

async function mountProfile() {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push('/profile')
  await router.isReady()

  const wrapper = mount(ProfileView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('ProfileView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    server.use(
      http.get(`*/api/v1/users/${userId}`, () => HttpResponse.json(makeProfileResponse())),
      http.put(`*/api/v1/users/${userId}`, async ({ request }) => {
        const body = (await request.json()) as Record<string, unknown>
        return HttpResponse.json({
          ...makeProfileResponse(),
          ...body,
        })
      }),
      http.patch(`*/api/v1/users/${userId}/password`, () => new HttpResponse(null, { status: 204 })),
      http.patch('*/api/v1/users/me/deactivate', () =>
        HttpResponse.text('Account deactivated successfully.'),
      ),
    )
  })

  it('loads and updates the user profile', async () => {
    const { wrapper } = await mountProfile()

    expect((wrapper.get('[data-test="profile-name"]').element as HTMLInputElement).value).toBe('Ana Silva')
    expect((wrapper.get('[data-test="profile-occupation"]').element as HTMLInputElement).value).toBe(
      'Desenvolvedora Java',
    )

    await wrapper.get('[data-test="profile-name"]').setValue('Ana Souza')
    await wrapper.get('[data-test="profile-bio"]').setValue('Backend Java e arquitetura.')
    await wrapper.get('[data-test="save-profile"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Perfil atualizado com sucesso.')
    expect(wrapper.text()).toContain('Ana Souza')
  })

  it('changes password from a separate dialog', async () => {
    let requestBody: unknown
    server.use(
      http.patch(`*/api/v1/users/${userId}/password`, async ({ request }) => {
        requestBody = await request.json()
        return new HttpResponse(null, { status: 204 })
      }),
    )
    const { wrapper } = await mountProfile()

    await wrapper.get('[data-test="open-password-dialog"]').trigger('click')
    await flushPromises()
    await wrapper.get('[data-test="old-password"]').setValue('old-secret')
    await wrapper.get('[data-test="new-password"]').setValue('new-secret')
    await wrapper.get('[data-test="confirm-password-change"]').trigger('click')
    await flushPromises()

    expect(requestBody).toEqual({
      oldPassword: 'old-secret',
      newPassword: 'new-secret',
    })
    expect(wrapper.text()).toContain('Senha alterada com sucesso.')
  })

  it('deactivates the account and redirects after confirmation delay', async () => {
    vi.useFakeTimers()
    const { wrapper, router } = await mountProfile()

    await wrapper.get('[data-test="deactivate-account"]').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Conta desativada. Você será redirecionado para o login.')

    await vi.advanceTimersByTimeAsync(5000)
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/login')
    expect(localStorage.getItem('liaprove.auth.session')).toBeNull()

    vi.useRealTimers()
  })
})
