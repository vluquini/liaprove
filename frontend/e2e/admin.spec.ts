import { expect, test, type Page, type Route } from '@playwright/test'

type AdminApiState = {
  userSearches: URLSearchParams[]
  questionSearches: URLSearchParams[]
  attemptSearches: URLSearchParams[]
  activatedUsers: string[]
  deactivatedUsers: string[]
  deletedUsers: string[]
  updatedQuestionTitle: string | null
  moderatedQuestionStatus: string | null
  roleMultipliers: Record<string, number>
  recruiterMultipliers: Record<string, number>
  recruiterWeights: Record<string, number>
  dryRunRequested: boolean | null
}

const adminUser = {
  id: 'admin-e2e-1',
  name: 'Admin E2E',
  email: 'admin-e2e@example.com',
  role: 'ADMIN',
}

const recruiterId = 'recruiter-e2e-1'
const userId = 'user-admin-1'
const questionId = 'question-e2e-1'

test('admin navigates from dashboard to admin home and user management', async ({ page }) => {
  const state = createAdminApiState()
  await mockAdminApi(page, state)
  await authenticateAsAdmin(page)

  await expect(page.getByRole('heading', { name: /Olá, Admin/ })).toBeVisible()
  await expect(page.getByText('Gerenciar usuários')).toBeVisible()
  await expect(page.getByText('Moderar questões')).toBeVisible()
  await expect(page.getByText('Ver tentativas')).toBeVisible()
  await expect(page.getByText('Algoritmo genético')).toBeVisible()

  await page.getByRole('link', { name: 'Admin' }).click()
  await expect(page).toHaveURL(/\/admin$/)
  await expect(page.getByRole('heading', { name: 'Painel administrativo' })).toBeVisible()

  await page.locator('.action-card').filter({ hasText: 'Usuários' }).getByRole('button', { name: 'Abrir' }).click()
  await expect(page).toHaveURL(/\/admin\/users$/)
  await expect(page.getByRole('heading', { name: 'Usuários' })).toBeVisible()
  await expect(page.getByText('Ana Administrada')).toBeVisible()
})

test('admin filters users and performs account actions', async ({ page }) => {
  const state = createAdminApiState()
  await mockAdminApi(page, state)
  await authenticateAsAdmin(page)

  page.on('dialog', async (dialog) => dialog.accept())

  await page.goto('/admin/users')
  await page.locator('[data-test="admin-user-filter-name"]').fill('Ana')
  await page.locator('[data-test="admin-user-filter-role"]').selectOption('RECRUITER')
  await page.locator('[data-test="admin-user-apply-filters"]').click()

  await expect.poll(() => state.userSearches.at(-1)?.get('name')).toBe('Ana')
  expect(state.userSearches.at(-1)?.get('role')).toBe('RECRUITER')

  await page.locator(`[data-test="activate-user-${userId}"]`).click()
  await expect(page.getByText('Usuário ativado com sucesso.')).toBeVisible()
  expect(state.activatedUsers).toContain(userId)

  await page.locator(`[data-test="deactivate-user-${userId}"]`).click()
  await expect(page.getByText('Usuário desativado com sucesso.')).toBeVisible()
  expect(state.deactivatedUsers).toContain(userId)

  await page.locator(`[data-test="delete-user-${userId}"]`).click()
  await expect(page.getByText('Usuário removido com sucesso.')).toBeVisible()
  expect(state.deletedUsers).toContain(userId)
})

