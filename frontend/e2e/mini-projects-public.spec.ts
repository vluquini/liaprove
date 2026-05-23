import { expect, test, type Page } from '@playwright/test'

test('professional reviews a public mini-project attempt', async ({ page }) => {
  const id = uniqueId()
  const name = `Revisor ${id}`
  const feedback = `Feedback E2E ${id}: entrega clara e bem documentada.`

  await authenticateProfessional(page, name)
  await mockMiniProjectReviewFlow(page, feedback)

  await page.goto('/mini-projects/public')
  await expect(page.getByRole('heading', { name: 'Mini-projetos públicos' })).toBeVisible()
  await expect(page.getByText('Avaliação de Desenvolvimento de Software')).toBeVisible()
  await expect(page.getByText('Ana Silva')).toBeVisible()
  await expect(page.getByText('Abrir entrega')).not.toBeVisible()
  await expect(page.getByRole('button', { name: 'Aprovar' })).not.toBeVisible()

  await page.locator('[data-test="view-mini-project-details-attempt-e2e-1"]').click()

  await expect(page).toHaveURL(/\/mini-projects\/public\/attempt-e2e-1$/)
  await expect(page.getByRole('heading', { name: 'API REST para pedidos' })).toBeVisible()
  await expect(page.getByText('Implementar autenticação JWT e cadastro de pedidos.')).toBeVisible()
  await expect(page.getByText('README com instruções de execução e collection HTTP.')).toBeVisible()
  await expect(page.getByRole('link', { name: 'Abrir entrega' })).toHaveAttribute(
    'href',
    'https://github.com/ana/orders-api',
  )

  await page.getByRole('button', { name: 'Aprovar' }).click()
  await expect(page.getByText('Voto registrado com sucesso.')).toBeVisible()

  await page.locator('[data-test="mini-project-feedback-comment"]').fill(feedback)
  await page.locator('[data-test="send-mini-project-feedback"]').click()
  await expect(page.getByText('Feedback enviado com sucesso.')).toBeVisible()
  await expect(page.getByText(feedback)).toBeVisible()

  await page.locator('[data-test="like-mini-project-feedback-feedback-e2e-1"]').click()
  await expect(page.getByText('Reação registrada.')).toBeVisible()
})

async function authenticateProfessional(page: Page, name: string) {
  await page.addInitScript((authenticatedName) => {
    window.localStorage.setItem(
      'liaprove.auth.session',
      JSON.stringify({
        token: 'e2e-token',
        tokenType: 'Bearer',
        expiresAt: '2099-01-01T00:00:00.000Z',
        user: {
          id: 'user-e2e-current',
          name: authenticatedName,
          email: 'mini-project-review@example.com',
          role: 'PROFESSIONAL',
          occupation: 'Desenvolvedor Java',
          bio: null,
          experienceLevel: 'JUNIOR',
          hardSkills: ['Java', 'Spring Boot', 'SQL'],
          softSkills: ['Comunicação'],
        },
      }),
    )
  }, name)
}

async function mockMiniProjectReviewFlow(page: Page, submittedFeedback: string) {
  let voteRegistered = false
  let feedbackRegistered = false
  let reactionRegistered = false

  await page.route('**/api/v1/assessment-attempts/mini-project/public', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          attemptId: 'attempt-e2e-1',
          assessmentTitle: 'Avaliação de SOFTWARE_DEVELOPMENT',
          authorName: 'Ana Silva',
          repositoryLink: 'https://github.com/ana/orders-api',
          finishedAt: '2026-05-18T14:30:00',
        },
      ]),
    })
  })

  await page.route('**/api/v1/assessment-attempts/mini-project/public/attempt-e2e-1', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(makeDetail({ voteRegistered, feedbackRegistered, reactionRegistered, submittedFeedback })),
    })
  })

  await page.route('**/api/v1/assessment-attempts/attempt-e2e-1/vote', async (route) => {
    expect(await route.request().postDataJSON()).toEqual({ voteType: 'APPROVE' })
    voteRegistered = true
    await route.fulfill({ status: 200 })
  })

  await page.route('**/api/v1/assessment-attempts/attempt-e2e-1/feedback', async (route) => {
    expect(await route.request().postDataJSON()).toEqual({ comment: submittedFeedback })
    feedbackRegistered = true
    await route.fulfill({ status: 201 })
  })

  await page.route('**/api/v1/assessment-feedbacks/feedback-e2e-1/react', async (route) => {
    expect(await route.request().postDataJSON()).toEqual({ reactionType: 'LIKE' })
    reactionRegistered = true
    await route.fulfill({ status: 200 })
  })
}

function makeDetail(state: {
  voteRegistered: boolean
  feedbackRegistered: boolean
  reactionRegistered: boolean
  submittedFeedback: string
}) {
  const feedbacks = [
    {
      id: 'feedback-e2e-1',
      comment: 'Boa organização do projeto.',
      author: {
        id: 'user-e2e-2',
        name: 'Roberto Lima',
      },
      submissionDate: '2026-05-19T09:00:00',
      reactions: state.reactionRegistered
        ? [
            {
              id: 'reaction-e2e-1',
              userId: 'user-e2e-3',
              userName: 'Maria Souza',
              type: 'LIKE',
              createdAt: '2026-05-19T09:30:00',
            },
          ]
        : [],
    },
  ]

  if (state.feedbackRegistered) {
    feedbacks.push({
      id: 'feedback-e2e-2',
      comment: state.submittedFeedback,
      author: {
        id: 'user-e2e-current',
        name: 'Revisor E2E',
      },
      submissionDate: '2026-05-19T10:00:00',
      reactions: [],
    })
  }

  return {
    attemptId: 'attempt-e2e-1',
    assessmentTitle: 'Avaliação de SOFTWARE_DEVELOPMENT',
    authorName: 'Ana Silva',
    finishedAt: '2026-05-18T14:30:00',
    repositoryLink: 'https://github.com/ana/orders-api',
    textResponse: 'README com instruções de execução e collection HTTP.',
    question: {
      id: 'question-e2e-1',
      title: 'API REST para pedidos',
      description: 'Implementar autenticação JWT e cadastro de pedidos.',
      knowledgeAreas: ['SOFTWARE_DEVELOPMENT'],
      difficulty: 'MEDIUM',
      relevance: 'FOUR',
    },
    voteSummary: {
      approves: state.voteRegistered ? 4 : 3,
      rejects: 1,
    },
    feedbacks,
  }
}

function uniqueId(): string {
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}
