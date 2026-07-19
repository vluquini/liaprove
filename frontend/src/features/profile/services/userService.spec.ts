import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import {
  changePassword,
  deactivateOwnAccount,
  getUserProfile,
  listMyCertificates,
  updateUserProfile,
} from './userService'

describe('userService', () => {
  it('loads and updates a user profile by id', async () => {
    let updateBody: unknown

    server.use(
      http.get('*/api/v1/users/me', ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/me')

        return HttpResponse.json({
          id: 'user-1',
          name: 'Ana Silva',
          email: 'ana@example.com',
          role: 'PROFESSIONAL',
          hardSkills: ['Java'],
          softSkills: [],
        })
      }),
      http.put('*/api/v1/users/me', async ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/me')
        updateBody = await request.json()
        return HttpResponse.json({
          id: 'user-1',
          name: 'Ana Souza',
          email: 'ana@example.com',
          role: 'PROFESSIONAL',
          hardSkills: ['Java', 'Vue'],
          softSkills: [],
        })
      }),
    )

    const profile = await getUserProfile()
    const updated = await updateUserProfile({
      name: 'Ana Souza',
      hardSkills: ['Java', 'Vue'],
    })

    expect(profile.name).toBe('Ana Silva')
    expect(updateBody).toEqual({
      name: 'Ana Souza',
      hardSkills: ['Java', 'Vue'],
    })
    expect(updated.name).toBe('Ana Souza')
  })

  it('changes password and deactivates the current account', async () => {
    let passwordBody: unknown
    let deactivateCalled = false

    server.use(
      http.patch('*/api/v1/users/me/password', async ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/me/password')
        passwordBody = await request.json()
        return new HttpResponse(null, { status: 204 })
      }),
      http.patch('*/api/v1/users/me/deactivate', () => {
        deactivateCalled = true
        return HttpResponse.text('Account deactivated successfully.')
      }),
    )

    await changePassword({
      oldPassword: 'old-secret',
      newPassword: 'new-secret',
    })
    await deactivateOwnAccount()

    expect(passwordBody).toEqual({
      oldPassword: 'old-secret',
      newPassword: 'new-secret',
    })
    expect(deactivateCalled).toBe(true)
  })

  it('lists certificates for the authenticated user', async () => {
    server.use(
      http.get('*/api/v1/users/me/certificates', ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/me/certificates')

        return HttpResponse.json([
          {
            certificateNumber: 'CERT-123',
            title: 'Certificado de Conclusão: Avaliação de SOFTWARE_DEVELOPMENT',
            description: 'Certificado emitido pelo LIA Prove.',
            certificateUrl: 'https://liaprove.com/certificates/CERT-123',
            issueDate: '2026-05-16',
            score: 92,
          },
        ])
      }),
    )

    const certificates = await listMyCertificates()

    expect(certificates).toHaveLength(1)
    expect(certificates[0].certificateNumber).toBe('CERT-123')
    expect(certificates[0].score).toBe(92)
  })
})