test('admin moderates a question and audits votes and feedbacks', async ({ page }) => {
  const state = createAdminApiState()
  await mockAdminApi(page, state)
  await authenticateAsAdmin(page)

  await page.goto('/admin/questions')
  await page.locator('[data-test="admin-question-filter-area"]').selectOption('SOFTWARE_DEVELOPMENT')
  await page.locator('[data-test="admin-question-filter-difficulty"]').selectOption('MEDIUM')
  await page.locator('[data-test="admin-question-filter-status"]').selectOption('VOTING')
  await page.locator('[data-test="admin-question-filter-author-name"]').fill('Carlos')
  await page.locator('[data-test="admin-question-filter-author-id"]').fill('author-e2e-1')
  await page.locator('[data-test="admin-question-apply-filters"]').click()

  await expect.poll(() => state.questionSearches.at(-1)?.get('authorName')).toBe('Carlos')
  expect(state.questionSearches.at(-1)?.get('authorId')).toBe('author-e2e-1')
  expect(state.questionSearches.at(-1)?.get('status')).toBe('VOTING')

  await page.locator(`[data-test="open-admin-question-${questionId}"]`).click()
  await expect(page).toHaveURL(new RegExp(`/admin/questions/${questionId}$`))
  await expect(page.getByRole('heading', { name: 'Transações em APIs Java' })).toBeVisible()
  await expect(page.getByText('Recrutador Votante', { exact: true })).toBeVisible()
  await expect(page.getByText('Questão clara para avaliação técnica.')).toBeVisible()

  await page.locator('[data-test="admin-question-title"]').fill('Transações REST revisadas')
  await page.locator('[data-test="save-admin-question"]').click()
  await expect(page.getByText('Questão atualizada com sucesso.')).toBeVisible()
  expect(state.updatedQuestionTitle).toBe('Transações REST revisadas')

  await page.locator('[data-test="admin-question-status"]').selectOption('APPROVED')
  await page.locator('[data-test="moderate-admin-question"]').click()
  await expect(page.getByText('Questão moderada com sucesso.')).toBeVisible()
  expect(state.moderatedQuestionStatus).toBe('APPROVED')

  await page.goto(`/admin/metrics/questions/${questionId}`)
  await expect(page.getByRole('heading', { name: 'Transações em APIs Java' })).toBeVisible()
  await expect(page.getByText('Recrutador Votante', { exact: true })).toBeVisible()
  await expect(page.getByText('Questão clara para avaliação técnica.')).toBeVisible()
})

test('admin filters attempts and runs a genetic algorithm dry run', async ({ page }) => {
  const state = createAdminApiState()
  await mockAdminApi(page, state)
  await authenticateAsAdmin(page)

  await page.goto('/admin/assessments/attempts')
  await expect(page.getByText('Java Backend Hiring Challenge')).toBeVisible()
  await expect(page.getByText('Candidato E2E')).toBeVisible()

  await page.locator('[data-test="admin-attempt-filter-type"]').selectOption('true')
  await page.locator('[data-test="admin-attempt-filter-status"]').selectOption('APPROVED')
  await page.locator('[data-test="admin-attempt-filter-start"]').fill('2026-05-01T00:00:00')
  await page.locator('[data-test="admin-attempt-filter-end"]').fill('2026-05-31T23:59:59')
  await page.locator('[data-test="admin-attempt-apply-filters"]').click()

  await expect.poll(() => state.attemptSearches.at(-1)?.get('isPersonalized')).toBe('true')
  expect(state.attemptSearches.at(-1)?.get('statuses')).toBe('APPROVED')
  expect(state.attemptSearches.at(-1)?.get('startDate')).toBe('2026-05-01T00:00:00')

  await page.goto('/admin/algorithms/genetic')
  await expect(page.getByRole('heading', { name: 'Algoritmo Genético' })).toBeVisible()
  await expect(page.getByText('Recrutadora Algoritmo')).toBeVisible()

  await page.locator('[data-test="admin-ga-role-RECRUITER"]').fill('2.4')
  await page.locator('[data-test="admin-ga-save-role-RECRUITER"]').click()
  await expect(page.getByText('Multiplicador de RECRUITER atualizado.')).toBeVisible()
  expect(state.roleMultipliers.RECRUITER).toBe(2.4)

  await page.locator(`[data-test="admin-ga-recruiter-multiplier-${recruiterId}"]`).fill('1.6')
  await page.locator(`[data-test="admin-ga-save-recruiter-multiplier-${recruiterId}"]`).click()
  await expect(page.getByText('Multiplicador de Recrutadora Algoritmo atualizado.')).toBeVisible()
  expect(state.recruiterMultipliers[recruiterId]).toBe(1.6)

  await page.locator(`[data-test="admin-ga-recruiter-weight-${recruiterId}"]`).fill('8')
  await page.locator(`[data-test="admin-ga-save-recruiter-weight-${recruiterId}"]`).click()
  await expect(page.getByText('Peso de Recrutadora Algoritmo atualizado.')).toBeVisible()
  expect(state.recruiterWeights[recruiterId]).toBe(8)

  await page.locator('[data-test="admin-ga-run-dry"]').click()
  await expect(page.getByText('Resultado simulado calculado.')).toBeVisible()
  await expect(page.getByRole('heading', { name: 'Resultado simulado' })).toBeVisible()
  await expect(page.getByText(recruiterId)).toBeVisible()
  await expect(page.getByText('9')).toBeVisible()
  expect(state.dryRunRequested).toBe(true)
})

