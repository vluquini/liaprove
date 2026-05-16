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
      { path: '/certificates/:certificateNumber', component: { template: '<div>Certificado</div>' } },
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
      http.get('*/api/v1/users/me/certificates', () =>
        HttpResponse.json([
          {
            certificateNumber: 'CERT-123',
            title: 'Certificado de Conclusão: Avaliação de SOFTWARE_DEVELOPMENT',
            description: 'Certificado emitido pelo LIA Prove.',
            certificateUrl: 'https://liaprove.com/certificates/CERT-123',
            issueDate: '2026-05-16',
            score: 92,
          },
        ]),
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

  it('loads certificates in the user profile', async () => {
    const { wrapper } = await mountProfile()

    expect(wrapper.text()).toContain('Meus certificados')
    expect(wrapper.text()).toContain('Certificado de Conclusão: Avaliação de Desenvolvimento de Software')
    expect(wrapper.text()).toContain('92%')
    expect(wrapper.get('[data-test="certificate-CERT-123"]').attributes('href')).toBe('/certificates/CERT-123')
  })

  it('renders certificates before the profile data form', async () => {
    const { wrapper } = await mountProfile()
    const certificatesSection = wrapper.get('[data-test="certificates-section"]').element
    const profileForm = wrapper.get('[data-test="profile-form-section"]').element

    expect(certificatesSection.compareDocumentPosition(profileForm) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy()
  })

  it('keeps the profile session when certificates cannot be loaded', async () => {
    server.use(
      http.get('*/api/v1/users/me/certificates', () =>
        HttpResponse.json(
          { error: 'Unauthorized: Full authentication is required to access this resource' },
          { status: 401 },
        ),
      ),
    )

    const { wrapper, router } = await mountProfile()

    expect(wrapper.text()).toContain('Ana Silva')
    expect(wrapper.text()).toContain('Não foi possível carregar seus certificados agora.')
    expect(wrapper.text()).not.toContain('Unauthorized: Full authentication is required to access this resource')
    expect(router.currentRoute.value.path).toBe('/profile')
    expect(localStorage.getItem('liaprove.auth.session')).not.toBeNull()
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
