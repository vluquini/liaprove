import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import {
  castQuestionVote,
  getQuestionVotingDetails,
  listVotingQuestions,
  preAnalyzeQuestion,
  reactToFeedback,
  submitQuestion,
  submitQuestionFeedback,
  type SubmitMultipleChoiceQuestionRequest,
} from './questionService'

describe('questionService', () => {
  it('lists voting questions and loads voting details', async () => {
    server.use(
      http.get('*/api/v1/questions/voting', ({ request }) => {
        const url = new URL(request.url)
        expect(url.searchParams.get('page')).toBe('1')
        expect(url.searchParams.get('size')).toBe('5')

        return HttpResponse.json([
          {
            id: 'question-1',
            authorId: 'author-1',
            title: 'Como validar transacoes em APIs REST?',
            knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
            submissionDate: '2026-05-14T10:00:00',
          },
        ])
      }),
      http.get('*/api/v1/questions/question-1/voting-details', () =>
        HttpResponse.json({
          id: 'question-1',
          authorId: 'author-1',
          author: { id: 'author-1', name: 'Ana Silva' },
          title: 'Como validar transacoes em APIs REST?',
          knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
          submissionDate: '2026-05-14T10:00:00',
          description: 'Descricao detalhada da questao para curadoria.',
          alternatives: [{ id: 'alt-1', text: 'Usar validacao transacional.' }],
          voteSummary: { approves: 2, rejects: 1 },
          feedbacks: [],
          relevanceByLLM: 'FOUR',
        }),
      ),
    )

    const list = await listVotingQuestions({ page: 1, size: 5 })
    const detail = await getQuestionVotingDetails('question-1')

    expect(list).toHaveLength(1)
    expect(list[0].title).toBe('Como validar transacoes em APIs REST?')
    expect(detail.voteSummary.approves).toBe(2)
  })

  it('sends vote, feedback, reaction, pre-analysis and submission payloads', async () => {
    const calls: Record<string, unknown> = {}

    server.use(
      http.post('*/api/v1/questions/question-1/vote', async ({ request }) => {
        calls.vote = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
      http.post('*/api/v1/questions/question-1/feedback', async ({ request }) => {
        calls.feedback = await request.json()
        return new HttpResponse(null, { status: 201 })
      }),
      http.post('*/api/v1/feedbacks/feedback-1/react', async ({ request }) => {
        calls.reaction = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
      http.post('*/api/v1/questions/pre-analysis', async ({ request }) => {
        calls.preAnalysis = await request.json()
        return HttpResponse.json({
          languageSuggestions: ['Clarear o enunciado.'],
          biasOrAmbiguityWarnings: [],
          distractorSuggestions: ['Melhorar alternativa B.'],
          difficultyLevelByLLM: 'MEDIUM',
          topicConsistencyNotes: ['Tema coerente.'],
        })
      }),
      http.post('*/api/v1/questions', async ({ request }) => {
        calls.submit = await request.json()
        return HttpResponse.json({ id: 'question-2', title: 'Nova questao', status: 'VOTING' }, { status: 201 })
      }),
    )

    await castQuestionVote('question-1', 'APPROVE')
    await submitQuestionFeedback('question-1', {
      comment: 'Boa questao, mas ajustaria o texto.',
      difficultyLevel: 'MEDIUM',
      knowledgeArea: 'SOFTWARE_DEVELOPMENT',
      relevanceLevel: 'FOUR',
    })
    await reactToFeedback('feedback-1', 'LIKE')
    await preAnalyzeQuestion(validMultipleChoiceRequest())
    await submitQuestion(validMultipleChoiceRequest())

    expect(calls.vote).toEqual({ voteType: 'APPROVE' })
    expect(calls.feedback).toMatchObject({ comment: 'Boa questao, mas ajustaria o texto.' })
    expect(calls.reaction).toEqual({ reactionType: 'LIKE' })
    expect(calls.preAnalysis).toMatchObject({ type: 'MULTIPLE_CHOICE' })
    expect(calls.submit).toMatchObject({ type: 'MULTIPLE_CHOICE' })
  })
})

function validMultipleChoiceRequest(): SubmitMultipleChoiceQuestionRequest {
  return {
    type: 'MULTIPLE_CHOICE' as const,
    title: 'Como validar transacoes em APIs REST?',
    description: 'Qual abordagem melhora a consistencia de dados em uma API REST com banco relacional?',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    difficultyByCommunity: 'MEDIUM' as const,
    relevanceByCommunity: 'FOUR' as const,
    alternatives: [
      { text: 'Usar transacoes no caso de uso.', correct: true },
      { text: 'Ignorar rollback em erros.', correct: false },
      { text: 'Persistir parcialmente os dados.', correct: false },
    ],
    acceptedLanguageSuggestions: [],
    acceptedBiasOrAmbiguityWarnings: [],
    acceptedDistractorSuggestions: [],
    acceptedDifficultyLevelByLLM: undefined,
    acceptedTopicConsistencyNotes: [],
  }
}
