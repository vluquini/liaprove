export type UserRole = 'PROFESSIONAL' | 'RECRUITER' | 'ADMIN'
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING'
export type ExperienceLevel = 'JUNIOR' | 'PLENO' | 'SENIOR'

export interface AuthenticationRequest {
  email: string
  password: string
}

export interface CreateUserRequest {
  name: string
  email: string
  password: string
  occupation?: string
  bio?: string
  experienceLevel?: ExperienceLevel
  hardSkills?: string[]
  softSkills?: string[]
  role: UserRole
  companyName?: string
  companyEmail?: string
}

export interface AuthenticatedUserResponse {
  id: string
  name: string
  email: string
  role: UserRole
  status: UserStatus
  occupation?: string | null
  experienceLevel?: ExperienceLevel | null
  companyName?: string | null
  companyEmail?: string | null
}

export interface AuthenticationResponse {
  token: string
  tokenType: string
  expiresAt: string
  user: AuthenticatedUserResponse
}

export interface StoredSession {
  token: string
  tokenType: string
  expiresAt: string
  user: AuthenticatedUserResponse
}
