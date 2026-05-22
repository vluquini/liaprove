import { http } from '@/shared/api/http'

export type MiniProjectVoteType = 'APPROVE' | 'REJECT'

export interface PublicMiniProjectAttemptResponse {
  attemptId: string
  assessmentTitle: string | null
  authorName: string | null
  repositoryLink: string | null
  finishedAt: string | null
}

export interface SubmitMiniProjectFeedbackRequest {
  comment: string
}

export async function listPublicMiniProjectAttempts(): Promise<PublicMiniProjectAttemptResponse[]> {
  const response = await http.get<PublicMiniProjectAttemptResponse[]>('/v1/assessment-attempts/mini-project/public')
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
