import type { AuthenticationResponse, AuthenticatedUserResponse, UserRole } from '@/shared/types/auth'

export function makeUser(role: UserRole = 'PROFESSIONAL'): AuthenticatedUserResponse {
  return {
    id: 'f66a1a44-40f4-4430-9b1b-eeb1df2e2eb0',
    name: 'Ana Silva',
    email: 'ana@example.com',
    role,
    status: 'ACTIVE',
  }
}

export function makeAuthResponse(role: UserRole = 'PROFESSIONAL'): AuthenticationResponse {
  return {
    token: 'test-token',
    tokenType: 'Bearer',
    expiresAt: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
    user: makeUser(role),
  }
}
