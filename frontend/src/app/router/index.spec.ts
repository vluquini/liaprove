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

  it('registers assessment, public certificate and mini-project routes', () => {
    expect(router.resolve('/assessments/start').name).toBe('assessment-start')
    expect(router.resolve('/assessments/attempts/attempt-1').name).toBe('assessment-attempt')
    expect(router.resolve('/assessments/attempts/attempt-1/result').name).toBe('assessment-result')
    expect(router.resolve('/certificates/CERT-123').name).toBe('certificate-public')
    expect(router.resolve('/mini-projects/public').name).toBe('mini-projects-public')
    expect(router.resolve('/mini-projects/public/attempt-1').name).toBe('mini-project-public-detail')
  })

  it('registers recruiter and personalized assessment token routes', () => {
    expect(router.resolve('/recruiter').name).toBe('recruiter-home')
    expect(router.resolve('/recruiter/job-analysis').name).toBe('recruiter-job-analysis')
    expect(router.resolve('/recruiter/assessments/new').name).toBe('recruiter-assessment-new')
    expect(router.resolve('/recruiter/assessments/assessment-1').name).toBe('recruiter-assessment-detail')
    expect(router.resolve('/recruiter/assessments/assessment-1/edit').name).toBe('recruiter-assessment-edit')
    expect(router.resolve('/recruiter/assessments/assessment-1/attempts').name).toBe('recruiter-assessment-attempts')
    expect(router.resolve('/recruiter/attempts/attempt-1').name).toBe('recruiter-attempt-detail')
    expect(router.resolve('/recruiter/questions/open/new').name).toBe('recruiter-open-question-new')
    expect(router.resolve('/assessments/personalized/token-1/start').name).toBe('personalized-assessment-start')
  })

  it('restricts recruiter routes to recruiters and admins', () => {
    const recruiterRoute = router.resolve('/recruiter/assessments/new')
    const personalizedTokenRoute = router.resolve('/assessments/personalized/token-1/start')
    const miniProjectsRoute = router.resolve('/mini-projects/public')
    const miniProjectDetailRoute = router.resolve('/mini-projects/public/attempt-1')

    expect(recruiterRoute.meta.roles).toEqual(['RECRUITER', 'ADMIN'])
    expect(personalizedTokenRoute.meta.roles).toBeUndefined()
    expect(miniProjectsRoute.meta.roles).toBeUndefined()
    expect(miniProjectDetailRoute.meta.roles).toBeUndefined()
  })

  it('registers admin routes restricted to admins', () => {
    const adminRoutes = [
      ['/admin', 'admin-home'],
      ['/admin/users', 'admin-users'],
      ['/admin/questions', 'admin-questions'],
      ['/admin/questions/question-1', 'admin-question-detail'],
      ['/admin/metrics/questions/question-1', 'admin-question-metrics'],
      ['/admin/assessments/attempts', 'admin-assessment-attempts'],
      ['/admin/algorithms/genetic', 'admin-genetic-algorithm'],
    ] as const

    for (const [path, name] of adminRoutes) {
      const route = router.resolve(path)

      expect(route.name).toBe(name)
      expect(route.meta.roles).toEqual(['ADMIN'])
    }
  })
})
