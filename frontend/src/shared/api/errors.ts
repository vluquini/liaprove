import axios, { type AxiosError } from 'axios'

export interface ApiError {
  status?: number
  message: string
  fieldErrors?: Record<string, string>
}

type BackendErrorBody = {
  message?: string
  error?: string
  errors?: Record<string, string>
  fieldErrors?: Record<string, string>
}

export function normalizeApiError(error: unknown): ApiError {
  if (!axios.isAxiosError(error)) {
    return { message: 'Não foi possível concluir a operação.' }
  }

  const axiosError = error as AxiosError<BackendErrorBody>
  const body = axiosError.response?.data

  return {
    status: axiosError.response?.status,
    message: body?.message ?? body?.error ?? 'Não foi possível concluir a operação.',
    fieldErrors: body?.fieldErrors ?? body?.errors,
  }
}
