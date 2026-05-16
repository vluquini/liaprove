import axios from 'axios'
import { devAuthUser, isDevAuthBypassEnabled } from '@/shared/config/authMode'
import { clearStoredSession, isSessionExpired, readStoredSession } from '@/shared/utils/session'

declare module 'axios' {
  export interface AxiosRequestConfig {
    skipSessionExpiredRedirect?: boolean
  }
}

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
})

http.interceptors.request.use((config) => {
  if (isDevAuthBypassEnabled) {
    config.headers.set('X-Dev-User-Email', devAuthUser.email)
    return config
  }

  const session = readStoredSession()

  if (!session) {
    return config
  }

  if (isSessionExpired(session.expiresAt)) {
    clearStoredSession()
    return config
  }

  config.headers.Authorization = `${session.tokenType} ${session.token}`
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.skipSessionExpiredRedirect) {
      clearStoredSession()
      window.dispatchEvent(new CustomEvent('liaprove:session-expired'))
    }

    return Promise.reject(error)
  },
)
