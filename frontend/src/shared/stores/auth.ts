import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { login as loginRequest, register as registerRequest } from '@/features/auth/services/authService'
import {
  clearStoredSession,
  isSessionExpired,
  readStoredSession,
  writeStoredSession,
} from '@/shared/utils/session'
import type {
  AuthenticationRequest,
  AuthenticatedUserResponse,
  CreateUserRequest,
  StoredSession,
  UserRole,
} from '@/shared/types/auth'

function toSession(response: StoredSession): StoredSession {
  return {
    token: response.token,
    tokenType: response.tokenType || 'Bearer',
    expiresAt: response.expiresAt,
    user: response.user,
  }
}

export const useAuthStore = defineStore('auth', () => {
  const storedSession = readStoredSession()
  const session = ref<StoredSession | null>(
    storedSession && !isSessionExpired(storedSession.expiresAt) ? storedSession : null,
  )

  if (storedSession && isSessionExpired(storedSession.expiresAt)) {
    clearStoredSession()
  }

  const user = computed<AuthenticatedUserResponse | null>(() => session.value?.user ?? null)
  const isAuthenticated = computed(() => Boolean(session.value) && !isExpired())

  function isExpired(): boolean {
    return session.value ? isSessionExpired(session.value.expiresAt) : true
  }

  function saveSession(nextSession: StoredSession): void {
    session.value = toSession(nextSession)
    writeStoredSession(session.value)
  }

  async function login(request: AuthenticationRequest): Promise<void> {
    const response = await loginRequest(request)
    saveSession(response)
  }

  async function register(request: CreateUserRequest): Promise<void> {
    const response = await registerRequest(request)
    saveSession(response)
  }

  function logout(): void {
    session.value = null
    clearStoredSession()
  }

  function updateUser(nextUser: AuthenticatedUserResponse): void {
    if (!session.value) {
      return
    }

    saveSession({
      ...session.value,
      user: {
        ...session.value.user,
        ...nextUser,
      },
    })
  }

  function hasAnyRole(roles: UserRole[]): boolean {
    return Boolean(user.value && roles.includes(user.value.role))
  }

  return {
    session,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    updateUser,
    hasAnyRole,
  }
})
