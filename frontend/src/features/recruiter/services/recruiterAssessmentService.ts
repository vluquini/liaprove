import { http } from '@/shared/api/http'

export type KnowledgeArea = 'SOFTWARE_DEVELOPMENT' | 'DATABASE' | 'CYBERSECURITY' | 'NETWORKS' | 'AI'
export type DifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD'
export type QuestionType = 'MULTIPLE_CHOICE' | 'PROJECT' | 'OPEN'
export type RelevanceLevel = 'ONE' | 'TWO' | 'THREE' | 'FOUR' | 'FIVE'
export type PersonalizedAssessmentStatus = 'ACTIVE' | 'DEACTIVATED' | 'EXPIRED'
export type AssessmentAttemptStatus = 'IN_PROGRESS' | 'COMPLETED' | 'APPROVED' | 'FAILED'
export type OpenQuestionVisibility = 'PRIVATE' | 'SHARED_WITH_RECRUITERS'

export interface CriteriaWeights {
  hardSkillsWeight: number
  softSkillsWeight: number
  experienceWeight: number
}

export interface JobDescriptionAnalysisResponse {
  originalJobDescription: string
  suggestedKnowledgeAreas: KnowledgeArea[]
  suggestedHardSkills: string[]
  suggestedSoftSkills: string[]
  suggestedCriteriaWeights: CriteriaWeights
}

export interface JobDescriptionAnalysisSnapshotRequest {
  originalJobDescription: string
  suggestedKnowledgeAreas: KnowledgeArea[]
  suggestedHardSkills: string[]
  suggestedSoftSkills: string[]
  suggestedHardSkillsWeight?: number
  suggestedSoftSkillsWeight?: number
  suggestedExperienceWeight?: number
}

export interface UserSummaryResponse {
  id: string
  name: string
  email: string
  role: string
}

export interface PersonalizedAssessmentQuestionResponse {
  id: string
  title: string
  knowledgeAreas: KnowledgeArea[]
  difficultyByCommunity?: DifficultyLevel | null
  submissionDate?: string | null
}

export interface PersonalizedAssessmentDetailsResponse {
  id: string
  title: string
  description: string
  creationDate: string
  evaluationTimerMinutes: number | null
  expirationDate: string
  totalAttempts: number
  maxAttempts: number
  shareableToken: string
  status: PersonalizedAssessmentStatus
  createdBy?: UserSummaryResponse | null
  criteriaWeights: CriteriaWeights | null
  jobDescriptionAnalysis: JobDescriptionAnalysisResponse | null
  questions: PersonalizedAssessmentQuestionResponse[]
}

export interface SuggestionParams {
  knowledgeAreas?: KnowledgeArea[]
  difficultyLevels?: DifficultyLevel[]
  questionTypes?: QuestionType[]
  excludeIds?: string[]
  page?: number
  pageSize?: number
}

export interface ScoredQuestionResponse {
  id: string
  title: string
  knowledgeAreas: KnowledgeArea[]
  difficultyByCommunity?: DifficultyLevel | null
  submissionDate?: string | null
  score: number
}