async function mockAdminApi(page: Page, state: AdminApiState) {
  await page.route('**/api/v1/admin/**', async (route) => {
    const request = route.request()
    const url = new URL(request.url())
    const path = url.pathname
    const method = request.method()

    if (method === 'GET' && path === '/api/v1/admin/users') {
      state.userSearches.push(url.searchParams)
      return fulfillJson(route, adminUsers())
    }

    if (method === 'PATCH' && path === `/api/v1/admin/users/${userId}/activate`) {
      state.activatedUsers.push(userId)
      return fulfillJson(route, null)
    }

    if (method === 'PATCH' && path === `/api/v1/admin/users/${userId}/deactivate`) {
      state.deactivatedUsers.push(userId)
      return fulfillJson(route, null)
    }

    if (method === 'DELETE' && path === `/api/v1/admin/users/${userId}`) {
      state.deletedUsers.push(userId)
      return fulfillJson(route, null)
    }

    if (method === 'GET' && path === '/api/v1/admin/questions') {
      state.questionSearches.push(url.searchParams)
      return fulfillJson(route, [adminQuestion()])
    }

    if (method === 'GET' && path === `/api/v1/admin/questions/${questionId}`) {
      return fulfillJson(route, adminQuestion())
    }

    if (method === 'GET' && path === `/api/v1/admin/questions/${questionId}/votes`) {
      return fulfillJson(route, questionVotes())
    }

    if (method === 'GET' && path === `/api/v1/admin/questions/${questionId}/feedbacks`) {
      return fulfillJson(route, questionFeedbacks())
    }

    if (method === 'PUT' && path === `/api/v1/admin/questions/${questionId}`) {
      const body = request.postDataJSON() as { title?: string }
      state.updatedQuestionTitle = body.title ?? null
      return fulfillJson(route, { ...adminQuestion(), title: body.title })
    }

    if (method === 'PATCH' && path === `/api/v1/admin/questions/${questionId}/moderate`) {
      const body = request.postDataJSON() as { newStatus?: string }
      state.moderatedQuestionStatus = body.newStatus ?? null
      return fulfillJson(route, { ...adminQuestion(), status: body.newStatus })
    }

    if (method === 'GET' && path === '/api/v1/admin/assessments/attempts') {
      state.attemptSearches.push(url.searchParams)
      return fulfillJson(route, [assessmentAttempt()])
    }

    if (method === 'GET' && path === '/api/v1/admin/algorithms/genetic/roles/PROFESSIONAL/multiplier') {
      return fulfillJson(route, state.roleMultipliers.PROFESSIONAL)
    }

    if (method === 'GET' && path === '/api/v1/admin/algorithms/genetic/roles/RECRUITER/multiplier') {
      return fulfillJson(route, state.roleMultipliers.RECRUITER)
    }

    if (method === 'PATCH' && path.startsWith('/api/v1/admin/algorithms/genetic/roles/')) {
      const role = path.split('/').at(-2)
      const body = request.postDataJSON() as { multiplier: number }
      if (role) {
        state.roleMultipliers[role] = body.multiplier
      }
      return fulfillJson(route, null)
    }

    if (method === 'GET' && path === '/api/v1/admin/algorithms/genetic/recruiters/weights') {
      return fulfillJson(route, [recruiterWeight(state)])
    }

    if (method === 'PATCH' && path === `/api/v1/admin/algorithms/genetic/recruiters/${recruiterId}/multiplier`) {
      const body = request.postDataJSON() as { multiplier: number }
      state.recruiterMultipliers[recruiterId] = body.multiplier
      return fulfillJson(route, null)
    }

    if (method === 'PATCH' && path === `/api/v1/admin/algorithms/genetic/recruiters/${recruiterId}/vote-weight`) {
      const body = request.postDataJSON() as { weight: number }
      state.recruiterWeights[recruiterId] = body.weight
      return fulfillJson(route, null)
    }

    if (method === 'POST' && path === '/api/v1/admin/algorithms/genetic/adjust') {
      const body = request.postDataJSON() as { dryRun: boolean }
      state.dryRunRequested = body.dryRun
      return fulfillJson(route, { [recruiterId]: 9 })
    }

    return route.continue()
  })
}

