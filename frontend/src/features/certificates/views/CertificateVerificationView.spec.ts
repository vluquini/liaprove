import { flushPromises, mount } from '@vue/test-utils'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { createMemoryHistory, createRouter } from 'vue-router'
import { server } from '@/test/msw'
import CertificateVerificationView from './CertificateVerificationView.vue'

function makeRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/certificates/:certificateNumber', component: CertificateVerificationView },
      { path: '/login', component: { template: '<div>Login</div>' } },
    ],
  })
}

async function mountCertificate(path: string) {
  const router = makeRouter()
  await router.push(path)
  await router.isReady()

  const wrapper = mount(CertificateVerificationView, {
    global: {
      plugins: [[PrimeVue, { theme: { preset: Aura } }], router],
    },
  })
  await flushPromises()

  return { wrapper, router }
}

describe('CertificateVerificationView', () => {
  it('loads and displays a public certificate', async () => {
    server.use(
      http.get('*/api/v1/certificates/CERT-123', () =>
        HttpResponse.json({
          certificateNumber: 'CERT-123',
          title: 'Certificado de Conclusão: Avaliação de SOFTWARE_DEVELOPMENT',
          description: 'Certificamos que Carlos completou com sucesso a Avaliação de SOFTWARE_DEVELOPMENT.',
          certificateUrl: '/certificates/CERT-123',
          issueDate: '2026-05-15',
          score: 80,
          owner: {
            id: 'user-1',
            name: 'Carlos Silva',
            occupation: 'Desenvolvedor Java',
            experienceLevel: 'JUNIOR',
          },
        }),
      ),
    )

    const { wrapper } = await mountCertificate('/certificates/CERT-123')

    expect(wrapper.text()).toContain('Validação pública')
    expect(wrapper.text()).toContain('Certificado válido')
    expect(wrapper.text()).toContain('Certificado de Conclusão: Avaliação de Desenvolvimento de Software')
    expect(wrapper.text()).toContain('Avaliação de Desenvolvimento de Software')
    expect(wrapper.text()).toContain('CERT-123')
    expect(wrapper.text()).toContain('Carlos Silva')
    expect(wrapper.text()).toContain('80')
  })

  it('shows a clear error when certificate is not found', async () => {
    server.use(
      http.get('*/api/v1/certificates/CERT-404', () =>
        HttpResponse.json({ message: 'Certificado nao encontrado.' }, { status: 404 }),
      ),
    )

    const { wrapper } = await mountCertificate('/certificates/CERT-404')

    expect(wrapper.text()).toContain('Certificado nao encontrado.')
  })
})
