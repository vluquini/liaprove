import { createPinia, setActivePinia } from 'pinia'
import { http, HttpResponse } from 'msw'
import { beforeEach, describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import { makeAuthResponse } from '@/test/factories/auth'
import { useAuthStore } from './auth'

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('logs in and persists the authenticated session', async () => {
    server.use(
      http.post('*/api/auth/login', async ({ request }) => {
        expect(await request.json()).toEqual({
          email: 'ana@example.com',
          password: 'secret123',
        })

        return HttpResponse.json(makeAuthResponse('PROFESSIONAL'))
      }),
    )

    const auth = useAuthStore()

    await auth.login({ email: 'ana@example.com', password: 'secret123' })

    expect(auth.isAuthenticated).toBe(true)
    expect(auth.user?.email).toBe('ana@example.com')
    expect(localStorage.getItem('liaprove.auth.session')).toContain('test-token')
  })

  it('registers and persists the authenticated session', async () => {
    server.use(
      http.post('*/api/auth/register', async ({ request }) => {
        expect(await request.json()).toMatchObject({
          name: 'Ana Silva',
          email: 'ana@example.com',
          password: 'secret123',
          role: 'PROFESSIONAL',
        })

        return HttpResponse.json(makeAuthResponse('PROFESSIONAL'))
      }),
    )

    const auth = useAuthStore()

    await auth.register({
      name: 'Ana Silva',
      email: 'ana@example.com',
      password: 'secret123',
      role: 'PROFESSIONAL',
      hardSkills: ['Java'],
      softSkills: ['Comunicação'],
    })

    expect(auth.isAuthenticated).toBe(true)
    expect(auth.user?.name).toBe('Ana Silva')
  })

  it('clears persisted data on logout', async () => {
    server.use(http.post('*/api/auth/login', () => HttpResponse.json(makeAuthResponse('RECRUITER'))))

    const auth = useAuthStore()

    await auth.login({ email: 'ana@example.com', password: 'secret123' })
    auth.logout()

    expect(auth.isAuthenticated).toBe(false)
    expect(auth.user).toBeNull()
    expect(localStorage.getItem('liaprove.auth.session')).toBeNull()
  })
})
