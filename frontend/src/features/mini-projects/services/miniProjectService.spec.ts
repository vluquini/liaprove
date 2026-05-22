import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import {
  castMiniProjectAttemptVote,
  listPublicMiniProjectAttempts,
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
})
