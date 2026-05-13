import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import type { Router } from 'vue-router'
import { handleSessionExpired } from './index'
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
})
