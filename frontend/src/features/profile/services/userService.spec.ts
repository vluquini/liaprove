import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import { changePassword, deactivateOwnAccount, getUserProfile, updateUserProfile } from './userService'

describe('userService', () => {
  it('loads and updates a user profile by id', async () => {
    let updateBody: unknown

    server.use(
      http.get('*/api/v1/users/user-1', ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/user-1')

        return HttpResponse.json({
          id: 'user-1',
          name: 'Ana Silva',
          email: 'ana@example.com',
          role: 'PROFESSIONAL',
          hardSkills: ['Java'],
          softSkills: [],
        })
      }),
      http.put('*/api/v1/users/user-1', async ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/user-1')
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

    const profile = await getUserProfile('user-1')
    const updated = await updateUserProfile('user-1', {
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
      http.patch('*/api/v1/users/user-1/password', async ({ request }) => {
        expect(new URL(request.url).pathname).toBe('/api/v1/users/user-1/password')
        passwordBody = await request.json()
        return new HttpResponse(null, { status: 204 })
      }),
      http.patch('*/api/v1/users/me/deactivate', () => {
        deactivateCalled = true
        return HttpResponse.text('Account deactivated successfully.')
      }),
    )

    await changePassword('user-1', {
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
})
