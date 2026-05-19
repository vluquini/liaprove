import { expect, test, type Page, type Route } from '@playwright/test'

type E2EUser = {
  id: string
  name: string
  email: string
  role: 'PROFESSIONAL' | 'RECRUITER'
  occupation: string
  experienceLevel: 'JUNIOR' | 'PLENO' | 'SENIOR'
  hardSkills?: string[]
  softSkills?: string[]
  companyName?: string
  companyEmail?: string
}

const assessmentId = 'assessment-e2e-1'
const attemptId = 'attempt-e2e-1'
const token = 'token-e2e-1'
const questionId = 'question-e2e-1'
const alternativeId = 'alternative-e2e-1'

test('recruiter creates a personalized assessment and evaluates a candidate attempt', async ({ page }) => {
  const id = uniqueId()
  const recruiter = {
    id: 'recruiter-e2e-1',
    name: `Recrutador ${id}`,
    email: `recruiter-assessment-${id}@example.com`,
    role: 'RECRUITER',
    occupation: 'Recrutador Técnico',
    experienceLevel: 'SENIOR',
    hardSkills: ['Java', 'Spring Boot', 'SQL'],
    softSkills: ['Comunicação'],
    companyName: 'LIA Recruiting',
    companyEmail: `company-${id}@example.com`,
  } satisfies E2EUser
  const candidate = {
    id: 'candidate-e2e-1',
    name: `Candidato ${id}`,
    email: `candidate-assessment-${id}@example.com`,
    role: 'PROFESSIONAL',
    occupation: 'Desenvolvedor Java',
    experienceLevel: 'SENIOR',
    hardSkills: ['Java', 'Spring Boot'],
    softSkills: ['Comunicação'],
  } satisfies E2EUser
  const state = {
    assessmentCreated: false,
    attemptSubmitted: false,
    candidate,
  }

  await mockRecruiterAssessmentFlow(page, state)
  await authenticateAs(page, recruiter)

  await page.goto('/recruiter')
  await expect(page.getByRole('heading', { name: 'Área do recrutador' })).toBeVisible()

  await page.getByRole('link', { name: 'Analisar vaga' }).click()
  await page.locator('[data-test="job-description"]').fill(
    'Senior Java backend engineer with Spring Boot, APIs, database design and strong communication.',
  )
  await page.locator('[data-test="analyze-job-description"]').click()
  await expect(page.getByText('Spring Boot')).toBeVisible()
  await page.locator('[data-test="use-analysis"]').click()

  await expect(page).toHaveURL(/\/recruiter\/assessments\/new$/)
  await page.locator('[data-test="assessment-title"]').fill('Java Backend Hiring Challenge')
  await page.locator('[data-test="assessment-description"]').fill('Avaliação técnica para vaga backend Java.')
  await page.locator('[data-test="assessment-expiration"]').fill(futureDateTimeLocal())
  await page.locator('[data-test="assessment-max-attempts"]').fill('2')
  await page.locator('[data-test="assessment-timer"]').fill('30')
  await page.locator('[data-test="load-suggestions"]').click()
  await expect(page.getByText('Transações em APIs Java')).toBeVisible()
  await page.locator(`[data-test="select-question-${questionId}"]`).check()
  await page.locator('[data-test="create-assessment"]').click()
  await expect(page.getByText(`/assessments/personalized/${token}/start`)).toBeVisible()

  await page.locator('[data-test="logout-button"]').click()
  await authenticateAs(page, candidate)

  await page.goto(`/assessments/personalized/${token}/start`)
  await expect(page.getByRole('heading', { name: 'Iniciar avaliação personalizada' })).toBeVisible()
  await page.locator('[data-test="start-personalized-assessment"]').click()
  await expect(page).toHaveURL(new RegExp(`/assessments/attempts/${attemptId}$`))
  await page.locator(`[data-test="answer-${questionId}-${alternativeId}"]`).check()
  await page.locator('[data-test="submit-assessment"]').click()
  await expect(page).toHaveURL(new RegExp(`/assessments/attempts/${attemptId}/result$`))
  await expect(page.getByRole('heading', { name: 'Resultado da avaliação' })).toBeVisible()
  await expect(page.getByText('Tentativa registrada para revisão do recrutador.')).toBeVisible()

  await page.locator('[data-test="logout-button"]').click()
  await authenticateAs(page, recruiter)

  await page.goto('/recruiter')
  await expect(page.getByText('Java Backend Hiring Challenge')).toBeVisible()
  await page.getByRole('button', { name: 'Detalhes' }).click()
  await expect(page).toHaveURL(new RegExp(`/recruiter/assessments/${assessmentId}$`))
  await page.getByRole('button', { name: 'Tentativas' }).click()
  await expect(page).toHaveURL(new RegExp(`/recruiter/assessments/${assessmentId}/attempts$`))
  await expect(page.getByText(candidate.name)).toBeVisible()
  await page.getByRole('button', { name: 'Abrir' }).click()

  await expect(page).toHaveURL(new RegExp(`/recruiter/attempts/${attemptId}$`))
  await expect(page.getByRole('heading', { name: candidate.name })).toBeVisible()
  await page.locator('[data-test="generate-pre-analysis"]').click()
  await expect(page.getByText('Boa aderência técnica ao cenário Java.')).toBeVisible()
  await page.locator('[data-test="approve-candidate"]').click()
  await expect(page.getByText('Candidato aprovado.')).toBeVisible()
})

