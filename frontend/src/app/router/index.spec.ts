import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import type { Router } from 'vue-router'
import { handleSessionExpired, router } from './index'
import { useAuthStore } from '@/shared/stores/auth'
import { writeStoredSession } from '@/shared/utils/session'
import { makeAuthResponse } from '@/test/factories/auth'

describe('router session handling', () => {
  beforeEach(async () => {
    localStorage.clear()
  })

  it('clears the in-memory auth state before redirecting to login on session expiration', async () => {
    writeStoredSession(makeAuthResponse('PROFESSIONAL'))
    setActivePinia(createPinia())
    const auth = useAuthStore()
    const pushedPaths: string[] = []
    const fakeRouter = {
      currentRoute: { value: { path: '/profile' } },
      push: (path: string) => {
        pushedPaths.push(path)
        return Promise.resolve()
      },
    } as Router

    expect(auth.isAuthenticated).toBe(true)

    handleSessionExpired(fakeRouter)

    expect(auth.isAuthenticated).toBe(false)
    expect(pushedPaths).toEqual(['/login'])
  })

  it('registers assessment and public certificate routes', () => {
    expect(router.resolve('/assessments/start').name).toBe('assessment-start')
    expect(router.resolve('/assessments/attempts/attempt-1').name).toBe('assessment-attempt')
    expect(router.resolve('/assessments/attempts/attempt-1/result').name).toBe('assessment-result')
    expect(router.resolve('/certificates/CERT-123').name).toBe('certificate-public')
  })
})
