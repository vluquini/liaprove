import type { AuthenticatedUserResponse, UserRole } from '@/shared/types/auth'

const DEFAULT_DEV_EMAIL = 'junior.dev@example.com'
const DEFAULT_DEV_ID = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'
const DEFAULT_DEV_NAME = 'Junior Dev'
const DEFAULT_DEV_ROLE: UserRole = 'PROFESSIONAL'

function parseRole(value: string | undefined): UserRole {
  if (value === 'PROFESSIONAL' || value === 'RECRUITER' || value === 'ADMIN') {
    return value
  }

  return DEFAULT_DEV_ROLE
}

export const isDevAuthBypassEnabled = import.meta.env.VITE_AUTH_MODE === 'dev'

export const devAuthUser: AuthenticatedUserResponse = {
  id: import.meta.env.VITE_DEV_USER_ID || DEFAULT_DEV_ID,
  name: import.meta.env.VITE_DEV_USER_NAME || DEFAULT_DEV_NAME,
  email: import.meta.env.VITE_DEV_USER_EMAIL || DEFAULT_DEV_EMAIL,
  role: parseRole(import.meta.env.VITE_DEV_USER_ROLE),
  status: 'ACTIVE',
}
