import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import {
  castMiniProjectAttemptVote,
  getPublicMiniProjectAttemptDetails,
  listPublicMiniProjectAttempts,
  reactToMiniProjectAttemptFeedback,
  submitMiniProjectAttemptFeedback,
} from './miniProjectService'

describe('miniProjectService', () => {
  it('lists public mini-project attempts', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public', () =>
        HttpResponse.json([
          {
            attemptId: 'attempt-1',
            assessmentTitle: 'API de pedidos',
            authorName: 'Ana Silva',
            repositoryLink: 'https://github.com/ana/orders-api',
            finishedAt: '2026-05-18T14:30:00',
          },
        ]),
      ),
    )

    const attempts = await listPublicMiniProjectAttempts()

    expect(attempts).toHaveLength(1)
    expect(attempts[0]).toMatchObject({
      attemptId: 'attempt-1',
      assessmentTitle: 'API de pedidos',
      authorName: 'Ana Silva',
      repositoryLink: 'https://github.com/ana/orders-api',
    })
  })

  it('sends vote and feedback payloads for a mini-project attempt', async () => {
    const calls: Record<string, unknown> = {}

    server.use(
      http.post('*/api/v1/assessment-attempts/attempt-1/vote', async ({ request }) => {
        calls.vote = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
      http.post('*/api/v1/assessment-attempts/attempt-1/feedback', async ({ request }) => {
        calls.feedback = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    await castMiniProjectAttemptVote('attempt-1', 'APPROVE')
    await submitMiniProjectAttemptFeedback('attempt-1', { comment: 'Entrega bem documentada.' })

    expect(calls.vote).toEqual({ voteType: 'APPROVE' })
    expect(calls.feedback).toEqual({ comment: 'Entrega bem documentada.' })
  })

  it('loads public mini-project attempt details', async () => {
    server.use(
      http.get('*/api/v1/assessment-attempts/mini-project/public/attempt-1', () =>
        HttpResponse.json({
          attemptId: 'attempt-1',
          assessmentTitle: 'Avaliação de Desenvolvimento de Software',
          authorName: 'Ana Silva',
          finishedAt: '2026-05-18T14:30:00',
          repositoryLink: 'https://github.com/ana/orders-api',
          textResponse: null,
          question: {
            id: 'question-1',
            title: 'API de pedidos',
            description: 'Implemente uma API REST para pedidos.',
            knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
            difficulty: 'MEDIUM',
            relevance: 'FOUR',
          },
          voteSummary: {
            approves: 2,
            rejects: 1,
          },
          feedbacks: [
            {
              id: 'feedback-1',
              comment: 'Entrega bem documentada.',
              author: {
                id: 'user-1',
                name: 'Roberto Lima',
              },
              submissionDate: '2026-05-19T09:00:00',
              reactions: [
                {
                  id: 'reaction-1',
                  userId: 'user-2',
                  userName: 'Maria Souza',
                  type: 'LIKE',
                  createdAt: '2026-05-19T10:00:00',
                },
              ],
            },
          ],
        }),
      ),
    )

    const detail = await getPublicMiniProjectAttemptDetails('attempt-1')

    expect(detail).toMatchObject({
      attemptId: 'attempt-1',
      question: {
        title: 'API de pedidos',
      },
      voteSummary: {
        approves: 2,
        rejects: 1,
      },
      feedbacks: [
        {
          id: 'feedback-1',
          reactions: [
            {
              type: 'LIKE',
            },
          ],
        },
      ],
    })
  })

  it('sends reaction payloads for mini-project feedbacks', async () => {
    let payload: unknown

    server.use(
      http.post('*/api/v1/assessment-feedbacks/feedback-1/react', async ({ request }) => {
        payload = await request.json()
        return new HttpResponse(null, { status: 200 })
      }),
    )

    await reactToMiniProjectAttemptFeedback('feedback-1', 'LIKE')

    expect(payload).toEqual({ reactionType: 'LIKE' })
  })
})
