import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import type { RouteLocationNormalized } from 'vue-router'
import { redirectAuthenticated, requireAuth } from './guards'
import { writeStoredSession } from '@/shared/utils/session'
import { makeAuthResponse } from '@/test/factories/auth'

function makeRoute(path: string, meta: RouteLocationNormalized['meta'] = {}): RouteLocationNormalized {
  return {
    fullPath: path,
    meta,
  } as RouteLocationNormalized
}

describe('router guards', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  it('redirects unauthenticated users without using the deprecated next callback', () => {
    const result = requireAuth(makeRoute('/questions/new'))

    expect(result).toEqual({
      path: '/login',
      query: { redirect: '/questions/new' },
    })
  })

  it('blocks authenticated users without the required route role', () => {
    writeStoredSession(makeAuthResponse('PROFESSIONAL'))
    setActivePinia(createPinia())

    const result = requireAuth(makeRoute('/admin', { roles: ['ADMIN'] }))

    expect(result).toBe('/forbidden')
  })

  it('allows authenticated users with an accepted route role', () => {
    writeStoredSession(makeAuthResponse('RECRUITER'))
    setActivePinia(createPinia())

    const result = requireAuth(makeRoute('/questions/voting', { roles: ['PROFESSIONAL', 'RECRUITER'] }))

    expect(result).toBeUndefined()
  })

  it('redirects authenticated users away from guest-only screens', () => {
    writeStoredSession(makeAuthResponse('PROFESSIONAL'))
    setActivePinia(createPinia())

    const result = redirectAuthenticated(makeRoute('/login'))

    expect(result).toBe('/dashboard')
  })
})
