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
import AdminGeneticAlgorithmView from './AdminGeneticAlgorithmView.vue'

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
      { path: '/admin/algorithms/genetic', component: AdminGeneticAlgorithmView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountView() {
  writeStoredSession(makeAuthResponse('ADMIN'))
  const router = makeRouter()
  await router.push('/admin/algorithms/genetic')
  await router.isReady()

  const wrapper = mount(AdminGeneticAlgorithmView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AdminGeneticAlgorithmView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('loads role multipliers and recruiter vote weights', async () => {
    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/roles/:role/multiplier', ({ params }) =>
        HttpResponse.json(params.role === 'RECRUITER' ? 1.8 : 1),
      ),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', ({ request }) => {
        const url = new URL(request.url)
        expect(url.searchParams.get('page')).toBe('0')
        expect(url.searchParams.get('size')).toBe('20')

        return HttpResponse.json([makeRecruiter()])
      }),
    )

    const { wrapper } = await mountView()

    expect(wrapper.text()).toContain('Algoritmo Genético')
    expect(wrapper.get<HTMLInputElement>('[data-test="admin-ga-role-PROFESSIONAL"]').element.value).toBe('1')
    expect(wrapper.get<HTMLInputElement>('[data-test="admin-ga-role-RECRUITER"]').element.value).toBe('1.8')
    expect(wrapper.text()).toContain('Marina Recrutadora')
    expect(wrapper.text()).toContain('Talent Labs')
    expect(wrapper.text()).toContain('2.4')
    expect(wrapper.text()).toContain('1.6')
  })

  it('updates a role multiplier', async () => {
    let payload: unknown
    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/roles/:role/multiplier', () => HttpResponse.json(1)),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', () => HttpResponse.json([])),
      http.patch('*/api/v1/admin/algorithms/genetic/roles/RECRUITER/multiplier', async ({ request }) => {
        payload = await request.json()
        return new HttpResponse(null, { status: 204 })
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-ga-role-RECRUITER"]').setValue('2.25')
    await wrapper.get('[data-test="admin-ga-save-role-RECRUITER"]').trigger('click')
    await flushPromises()

    expect(payload).toEqual({ multiplier: 2.25 })
    expect(wrapper.text()).toContain('Multiplicador de RECRUITER atualizado.')
  })

  it('updates recruiter multiplier and vote weight', async () => {
    const payloads: unknown[] = []
    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/roles/:role/multiplier', () => HttpResponse.json(1)),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', () => HttpResponse.json([makeRecruiter()])),
      http.patch('*/api/v1/admin/algorithms/genetic/recruiters/recruiter-1/multiplier', async ({ request }) => {
        payloads.push(await request.json())
        return new HttpResponse(null, { status: 204 })
      }),
      http.patch('*/api/v1/admin/algorithms/genetic/recruiters/recruiter-1/vote-weight', async ({ request }) => {
        payloads.push(await request.json())
        return new HttpResponse(null, { status: 204 })
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-ga-recruiter-multiplier-recruiter-1"]').setValue('1.9')
    await wrapper.get('[data-test="admin-ga-save-recruiter-multiplier-recruiter-1"]').trigger('click')
    await flushPromises()

    await wrapper.get('[data-test="admin-ga-recruiter-weight-recruiter-1"]').setValue('2.75')
    await wrapper.get('[data-test="admin-ga-save-recruiter-weight-recruiter-1"]').trigger('click')
    await flushPromises()

    expect(payloads).toEqual([{ multiplier: 1.9 }, { weight: 2.75 }])
    expect(wrapper.text()).toContain('Multiplicador de Marina Recrutadora atualizado.')
    expect(wrapper.text()).toContain('Peso de Marina Recrutadora atualizado.')
  })

  it('runs dry-run and real genetic adjustments', async () => {
    const dryRuns: boolean[] = []
    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/roles/:role/multiplier', () => HttpResponse.json(1)),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', () => HttpResponse.json([])),
      http.post('*/api/v1/admin/algorithms/genetic/adjust', async ({ request }) => {
        const body = (await request.json()) as { dryRun: boolean }
        dryRuns.push(body.dryRun)
        return HttpResponse.json({
          'recruiter-1': body.dryRun ? 2.5 : 2.8,
        })
      }),
    )

    const { wrapper } = await mountView()

    await wrapper.get('[data-test="admin-ga-run-dry"]').trigger('click')
    await flushPromises()

    expect(dryRuns).toEqual([true])
    expect(wrapper.text()).toContain('Resultado simulado')
    expect(wrapper.text()).toContain('recruiter-1')
    expect(wrapper.text()).toContain('2.5')

    await wrapper.get('[data-test="admin-ga-run-real"]').trigger('click')
    await flushPromises()

    expect(dryRuns).toEqual([true, false])
    expect(wrapper.text()).toContain('Pesos ajustados')
    expect(wrapper.text()).toContain('2.8')
  })

  it('shows empty and error states', async () => {
    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/roles/:role/multiplier', () => HttpResponse.json(1)),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', () => HttpResponse.json([])),
    )

    const empty = await mountView()
    expect(empty.wrapper.text()).toContain('Nenhum recrutador com peso registrado.')

    empty.wrapper.unmount()
    localStorage.clear()
    setActivePinia(createPinia())
    server.use(
      http.get('*/api/v1/admin/algorithms/genetic/roles/:role/multiplier', () =>
        HttpResponse.json({ message: 'Falha ao carregar multiplicadores.' }, { status: 500 }),
      ),
      http.get('*/api/v1/admin/algorithms/genetic/recruiters/weights', () => HttpResponse.json([])),
    )

    const error = await mountView()
    expect(error.wrapper.text()).toContain('Falha ao carregar multiplicadores.')
  })
})

function makeRecruiter() {
  return {
    id: 'recruiter-1',
    name: 'Marina Recrutadora',
    email: 'marina@example.com',
    companyName: 'Talent Labs',
    companyEmail: 'rh@talentlabs.com',
    voteWeight: 2.4,
    multiplier: 1.6,
  }
}
