import { http } from '@/shared/api/http'

export type MiniProjectVoteType = 'APPROVE' | 'REJECT'
export type MiniProjectReactionType = 'LIKE' | 'DISLIKE'
export type MiniProjectKnowledgeArea = 'SOFTWARE_DEVELOPMENT' | 'DATABASE' | 'CYBERSECURITY' | 'NETWORKS' | 'AI'
export type MiniProjectDifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD'
export type MiniProjectRelevanceLevel = 'ONE' | 'TWO' | 'THREE' | 'FOUR' | 'FIVE'

export interface PublicMiniProjectAttemptResponse {
  attemptId: string
  assessmentTitle: string | null
  authorName: string | null
  repositoryLink: string | null
  finishedAt: string | null
}

export interface MiniProjectAuthorResponse {
  id: string
  name: string
}

export interface PublicMiniProjectQuestionResponse {
  id: string
  title: string
  description: string
  knowledgeAreas: MiniProjectKnowledgeArea[]
  difficulty: MiniProjectDifficultyLevel
  relevance: MiniProjectRelevanceLevel
}

export interface MiniProjectVoteSummaryResponse {
  approves: number
  rejects: number
}

export interface MiniProjectFeedbackReactionResponse {
  id: string
  userId: string
  userName: string
  type: MiniProjectReactionType
  createdAt: string
}

export interface MiniProjectFeedbackResponse {
  id: string
  comment: string
  author: MiniProjectAuthorResponse
  submissionDate: string
  reactions: MiniProjectFeedbackReactionResponse[]
}

export interface PublicMiniProjectAttemptDetailResponse extends PublicMiniProjectAttemptResponse {
  textResponse: string | null
  question: PublicMiniProjectQuestionResponse
  voteSummary: MiniProjectVoteSummaryResponse
  feedbacks: MiniProjectFeedbackResponse[]
}

export interface SubmitMiniProjectFeedbackRequest {
  comment: string
}

export async function listPublicMiniProjectAttempts(): Promise<PublicMiniProjectAttemptResponse[]> {
  const response = await http.get<PublicMiniProjectAttemptResponse[]>('/v1/assessment-attempts/mini-project/public')
  return response.data
}

export async function getPublicMiniProjectAttemptDetails(
  attemptId: string,
): Promise<PublicMiniProjectAttemptDetailResponse> {
  const response = await http.get<PublicMiniProjectAttemptDetailResponse>(
    `/v1/assessment-attempts/mini-project/public/${attemptId}`,
  )
  return response.data
}

export async function castMiniProjectAttemptVote(
  attemptId: string,
  voteType: MiniProjectVoteType,
): Promise<void> {
  await http.post(`/v1/assessment-attempts/${attemptId}/vote`, { voteType })
}

export async function submitMiniProjectAttemptFeedback(
  attemptId: string,
  request: SubmitMiniProjectFeedbackRequest,
): Promise<void> {
  await http.post(`/v1/assessment-attempts/${attemptId}/feedback`, request)
}

export async function reactToMiniProjectAttemptFeedback(
  feedbackId: string,
  reactionType: MiniProjectReactionType,
): Promise<void> {
  await http.post(`/v1/assessment-feedbacks/${feedbackId}/react`, { reactionType })
}
