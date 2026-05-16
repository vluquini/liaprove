import { afterEach, describe, expect, it } from 'vitest'
import type { AssessmentAttemptResponse, AssessmentResultResponse } from '../services/assessmentService'
import {
  clearCurrentAssessmentAttempt,
  readAssessmentResult,
  readCurrentAssessmentAttempt,
  saveAssessmentResult,
  saveCurrentAssessmentAttempt,
} from './assessmentSession'

describe('assessmentSession', () => {
  afterEach(() => sessionStorage.clear())

  it('saves, reads and clears the current attempt by id', () => {
    saveCurrentAssessmentAttempt(makeAttempt())

    expect(readCurrentAssessmentAttempt('attempt-1')?.assessmentTitle).toBe('Avaliacao Java')
    expect(readCurrentAssessmentAttempt('other-attempt')).toBeNull()

    clearCurrentAssessmentAttempt()
    expect(readCurrentAssessmentAttempt('attempt-1')).toBeNull()
  })

  it('saves and reads the last result by attempt id', () => {
    saveAssessmentResult('attempt-1', makeResult())
    expect(readAssessmentResult('attempt-1')?.status).toBe('APPROVED')
    expect(readAssessmentResult('attempt-2')).toBeNull()
  })
})

function makeAttempt(): AssessmentAttemptResponse {
  return {
    attemptId: 'attempt-1',
    assessmentTitle: 'Avaliacao Java',
    startedAt: '2026-05-15T10:00:00',
    evaluationTimerMinutes: 30,
    questions: [],
  }
}

function makeResult(): AssessmentResultResponse {
  return {
    status: 'APPROVED',
    accuracyRate: 80,
    certificateUrl: '/certificates/CERT-123',
    message: 'Aprovado',
  }
}
