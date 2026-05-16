import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import { verifyCertificate } from './certificateService'

describe('certificateService', () => {
  it('verifies a public certificate by number', async () => {
    server.use(
      http.get('*/api/v1/certificates/CERT-123', () =>
        HttpResponse.json({
          certificateNumber: 'CERT-123',
          title: 'Avaliacao Java',
          description: 'Certificado emitido pelo LIA Prove.',
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

    const certificate = await verifyCertificate('CERT-123')

    expect(certificate.certificateNumber).toBe('CERT-123')
    expect(certificate.owner.name).toBe('Carlos Silva')
  })
})
