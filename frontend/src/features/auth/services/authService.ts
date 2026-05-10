import { http } from '@/shared/api/http'
import type { AuthenticationRequest, AuthenticationResponse, CreateUserRequest } from '@/shared/types/auth'

export async function login(request: AuthenticationRequest): Promise<AuthenticationResponse> {
  const response = await http.post<AuthenticationResponse>('/auth/login', request)
  return response.data
}

export async function register(request: CreateUserRequest): Promise<AuthenticationResponse> {
  const response = await http.post<AuthenticationResponse>('/auth/register', request)
  return response.data
}