export interface SuggestedQuestionsResponse {
  questions: ScoredQuestionResponse[]
  page: number
  pageSize: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface CreatePersonalizedAssessmentRequest {
  title: string
  description: string
  questionIds: string[]
  expirationDate: string
  maxAttempts: number
  evaluationTimerMinutes: number
  hardSkillsWeight?: number
  softSkillsWeight?: number
  experienceWeight?: number
  jobDescriptionAnalysis?: JobDescriptionAnalysisSnapshotRequest | null
}

export interface PersonalizedAssessmentResponse {
  id: string
  title: string
  shareableToken: string
  status: PersonalizedAssessmentStatus
  criteriaWeights?: CriteriaWeights | null
  jobDescriptionAnalysis?: JobDescriptionAnalysisResponse | null
}

export interface UpdatePersonalizedAssessmentRequest {
  expirationDate?: string
  maxAttempts?: number
  status?: PersonalizedAssessmentStatus
  hardSkillsWeight?: number
  softSkillsWeight?: number
  experienceWeight?: number
}

export interface UpdatePersonalizedAssessmentResponse {
  assessmentId?: string
  id?: string
  expirationDate: string
  maxAttempts: number
  status: PersonalizedAssessmentStatus
  criteriaWeights?: CriteriaWeights | null
  jobDescriptionAnalysis?: JobDescriptionAnalysisResponse | null
}

export interface DeletePersonalizedAssessmentResponse {
  assessmentId?: string
  id?: string
  deleted?: boolean
  message?: string
}

export interface AssessmentAttemptSummaryResponse {
  attemptId: string
  candidateId: string
  candidateName: string
  candidateEmail: string
  assessmentId: string
  assessmentTitle: string
  status: AssessmentAttemptStatus
  accuracyRate: number | null
  startedAt: string
  submittedAt?: string | null
}

export interface AssessmentAttemptDetailsResponse {
  attemptId: string
  status: AssessmentAttemptStatus
  accuracyRate: number | null
  startedAt: string
  finishedAt?: string | null
  assessment: AssessmentAttemptAssessmentResponse
  candidate: AssessmentAttemptCandidateResponse
  explainability: AssessmentExplainabilityResponse
  questions: AttemptQuestionDetailsResponse[]
}

export interface AssessmentAttemptAssessmentResponse {
  id: string
  title: string
  description: string
  evaluationTimerMinutes: number | null
  criteriaWeights: CriteriaWeights | null
  jobDescriptionAnalysis: JobDescriptionAnalysisResponse | null
}

export interface AssessmentAttemptCandidateResponse {
  id: string
  name: string
  email: string
  role?: string
  experienceLevel?: string | null
  hardSkills?: string[] | null
  softSkills?: string[] | null
}

export interface AssessmentExplainabilityResponse {
  totalQuestions: number
  answeredQuestions: number
  multipleChoiceQuestions: number
  openQuestions: number
  projectQuestions: number
  candidateExperienceLevel?: string | null
  candidateHardSkills: string[]
  candidateSoftSkills: string[]
  criteriaWeights: CriteriaWeights | null
}

export interface AttemptQuestionDetailsResponse {
  id: string
  title: string
  description: string
  guideline?: string | null
  alternatives: AttemptAlternativeResponse[]
  answer: AttemptAnswerResponse | null
}

export interface AttemptAlternativeResponse {
  id: string
  text: string
}

export interface AttemptAnswerResponse {
  questionId: string
  selectedAlternativeId?: string | null
  projectUrl?: string | null
  textResponse?: string | null
}

export interface LegacyAssessmentAttemptDetailsResponse {
  attemptId: string
  candidate: {
    id: string
    name: string
    email: string
  }
  assessment: {
    id: string
    title: string
  }
  status: AssessmentAttemptStatus
  accuracyRate: number | null
  answers: unknown[]
  explainability?: unknown
}

export interface AttemptPreAnalysisResponse {
  metadata: {
    attemptId: string
    generatedAt: string
    ignoredQuestionTypes: QuestionType[]
  }
  analysis: {
    summary: string
    strengths: string[]
    attentionPoints: string[]
    finalExplanation: string
  }
}

export interface EvaluateAssessmentAttemptResponse {
  attemptId: string
  finalStatus?: 'APPROVED' | 'FAILED'
  status?: AssessmentAttemptStatus
  [key: string]: unknown
}

export interface CreateOpenQuestionRequest {
  title: string
  description: string
  knowledgeAreas: KnowledgeArea[]
  difficultyByCommunity: DifficultyLevel
  relevanceByCommunity: RelevanceLevel
  guideline: string
  visibility?: OpenQuestionVisibility | null
}

export interface QuestionResponse {
  id: string
  title: string
  type?: QuestionType
  [key: string]: unknown
}

export async function listPersonalizedAssessments(): Promise<PersonalizedAssessmentDetailsResponse[]> {
  const response = await http.get<PersonalizedAssessmentDetailsResponse[]>('/v1/assessments/personalized')
  return response.data
}

export async function getPersonalizedAssessment(id: string): Promise<PersonalizedAssessmentDetailsResponse> {
  const response = await http.get<PersonalizedAssessmentDetailsResponse>(`/v1/assessments/personalized/${id}`)
  return response.data
}

export async function analyzeJobDescription(jobDescription: string): Promise<JobDescriptionAnalysisResponse> {
  const response = await http.post<JobDescriptionAnalysisResponse>(
    '/v1/assessments/personalized/job-description-analysis',
    { jobDescription },
  )
  return response.data
}

export async function listSuggestedQuestions(params: SuggestionParams): Promise<SuggestedQuestionsResponse> {
  const response = await http.get<SuggestedQuestionsResponse>('/v1/assessments/personalized/suggestions', {
    params: toSuggestionSearchParams(params),
  })
  return response.data
}

export async function createPersonalizedAssessment(
  request: CreatePersonalizedAssessmentRequest,
): Promise<PersonalizedAssessmentResponse> {
  const response = await http.post<PersonalizedAssessmentResponse>('/v1/assessments/personalized', request)
  return response.data
}

export async function updatePersonalizedAssessment(
  id: string,
  request: UpdatePersonalizedAssessmentRequest,
): Promise<UpdatePersonalizedAssessmentResponse> {
  const response = await http.patch<UpdatePersonalizedAssessmentResponse>(
    `/v1/assessments/personalized/${id}`,
    request,
  )
  return response.data
}

export async function deletePersonalizedAssessment(id: string): Promise<DeletePersonalizedAssessmentResponse> {
  const response = await http.delete<DeletePersonalizedAssessmentResponse>(`/v1/assessments/personalized/${id}`)
  return response.data
}

export async function listAssessmentAttempts(assessmentId: string): Promise<AssessmentAttemptSummaryResponse[]> {
  const response = await http.get<AssessmentAttemptSummaryResponse[]>(
    `/v1/assessments/personalized/${assessmentId}/attempts`,
  )
  return response.data
}

export async function getAssessmentAttemptDetails(attemptId: string): Promise<AssessmentAttemptDetailsResponse> {
  const response = await http.get<AssessmentAttemptDetailsResponse>(`/v1/assessments/attempts/${attemptId}`)
  return response.data
}

export async function generateAttemptPreAnalysis(attemptId: string): Promise<AttemptPreAnalysisResponse> {
  const response = await http.post<AttemptPreAnalysisResponse>(
    `/v1/assessments/attempts/${attemptId}/pre-analysis`,
  )
  return response.data
}

export async function evaluateAssessmentAttempt(
  attemptId: string,
  finalStatus: 'APPROVED' | 'FAILED',
): Promise<EvaluateAssessmentAttemptResponse> {
  const response = await http.post<EvaluateAssessmentAttemptResponse>(`/v1/assessments/${attemptId}/evaluate`, {
    finalStatus,
  })
  return response.data
}

export async function createOpenQuestion(request: CreateOpenQuestionRequest): Promise<QuestionResponse> {
  const response = await http.post<QuestionResponse>('/v1/questions/open', {
    type: 'OPEN',
    ...request,
  })
  return response.data
}

function toSuggestionSearchParams(params: SuggestionParams): URLSearchParams {
  const searchParams = new URLSearchParams()

  appendAll(searchParams, 'knowledgeAreas', params.knowledgeAreas)
  appendAll(searchParams, 'difficultyLevels', params.difficultyLevels)
  appendAll(searchParams, 'questionTypes', params.questionTypes)
  appendAll(searchParams, 'excludeIds', params.excludeIds)

  if (params.page !== undefined) {
    searchParams.set('page', String(params.page))
  }

  if (params.pageSize !== undefined) {
    searchParams.set('pageSize', String(params.pageSize))
  }

  return searchParams
}

function appendAll(searchParams: URLSearchParams, key: string, values: string[] | undefined): void {
  values?.forEach((value) => searchParams.append(key, value))
}