async function mockRecruiterAssessmentFlow(
  page: Page,
  state: {
    assessmentCreated: boolean
    attemptSubmitted: boolean
    candidate: { name: string; email: string }
  },
) {
  await page.route('**/api/v1/assessments/**', async (route) => {
    const request = route.request()
    const url = new URL(request.url())
    const path = url.pathname
    const method = request.method()

    if (method === 'POST' && path === '/api/v1/assessments/personalized/job-description-analysis') {
      return fulfillJson(route, jobDescriptionAnalysis())
    }

    if (method === 'GET' && path === '/api/v1/assessments/personalized/suggestions') {
      return fulfillJson(route, {
        content: [suggestedQuestion()],
        page: 1,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        last: true,
      })
    }

    if (method === 'POST' && path === '/api/v1/assessments/personalized') {
      state.assessmentCreated = true
      return fulfillJson(route, personalizedAssessmentSummary(), 201)
    }

    if (method === 'GET' && path === '/api/v1/assessments/personalized') {
      return fulfillJson(route, state.assessmentCreated ? [personalizedAssessmentDetails()] : [])
    }

    if (method === 'GET' && path === `/api/v1/assessments/personalized/${assessmentId}`) {
      return fulfillJson(route, personalizedAssessmentDetails())
    }

    if (method === 'GET' && path === `/api/v1/assessments/personalized/${assessmentId}/attempts`) {
      return fulfillJson(route, state.attemptSubmitted ? [attemptSummary(state.candidate)] : [])
    }

    if (method === 'POST' && path === `/api/v1/assessments/start-personalized/${token}`) {
      return fulfillJson(route, assessmentAttempt(), 201)
    }

    if (method === 'POST' && path === `/api/v1/assessments/${attemptId}/submit`) {
      state.attemptSubmitted = true
      return fulfillJson(route, {
        status: 'COMPLETED',
        accuracyRate: null,
        certificateUrl: null,
        message: 'Tentativa registrada para revisão do recrutador.',
      })
    }

    if (method === 'GET' && path === `/api/v1/assessments/attempts/${attemptId}`) {
      return fulfillJson(route, attemptDetails(state.candidate))
    }

    if (method === 'POST' && path === `/api/v1/assessments/attempts/${attemptId}/pre-analysis`) {
      return fulfillJson(route, {
        metadata: {
          attemptId,
          generatedAt: new Date().toISOString(),
          ignoredQuestionTypes: ['PROJECT'],
        },
        analysis: {
          summary: 'Boa aderência técnica ao cenário Java.',
          strengths: ['Reconhece uso de transações em operações críticas.'],
          attentionPoints: ['Aprofundar estratégia de observabilidade.'],
          finalExplanation: 'Candidato recomendado para próxima etapa.',
        },
      })
    }

    if (method === 'POST' && path === `/api/v1/assessments/${attemptId}/evaluate`) {
      return fulfillJson(route, {
        attemptId,
        status: 'APPROVED',
        message: 'Candidato aprovado.',
      })
    }

    return route.continue()
  })
}

async function fulfillJson(route: Route, body: unknown, status = 200) {
  await route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body),
  })
}

function jobDescriptionAnalysis() {
  return {
    originalJobDescription:
      'Senior Java backend engineer with Spring Boot, APIs, database design and strong communication.',
    suggestedKnowledgeAreas: ['SOFTWARE_DEVELOPMENT', 'DATABASE'],
    suggestedHardSkills: ['Java', 'Spring Boot', 'SQL'],
    suggestedSoftSkills: ['Communication'],
    suggestedCriteriaWeights: {
      hardSkillsWeight: 60,
      softSkillsWeight: 20,
      experienceWeight: 20,
    },
  }
}

function suggestedQuestion() {
  return {
    id: questionId,
    title: 'Transações em APIs Java',
    description: 'Qual prática reduz falhas de consistência ao salvar dados relacionados?',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    difficultyLevel: 'MEDIUM',
    submissionDate: '2026-05-15T10:00:00',
    score: 0.92,
  }
}

function personalizedAssessmentSummary() {
  return {
    id: assessmentId,
    title: 'Java Backend Hiring Challenge',
    shareableToken: token,
    status: 'ACTIVE',
    criteriaWeights: jobDescriptionAnalysis().suggestedCriteriaWeights,
    jobDescriptionAnalysis: jobDescriptionAnalysis(),
  }
}

