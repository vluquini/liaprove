import type { StoredSession } from '@/shared/types/auth'

const SESSION_STORAGE_KEY = 'liaprove.auth.session'

export function readStoredSession(): StoredSession | null {
  const rawSession = localStorage.getItem(SESSION_STORAGE_KEY)

  if (!rawSession) {
    return null
  }

  try {
    return JSON.parse(rawSession) as StoredSession
  } catch {
    localStorage.removeItem(SESSION_STORAGE_KEY)
    return null
  }
}

export function writeStoredSession(session: StoredSession): void {
  localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session))
}

export function clearStoredSession(): void {
  localStorage.removeItem(SESSION_STORAGE_KEY)
}

export function isSessionExpired(expiresAt: string): boolean {
  return new Date(expiresAt).getTime() <= Date.now()
}
