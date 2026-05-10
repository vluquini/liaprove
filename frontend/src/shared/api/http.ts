import axios from 'axios'
import { clearStoredSession, isSessionExpired, readStoredSession } from '@/shared/utils/session'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
})

http.interceptors.request.use((config) => {
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
    if (error.response?.status === 401) {
      clearStoredSession()
      window.dispatchEvent(new CustomEvent('liaprove:session-expired'))
    }

    return Promise.reject(error)
  },
)
