import { http } from '@/shared/api/http'

export type KnowledgeArea = 'SOFTWARE_DEVELOPMENT' | 'DATABASE' | 'CYBERSECURITY' | 'NETWORKS' | 'AI'
export type DifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD'
export type SystemAssessmentType = 'MULTIPLE_CHOICE' | 'PROJECT'
export type AssessmentAttemptStatus = 'IN_PROGRESS' | 'COMPLETED' | 'APPROVED' | 'FAILED'

export interface StartSystemAssessmentRequest {
  knowledgeArea: KnowledgeArea
  difficultyLevel: DifficultyLevel
  type: SystemAssessmentType
}

export interface AttemptAlternativeResponse {
  id: string
  text: string
}

export interface AttemptQuestionResponse {
  id: string
  title: string
  description: string
  alternatives?: AttemptAlternativeResponse[] | null
}

export interface AssessmentAttemptResponse {
  attemptId: string
  assessmentTitle: string
  startedAt: string
  evaluationTimerMinutes: number
  questions: AttemptQuestionResponse[]
}

export interface SubmitAssessmentAnswerRequest {
  questionId: string
  selectedAlternativeId?: string | null
  projectUrl?: string | null
  textResponse?: string | null
}

export interface SubmitAssessmentRequest {
  answers: SubmitAssessmentAnswerRequest[]
}

export interface AssessmentResultResponse {
  status: AssessmentAttemptStatus
  accuracyRate: number | null
  certificateUrl: string | null
  message: string
}

export async function startSystemAssessment(
  request: StartSystemAssessmentRequest,
): Promise<AssessmentAttemptResponse> {
  const response = await http.post<AssessmentAttemptResponse>('/v1/assessments/start-system', request)
  return response.data
}

export async function submitAssessment(
  attemptId: string,
  request: SubmitAssessmentRequest,
): Promise<AssessmentResultResponse> {
  const response = await http.post<AssessmentResultResponse>(`/v1/assessments/${attemptId}/submit`, request)
  return response.data
}
