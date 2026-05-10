import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useAuthStore } from '@/shared/stores/auth'
import type { UserRole } from '@/shared/types/auth'

export function dashboardRouteForRole(_role?: UserRole): string {
  return '/dashboard'
}

export function requireAuth(
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext,
): void {
  const auth = useAuthStore()
  const allowedRoles = to.meta.roles as UserRole[] | undefined

  if (!auth.isAuthenticated) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (allowedRoles && !auth.hasAnyRole(allowedRoles)) {
    next('/forbidden')
    return
  }

  next()
}

export function redirectAuthenticated(
  _to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext,
): void {
  const auth = useAuthStore()

  if (auth.isAuthenticated) {
    next(dashboardRouteForRole(auth.user?.role))
    return
  }

  next()
}
