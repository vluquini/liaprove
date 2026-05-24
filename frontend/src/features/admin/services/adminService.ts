import { http } from '@/shared/api/http'
import type { ExperienceLevel, UserRole } from '@/shared/types/auth'

export type KnowledgeArea = 'SOFTWARE_DEVELOPMENT' | 'DATABASE' | 'CYBERSECURITY' | 'NETWORKS' | 'AI'
export type DifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD'
export type RelevanceLevel = 'ONE' | 'TWO' | 'THREE' | 'FOUR' | 'FIVE'
export type QuestionType = 'MULTIPLE_CHOICE' | 'PROJECT' | 'OPEN'
export type QuestionStatus = 'SUBMITTED' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'NEEDS_REVISION'
export type VoteType = 'APPROVE' | 'REJECT'
export type ReactionType = 'LIKE' | 'DISLIKE'
export type AssessmentAttemptStatus = 'IN_PROGRESS' | 'COMPLETED' | 'APPROVED' | 'FAILED'
export type PersonalizedAssessmentStatus = 'ACTIVE' | 'DEACTIVATED' | 'EXPIRED'
export type OpenQuestionVisibility = 'PRIVATE' | 'SHARED'

export interface AdminUserResponse {
  id: string
  name: string
  email: string
  occupation?: string | null
  bio?: string | null
  experienceLevel?: ExperienceLevel | null
  hardSkills?: string[] | null
  softSkills?: string[] | null
  role: UserRole
  companyName?: string | null
  companyEmail?: string | null
}

export interface ListAdminUsersParams {
  name?: string
  role?: UserRole
  page?: number
  size?: number
}

export interface AlternativeResponse {
  id: string
  text: string
}

export interface AdminQuestionResponse {
  type: QuestionType
  id: string
  authorId: string
  title: string
  description: string
  knowledgeAreas: KnowledgeArea[]
  difficultyByCommunity?: DifficultyLevel | null
  relevanceByCommunity?: RelevanceLevel | null
  relevanceByLLM?: RelevanceLevel | null
  submissionDate?: string | null
  status: QuestionStatus
  alternatives?: AlternativeResponse[]
  guideline?: string | null
  visibility?: OpenQuestionVisibility | null
}

export interface ListAdminQuestionsParams {
  knowledgeAreas?: KnowledgeArea[]
  difficultyLevel?: DifficultyLevel
  status?: QuestionStatus
  authorId?: string
  page?: number
  size?: number
}

export interface UpdateAdminQuestionRequest {
  title?: string
  description?: string
  knowledgeAreas?: KnowledgeArea[]
  alternatives?: AlternativeResponse[]
}

export interface VoteResponse {
  id: string
  user: AdminUserResponse
  voteType: VoteType
  createdAt: string
}

export interface AuthorResponse {
  id: string
  name: string
}

export interface FeedbackReactionResponse {
  id: string
  userId: string
  userName: string
  type: ReactionType
  createdAt: string
}

export interface FeedbackQuestionResponse {
  id: string
  comment: string
  author: AuthorResponse
  submissionDate: string
  reactions: FeedbackReactionResponse[]
}

export interface AssessmentCriteriaWeightsResponse {
  hardSkillsWeight: number
  softSkillsWeight: number
  experienceWeight: number
}

export interface JobDescriptionAnalysisResponse {
  originalJobDescription: string
  suggestedKnowledgeAreas: KnowledgeArea[]
  suggestedHardSkills: string[]
  suggestedSoftSkills: string[]
  suggestedCriteriaWeights: AssessmentCriteriaWeightsResponse
}

export interface AdminAssessmentAttemptSummaryResponse {
  attemptId: string
  status: AssessmentAttemptStatus
  accuracyRate: number | null
  startedAt: string
  finishedAt?: string | null
  assessment: {
    id: string
    title: string
    personalized: boolean
    criteriaWeights?: AssessmentCriteriaWeightsResponse | null
    jobDescriptionAnalysis?: JobDescriptionAnalysisResponse | null
  }
  candidate: AdminUserResponse
}

export interface ListAdminAssessmentAttemptsParams {
  isPersonalized?: boolean
  startDate?: string
  endDate?: string
  statuses?: AssessmentAttemptStatus[]
}

export interface RecruiterVoteWeightResponse {
  id: string
  name: string
  email: string
  companyName?: string | null
  companyEmail?: string | null
  voteWeight: number | null
  multiplier: number | null
}

export interface ListRecruiterVoteWeightsParams {
  name?: string
  page?: number
  size?: number
}

export type AdjustGeneticWeightsResponse = Record<string, number>

export async function listAdminUsers(params: ListAdminUsersParams = {}): Promise<AdminUserResponse[]> {
  const response = await http.get<AdminUserResponse[]>('/v1/admin/users', {
    params: toAdminUserSearchParams(params),
  })
  return response.data
}

export async function activateAdminUser(id: string): Promise<void> {
  await http.patch(`/v1/admin/users/${id}/activate`)
}

export async function deactivateAdminUser(id: string): Promise<void> {
  await http.patch(`/v1/admin/users/${id}/deactivate`)
}

export async function deleteAdminUser(id: string): Promise<void> {
  await http.delete(`/v1/admin/users/${id}`)
}

export async function listAdminQuestions(
  params: ListAdminQuestionsParams = {},
): Promise<AdminQuestionResponse[]> {
  const response = await http.get<AdminQuestionResponse[]>('/v1/admin/questions', {
    params: toAdminQuestionSearchParams(params),
  })
  return response.data
}

export async function getAdminQuestion(questionId: string): Promise<AdminQuestionResponse> {
  const response = await http.get<AdminQuestionResponse>(`/v1/admin/questions/${questionId}`)
  return response.data
}

