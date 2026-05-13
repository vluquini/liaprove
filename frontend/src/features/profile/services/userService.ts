import { http } from '@/shared/api/http'
import type { AuthenticatedUserResponse, ExperienceLevel } from '@/shared/types/auth'

export interface UserProfileResponse extends AuthenticatedUserResponse {
  bio?: string | null
  hardSkills?: string[] | null
  softSkills?: string[] | null
}

export interface UpdateUserProfileRequest {
  name?: string
  email?: string
  occupation?: string
  bio?: string
  experienceLevel?: ExperienceLevel | null
  hardSkills?: string[]
  softSkills?: string[]
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export async function getUserProfile(id: string): Promise<UserProfileResponse> {
  const response = await http.get<UserProfileResponse>(`/v1/users/${id}`)
  return response.data
}

export async function updateUserProfile(
  id: string,
  request: UpdateUserProfileRequest,
): Promise<UserProfileResponse> {
  const response = await http.put<UserProfileResponse>(`/v1/users/${id}`, request)
  return response.data
}

export async function changePassword(id: string, request: ChangePasswordRequest): Promise<void> {
  await http.patch(`/v1/users/${id}/password`, request)
}

export async function deactivateOwnAccount(): Promise<void> {
  await http.patch('/v1/users/me/deactivate')
}
