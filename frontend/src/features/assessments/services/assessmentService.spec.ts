import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { server } from '@/test/msw'
import { startSystemAssessment, submitAssessment } from './assessmentService'

describe('assessmentService', () => {
  it('starts a system assessment with selected criteria', async () => {
    let body: unknown
    server.use(
      http.post('*/api/v1/assessments/start-system', async ({ request }) => {
        body = await request.json()
        return HttpResponse.json(makeAttempt(), { status: 201 })
      }),
    )

    const attempt = await startSystemAssessment({
      knowledgeArea: 'SOFTWARE_DEVELOPMENT',
      difficultyLevel: 'MEDIUM',
      type: 'MULTIPLE_CHOICE',
    })

    expect(body).toEqual({
      knowledgeArea: 'SOFTWARE_DEVELOPMENT',
      difficultyLevel: 'MEDIUM',
      type: 'MULTIPLE_CHOICE',
    })
    expect(attempt.attemptId).toBe('attempt-1')
    expect(attempt.questions[0].alternatives?.[0].id).toBe('alt-1')
  })

  it('submits answers for an assessment attempt', async () => {
    let body: unknown
    server.use(
      http.post('*/api/v1/assessments/attempt-1/submit', async ({ request }) => {
        body = await request.json()
        return HttpResponse.json({
          status: 'APPROVED',
          accuracyRate: 80,
          certificateUrl: '/certificates/CERT-123',
          message: 'Aprovado',
        })
      }),
    )

    const result = await submitAssessment('attempt-1', {
      answers: [{ questionId: 'question-1', selectedAlternativeId: 'alt-1' }],
    })

    expect(body).toEqual({
      answers: [{ questionId: 'question-1', selectedAlternativeId: 'alt-1' }],
    })
    expect(result.status).toBe('APPROVED')
    expect(result.certificateUrl).toBe('/certificates/CERT-123')
  })
})

function makeAttempt() {
  return {
    attemptId: 'attempt-1',
    assessmentTitle: 'Avaliacao Java',
    startedAt: '2026-05-15T10:00:00',
    evaluationTimerMinutes: 30,
    questions: [
      {
        id: 'question-1',
        title: 'Como validar transacoes?',
        description: 'Escolha a alternativa correta.',
        alternatives: [{ id: 'alt-1', text: 'Usar transacoes no caso de uso.' }],
      },
    ],
  }
}