function personalizedAssessmentDetails() {
  return {
    id: assessmentId,
    title: 'Java Backend Hiring Challenge',
    description: 'Avaliação técnica para vaga backend Java.',
    creationDate: '2026-05-15T10:00:00',
    evaluationTimerMinutes: 30,
    expirationDate: '2026-12-31T23:59:00',
    totalAttempts: 1,
    maxAttempts: 2,
    shareableToken: token,
    status: 'ACTIVE',
    createdBy: {
      id: 'recruiter-e2e-1',
      name: 'Recrutador E2E',
      email: 'recruiter-e2e@example.com',
      role: 'RECRUITER',
    },
    criteriaWeights: jobDescriptionAnalysis().suggestedCriteriaWeights,
    jobDescriptionAnalysis: jobDescriptionAnalysis(),
    questions: [suggestedQuestion()],
  }
}

function assessmentAttempt() {
  return {
    attemptId,
    assessmentTitle: 'Java Backend Hiring Challenge',
    startedAt: new Date().toISOString(),
    evaluationTimerMinutes: 30,
    questions: [
      {
        id: questionId,
        title: 'Transações em APIs Java',
        description: 'Qual prática reduz falhas de consistência ao salvar dados relacionados?',
        alternatives: [
          { id: alternativeId, text: 'Usar transações e rollback para operações relacionadas.' },
          { id: 'alternative-e2e-2', text: 'Persistir cada operação sem controle transacional.' },
        ],
      },
    ],
  }
}

function attemptSummary(candidate: { name: string; email: string }) {
  return {
    attemptId,
    candidateId: 'candidate-e2e-1',
    candidateName: candidate.name,
    candidateEmail: candidate.email,
    assessmentId,
    assessmentTitle: 'Java Backend Hiring Challenge',
    status: 'COMPLETED',
    accuracyRate: null,
    startedAt: '2026-05-15T10:00:00',
    submittedAt: '2026-05-15T10:20:00',
  }
}

function attemptDetails(candidate: { name: string; email: string }) {
  return {
    attemptId,
    status: 'COMPLETED',
    accuracyRate: null,
    startedAt: '2026-05-15T10:00:00',
    finishedAt: '2026-05-15T10:20:00',
    assessment: {
      id: assessmentId,
      title: 'Java Backend Hiring Challenge',
      description: 'Avaliação técnica para vaga backend Java.',
      evaluationTimerMinutes: 30,
      criteriaWeights: jobDescriptionAnalysis().suggestedCriteriaWeights,
      jobDescriptionAnalysis: jobDescriptionAnalysis(),
    },
    candidate: {
      id: 'candidate-e2e-1',
      name: candidate.name,
      email: candidate.email,
      role: 'PROFESSIONAL',
      experienceLevel: 'SENIOR',
      hardSkills: ['Java', 'Spring Boot'],
      softSkills: ['Communication'],
    },
    explainability: {
      totalQuestions: 1,
      answeredQuestions: 1,
      multipleChoiceQuestions: 1,
      openQuestions: 0,
      projectQuestions: 0,
      candidateExperienceLevel: 'SENIOR',
      candidateHardSkills: ['Java', 'Spring Boot'],
      candidateSoftSkills: ['Communication'],
      criteriaWeights: jobDescriptionAnalysis().suggestedCriteriaWeights,
    },
    questions: [
      {
        id: questionId,
        title: 'Transações em APIs Java',
        description: 'Qual prática reduz falhas de consistência ao salvar dados relacionados?',
        guideline: null,
        alternatives: [
          { id: alternativeId, text: 'Usar transações e rollback para operações relacionadas.' },
          { id: 'alternative-e2e-2', text: 'Persistir cada operação sem controle transacional.' },
        ],
        answer: {
          questionId,
          selectedAlternativeId: alternativeId,
          projectUrl: null,
          textResponse: null,
        },
      },
    ],
  }
}

async function authenticateAs(page: Page, user: E2EUser) {
  await page.goto('/login')
  await page.evaluate((sessionUser) => {
    const expiresAt = new Date()
    expiresAt.setHours(expiresAt.getHours() + 2)
    localStorage.setItem(
      'liaprove.auth.session',
      JSON.stringify({
        token: `e2e-token-${sessionUser.role.toLowerCase()}`,
        tokenType: 'Bearer',
        expiresAt: expiresAt.toISOString(),
        user: {
          id: sessionUser.id,
          name: sessionUser.name,
          email: sessionUser.email,
          role: sessionUser.role,
          status: 'ACTIVE',
          occupation: sessionUser.occupation,
          experienceLevel: sessionUser.experienceLevel,
          hardSkills: sessionUser.hardSkills ?? [],
          softSkills: sessionUser.softSkills ?? [],
          companyName: sessionUser.companyName ?? null,
          companyEmail: sessionUser.companyEmail ?? null,
        },
      }),
    )
  }, user)
  await page.goto('/dashboard')

  await expect(page).toHaveURL(/\/dashboard$/)
}

function futureDateTimeLocal(): string {
  const value = new Date()
  value.setDate(value.getDate() + 30)
  value.setMinutes(value.getMinutes() - value.getTimezoneOffset())
  return value.toISOString().slice(0, 16)
}

function uniqueId(): string {
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}