async function authenticateAsAdmin(page: Page) {
  await page.addInitScript((user) => {
    const expiresAt = new Date()
    expiresAt.setHours(expiresAt.getHours() + 2)
    localStorage.setItem(
      'liaprove.auth.session',
      JSON.stringify({
        token: 'admin-e2e-token',
        tokenType: 'Bearer',
        expiresAt: expiresAt.toISOString(),
        user: {
          ...user,
          status: 'ACTIVE',
          occupation: 'Administrador',
          experienceLevel: 'SENIOR',
          hardSkills: ['Governança'],
          softSkills: ['Comunicação'],
        },
      }),
    )
  }, adminUser)

  await page.goto('/dashboard')
  await expect(page).toHaveURL(/\/dashboard$/)
}

async function fulfillJson(route: Route, body: unknown, status = 200) {
  await route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body),
  })
}

function createAdminApiState(): AdminApiState {
  return {
    userSearches: [],
    questionSearches: [],
    attemptSearches: [],
    activatedUsers: [],
    deactivatedUsers: [],
    deletedUsers: [],
    updatedQuestionTitle: null,
    moderatedQuestionStatus: null,
    roleMultipliers: {
      PROFESSIONAL: 1,
      RECRUITER: 2,
    },
    recruiterMultipliers: {
      [recruiterId]: 1.2,
    },
    recruiterWeights: {
      [recruiterId]: 7,
    },
    dryRunRequested: null,
  }
}

function adminUsers() {
  return [
    {
      id: userId,
      name: 'Ana Administrada',
      email: 'ana.admin@example.com',
      occupation: 'Recrutadora técnica',
      experienceLevel: 'SENIOR',
      hardSkills: ['Java', 'Spring Boot'],
      softSkills: ['Comunicação'],
      role: 'RECRUITER',
      companyName: 'LIA Recruiting',
      companyEmail: 'recruiting@example.com',
    },
  ]
}

function adminQuestion() {
  return {
    type: 'MULTIPLE_CHOICE',
    id: questionId,
    authorId: 'author-e2e-1',
    title: 'Transações em APIs Java',
    description: 'Qual prática mantém consistência de dados em operações críticas?',
    knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
    difficultyByCommunity: 'MEDIUM',
    relevanceByCommunity: 'FOUR',
    relevanceByLLM: 'FIVE',
    submissionDate: '2026-05-15T10:00:00',
    status: 'VOTING',
    alternatives: [
      { id: 'alternative-e2e-1', text: 'Usar transações e rollback.' },
      { id: 'alternative-e2e-2', text: 'Persistir parcialmente cada operação.' },
    ],
  }
}

function questionVotes() {
  return [
    {
      id: 'vote-e2e-1',
      user: {
        id: recruiterId,
        name: 'Recrutador Votante',
        email: 'votante@example.com',
        role: 'RECRUITER',
      },
      voteType: 'APPROVE',
      createdAt: '2026-05-16T10:00:00',
    },
  ]
}

function questionFeedbacks() {
  return [
    {
      id: 'feedback-e2e-1',
      comment: 'Questão clara para avaliação técnica.',
      author: {
        id: recruiterId,
        name: 'Recrutador Votante',
      },
      submissionDate: '2026-05-16T11:00:00',
      reactions: [
        {
          id: 'reaction-e2e-1',
          userId: 'user-e2e-2',
          userName: 'Avaliadora',
          type: 'LIKE',
          createdAt: '2026-05-16T12:00:00',
        },
      ],
    },
  ]
}

function assessmentAttempt() {
  return {
    attemptId: 'attempt-e2e-1',
    status: 'APPROVED',
    accuracyRate: 88,
    startedAt: '2026-05-15T10:00:00',
    finishedAt: '2026-05-15T10:30:00',
    assessment: {
      id: 'assessment-e2e-1',
      title: 'Java Backend Hiring Challenge',
      personalized: true,
      criteriaWeights: {
        hardSkillsWeight: 60,
        softSkillsWeight: 20,
        experienceWeight: 20,
      },
      jobDescriptionAnalysis: null,
    },
    candidate: {
      id: 'candidate-e2e-1',
      name: 'Candidato E2E',
      email: 'candidate@example.com',
      occupation: 'Desenvolvedor Java',
      experienceLevel: 'SENIOR',
      hardSkills: ['Java', 'Spring Boot'],
      softSkills: ['Comunicação'],
      role: 'PROFESSIONAL',
    },
  }
}

function recruiterWeight(state: AdminApiState) {
  return {
    id: recruiterId,
    name: 'Recrutadora Algoritmo',
    email: 'algoritmo@example.com',
    companyName: 'LIA Recruiting',
    companyEmail: 'algoritmo-company@example.com',
    voteWeight: state.recruiterWeights[recruiterId],
    multiplier: state.recruiterMultipliers[recruiterId],
  }
}
