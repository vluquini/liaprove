import type { AssessmentAttemptResponse, AssessmentResultResponse } from '../services/assessmentService'

const currentAttemptKey = 'liaprove.currentAssessmentAttempt'
const resultPrefix = 'liaprove.assessmentResult.'

export function saveCurrentAssessmentAttempt(attempt: AssessmentAttemptResponse): void {
  sessionStorage.setItem(currentAttemptKey, JSON.stringify(attempt))
}

export function readCurrentAssessmentAttempt(attemptId: string): AssessmentAttemptResponse | null {
  const raw = sessionStorage.getItem(currentAttemptKey)

  if (!raw) {
    return null
  }

  try {
    const attempt = JSON.parse(raw) as AssessmentAttemptResponse
    return attempt.attemptId === attemptId ? attempt : null
  } catch {
    return null
  }
}

export function clearCurrentAssessmentAttempt(): void {
  sessionStorage.removeItem(currentAttemptKey)
}

export function saveAssessmentResult(attemptId: string, result: AssessmentResultResponse): void {
  sessionStorage.setItem(`${resultPrefix}${attemptId}`, JSON.stringify(result))
}

export function readAssessmentResult(attemptId: string): AssessmentResultResponse | null {
  const raw = sessionStorage.getItem(`${resultPrefix}${attemptId}`)

  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw) as AssessmentResultResponse
  } catch {
    return null
  }
}
