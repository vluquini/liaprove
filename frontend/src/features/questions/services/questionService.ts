import { http } from '@/shared/api/http'

export type KnowledgeArea = 'SOFTWARE_DEVELOPMENT' | 'DATABASE' | 'CYBERSECURITY' | 'NETWORKS' | 'AI'
export type DifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD'
export type RelevanceLevel = 'ONE' | 'TWO' | 'THREE' | 'FOUR' | 'FIVE'
export type VoteType = 'APPROVE' | 'REJECT'
export type ReactionType = 'LIKE' | 'DISLIKE'

export interface QuestionSummaryResponse {
  id: string
  authorId: string
  title: string
  knowledgeAreas: KnowledgeArea[]
  submissionDate: string
}

export interface AuthorResponse {
  id: string
  name: string
}

export interface AlternativeResponse {
  id: string
  text: string
}

export interface VoteSummaryResponse {
  approves: number
  rejects: number
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

export interface QuestionDetailResponse extends QuestionSummaryResponse {
  author: AuthorResponse
  description: string
  alternatives: AlternativeResponse[]
  voteSummary: VoteSummaryResponse
  feedbacks: FeedbackQuestionResponse[]
  relevanceByLLM?: RelevanceLevel | null
}

export interface ListVotingQuestionsParams {
  page: number
  size: number
}

export interface SubmitFeedbackQuestionRequest {
  comment: string
  difficultyLevel: DifficultyLevel
  knowledgeArea: KnowledgeArea
  relevanceLevel: RelevanceLevel
}

export interface AlternativeRequest {
  text: string
  correct: boolean
}

export interface SubmitMultipleChoiceQuestionRequest {
  type: 'MULTIPLE_CHOICE'
  title: string
  description: string
  knowledgeAreas: KnowledgeArea[]
  difficultyByCommunity: DifficultyLevel
  relevanceByCommunity: RelevanceLevel
  alternatives: AlternativeRequest[]
  acceptedLanguageSuggestions?: string[]
  acceptedBiasOrAmbiguityWarnings?: string[]
  acceptedDistractorSuggestions?: string[]
  acceptedDifficultyLevelByLLM?: string
  acceptedTopicConsistencyNotes?: string[]
}

export interface PreAnalyzeQuestionResponse {
  languageSuggestions: string[]
  biasOrAmbiguityWarnings: string[]
  distractorSuggestions: string[]
  difficultyLevelByLLM?: string | null
  topicConsistencyNotes: string[]
}

export async function listVotingQuestions(params: ListVotingQuestionsParams): Promise<QuestionSummaryResponse[]> {
  const response = await http.get<QuestionSummaryResponse[]>('/v1/questions/voting', { params })
  return response.data
}

export async function getQuestionVotingDetails(questionId: string): Promise<QuestionDetailResponse> {
  const response = await http.get<QuestionDetailResponse>(`/v1/questions/${questionId}/voting-details`)
  return response.data
}

export async function castQuestionVote(questionId: string, voteType: VoteType): Promise<void> {
  await http.post(`/v1/questions/${questionId}/vote`, { voteType })
}

export async function submitQuestionFeedback(
  questionId: string,
  request: SubmitFeedbackQuestionRequest,
): Promise<void> {
  await http.post(`/v1/questions/${questionId}/feedback`, request)
}

export async function reactToFeedback(feedbackId: string, reactionType: ReactionType): Promise<void> {
  await http.post(`/v1/feedbacks/${feedbackId}/react`, { reactionType })
}

export async function preAnalyzeQuestion(
  request: SubmitMultipleChoiceQuestionRequest,
): Promise<PreAnalyzeQuestionResponse> {
  const response = await http.post<PreAnalyzeQuestionResponse>('/v1/questions/pre-analysis', request)
  return response.data
}

export async function submitQuestion(request: SubmitMultipleChoiceQuestionRequest): Promise<unknown> {
  const response = await http.post('/v1/questions', request)
  return response.data
}
