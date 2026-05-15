import type { RouteLocationNormalized, RouteLocationRaw } from 'vue-router'
import { isDevAuthBypassEnabled } from '@/shared/config/authMode'
import { useAuthStore } from '@/shared/stores/auth'
import type { UserRole } from '@/shared/types/auth'

export function dashboardRouteForRole(_role?: UserRole): string {
  return '/dashboard'
}

export function requireAuth(
  to: RouteLocationNormalized,
): RouteLocationRaw | undefined {
  const auth = useAuthStore()
  const allowedRoles = to.meta.roles as UserRole[] | undefined

  if (!isDevAuthBypassEnabled && !auth.isAuthenticated) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (allowedRoles && !auth.hasAnyRole(allowedRoles)) {
    return '/forbidden'
  }
}

export function redirectAuthenticated(
  _to: RouteLocationNormalized,
): RouteLocationRaw | undefined {
  const auth = useAuthStore()

  if (!isDevAuthBypassEnabled && auth.isAuthenticated) {
    return dashboardRouteForRole(auth.user?.role)
  }
}