export async function updateAdminQuestion(
  questionId: string,
  request: UpdateAdminQuestionRequest,
): Promise<AdminQuestionResponse> {
  const response = await http.put<AdminQuestionResponse>(`/v1/admin/questions/${questionId}`, request)
  return response.data
}

export async function moderateAdminQuestion(
  questionId: string,
  newStatus: QuestionStatus,
): Promise<AdminQuestionResponse> {
  const response = await http.patch<AdminQuestionResponse>(`/v1/admin/questions/${questionId}/moderate`, {
    newStatus,
  })
  return response.data
}

export async function listQuestionVotes(questionId: string): Promise<VoteResponse[]> {
  const response = await http.get<VoteResponse[]>(`/v1/admin/questions/${questionId}/votes`)
  return response.data
}

export async function listQuestionFeedbacks(questionId: string): Promise<FeedbackQuestionResponse[]> {
  const response = await http.get<FeedbackQuestionResponse[]>(`/v1/admin/questions/${questionId}/feedbacks`)
  return response.data
}

export async function listAdminAssessmentAttempts(
  params: ListAdminAssessmentAttemptsParams = {},
): Promise<AdminAssessmentAttemptSummaryResponse[]> {
  const response = await http.get<AdminAssessmentAttemptSummaryResponse[]>('/v1/admin/assessments/attempts', {
    params: toAdminAttemptSearchParams(params),
  })
  return response.data
}

export async function listRecruiterVoteWeights(
  params: ListRecruiterVoteWeightsParams = {},
): Promise<RecruiterVoteWeightResponse[]> {
  const response = await http.get<RecruiterVoteWeightResponse[]>('/v1/admin/algorithms/genetic/recruiters/weights', {
    params: toRecruiterWeightSearchParams(params),
  })
  return response.data
}

export async function adjustGeneticWeights(dryRun: boolean): Promise<AdjustGeneticWeightsResponse> {
  const response = await http.post<AdjustGeneticWeightsResponse>('/v1/admin/algorithms/genetic/adjust', {
    dryRun,
  })
  return response.data
}

export async function getRoleMultiplier(role: UserRole): Promise<number> {
  const response = await http.get<number>(`/v1/admin/algorithms/genetic/roles/${role}/multiplier`)
  return response.data
}

export async function setRoleMultiplier(role: UserRole, multiplier: number): Promise<void> {
  await http.patch(`/v1/admin/algorithms/genetic/roles/${role}/multiplier`, { multiplier })
}

export async function getRecruiterMultiplier(recruiterId: string): Promise<number | null> {
  const response = await http.get<number | null>(
    `/v1/admin/algorithms/genetic/recruiters/${recruiterId}/multiplier`,
  )
  return response.data
}

export async function setRecruiterMultiplier(recruiterId: string, multiplier: number): Promise<void> {
  await http.patch(`/v1/admin/algorithms/genetic/recruiters/${recruiterId}/multiplier`, { multiplier })
}

export async function setRecruiterVoteWeight(recruiterId: string, weight: number): Promise<void> {
  await http.patch(`/v1/admin/algorithms/genetic/recruiters/${recruiterId}/vote-weight`, { weight })
}

function toAdminUserSearchParams(params: ListAdminUsersParams): URLSearchParams {
  const searchParams = new URLSearchParams()
  appendOptional(searchParams, 'name', params.name)
  appendOptional(searchParams, 'role', params.role)
  appendOptionalNumber(searchParams, 'page', params.page)
  appendOptionalNumber(searchParams, 'size', params.size)
  return searchParams
}

function toAdminQuestionSearchParams(params: ListAdminQuestionsParams): URLSearchParams {
  const searchParams = new URLSearchParams()
  appendAll(searchParams, 'knowledgeAreas', params.knowledgeAreas)
  appendOptional(searchParams, 'difficultyLevel', params.difficultyLevel)
  appendOptional(searchParams, 'status', params.status)
  appendOptional(searchParams, 'authorId', params.authorId)
  appendOptionalNumber(searchParams, 'page', params.page)
  appendOptionalNumber(searchParams, 'size', params.size)
  return searchParams
}

function toAdminAttemptSearchParams(params: ListAdminAssessmentAttemptsParams): URLSearchParams {
  const searchParams = new URLSearchParams()
  appendOptionalBoolean(searchParams, 'isPersonalized', params.isPersonalized)
  appendOptional(searchParams, 'startDate', params.startDate)
  appendOptional(searchParams, 'endDate', params.endDate)
  appendAll(searchParams, 'statuses', params.statuses)
  return searchParams
}

function toRecruiterWeightSearchParams(params: ListRecruiterVoteWeightsParams): URLSearchParams {
  const searchParams = new URLSearchParams()
  appendOptional(searchParams, 'name', params.name)
  appendOptionalNumber(searchParams, 'page', params.page)
  appendOptionalNumber(searchParams, 'size', params.size)
  return searchParams
}

function appendAll(searchParams: URLSearchParams, key: string, values: string[] | undefined): void {
  values?.forEach((value) => searchParams.append(key, value))
}

function appendOptional(searchParams: URLSearchParams, key: string, value: string | undefined): void {
  if (value !== undefined && value !== '') {
    searchParams.set(key, value)
  }
}

function appendOptionalNumber(searchParams: URLSearchParams, key: string, value: number | undefined): void {
  if (value !== undefined) {
    searchParams.set(key, String(value))
  }
}

function appendOptionalBoolean(searchParams: URLSearchParams, key: string, value: boolean | undefined): void {
  if (value !== undefined) {
    searchParams.set(key, String(value))
  }
}
