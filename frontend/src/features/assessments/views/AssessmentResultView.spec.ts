import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { makeAuthResponse } from '@/test/factories/auth'
import { writeStoredSession } from '@/shared/utils/session'
import { saveAssessmentResult } from '../utils/assessmentSession'
import AssessmentResultView from './AssessmentResultView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/dashboard', component: { template: '<div>Dashboard</div>' } },
      { path: '/profile', component: { template: '<div>Perfil</div>' } },
      { path: '/assessments/start', component: { template: '<div>Inicio</div>' } },
      { path: '/assessments/attempts/:attemptId/result', component: AssessmentResultView },
      { path: '/questions/voting', component: { template: '<div>Questoes</div>' } },
      { path: '/certificates/:certificateNumber', component: { template: '<div>Certificado</div>' } },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountResult(path: string) {
  writeStoredSession(makeAuthResponse('PROFESSIONAL'))
  const router = makeRouter()
  await router.push(path)
  await router.isReady()

  const wrapper = mount(AssessmentResultView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('AssessmentResultView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  afterEach(() => {
    sessionStorage.clear()
  })

  it('shows an approved result with certificate link', async () => {
    saveAssessmentResult('attempt-1', {
      status: 'APPROVED',
      accuracyRate: 80,
      certificateUrl: '/certificates/CERT-123',
      message: 'Aprovado',
    })

    const { wrapper } = await mountResult('/assessments/attempts/attempt-1/result')

    expect(wrapper.text()).toContain('Resultado da avaliação')
    expect(wrapper.text()).toContain('80%')
    expect(wrapper.text()).toContain('Aprovado')
    expect(wrapper.get('[data-test="open-certificate"]').attributes('href')).toBe('/certificates/CERT-123')
  })

  it('uses the local certificate route when the backend returns an absolute certificate url', async () => {
    saveAssessmentResult('attempt-1', {
      status: 'APPROVED',
      accuracyRate: 80,
      certificateUrl: 'https://liaprove.com/certificates/CERT-123',
      message: 'Aprovado',
    })

    const { wrapper } = await mountResult('/assessments/attempts/attempt-1/result')

    expect(wrapper.get('[data-test="open-certificate"]').attributes('href')).toBe('/certificates/CERT-123')
  })

  it('shows a failed result without certificate action', async () => {
    saveAssessmentResult('attempt-1', {
      status: 'FAILED',
      accuracyRate: 40,
      certificateUrl: null,
      message: 'Pontuacao insuficiente para emissao de certificado.',
    })

    const { wrapper } = await mountResult('/assessments/attempts/attempt-1/result')

    expect(wrapper.text()).toContain('40%')
    expect(wrapper.text()).toContain('Pontuacao insuficiente')
    expect(wrapper.find('[data-test="open-certificate"]').exists()).toBe(false)
  })

  it('shows a recoverable state when result is missing', async () => {
    const { wrapper } = await mountResult('/assessments/attempts/attempt-1/result')
    expect(wrapper.text()).toContain('Resultado indisponível neste dispositivo.')
  })
})
