import { expect, test, type Page } from '@playwright/test'

const password = 'Teste123!'

test('professional starts and submits a system assessment', async ({ page }) => {
  const id = uniqueId()
  const name = `Profissional ${id}`
  const email = `assessment-${id}@example.com`

  await registerProfessional(page, { name, email })
  await mockSystemAssessmentFlow(page)

  await page.goto('/assessments/start')
  await expect(page.getByRole('heading', { name: 'Iniciar avaliação' })).toBeVisible()

  await page.locator('[data-test="knowledge-area"]').selectOption('SOFTWARE_DEVELOPMENT')
  await page.locator('[data-test="difficulty-level"]').selectOption('MEDIUM')
  await page.locator('[data-test="system-assessment-type"]').selectOption('MULTIPLE_CHOICE')
  await page.getByRole('button', { name: 'Iniciar avaliação' }).click()

  await expect(page).toHaveURL(/\/assessments\/attempts\/.+$/)
  await expect(page.getByRole('heading', { name: /Avaliacao/i })).toBeVisible()

  const questionRadioNames = await page.locator('input[type="radio"]').evaluateAll((inputs) =>
    Array.from(new Set(inputs.map((input) => input.getAttribute('name')).filter(Boolean))),
  )

  expect(questionRadioNames.length).toBeGreaterThan(0)

  for (const nameAttribute of questionRadioNames) {
    await page.locator(`input[name="${nameAttribute}"]`).first().check()
  }

  await page.getByRole('button', { name: 'Enviar respostas' }).click()

  await expect(page).toHaveURL(/\/assessments\/attempts\/.+\/result$/)
  await expect(page.getByRole('heading', { name: 'Resultado da avaliação' })).toBeVisible()
  await expect(page.getByText('Aproveitamento')).toBeVisible()
  await page.getByRole('button', { name: 'Ver certificado' }).click()
  await expect(page).toHaveURL(/\/certificates\/CERT-E2E-1$/)
  await expect(page.getByText('Certificado válido')).toBeVisible()
})

async function mockSystemAssessmentFlow(page: Page) {
  await page.route('**/api/v1/assessments/start-system', async (route) => {
    await route.fulfill({
      status: 201,
      contentType: 'application/json',
      body: JSON.stringify({
        attemptId: 'attempt-e2e-1',
        assessmentTitle: 'Avaliacao E2E Java',
        startedAt: new Date().toISOString(),
        evaluationTimerMinutes: 30,
        questions: [
          {
            id: 'question-e2e-1',
            title: 'Qual pratica reduz falhas em transacoes?',
            description: 'Considere uma API Java que precisa manter consistencia ao salvar dados relacionados.',
            alternatives: [
              { id: 'alternative-e2e-1', text: 'Usar transacoes e rollback para operacoes relacionadas.' },
              { id: 'alternative-e2e-2', text: 'Persistir cada etapa isoladamente sem tratamento de erro.' },
            ],
          },
        ],
      }),
    })
  })

  await page.route('**/api/v1/assessments/*/submit', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        attemptId: 'attempt-e2e-1',
        status: 'APPROVED',
        accuracyRate: 100,
        correctAnswers: 1,
        totalQuestions: 1,
        certificateUrl: 'https://liaprove.com/certificates/CERT-E2E-1',
        message: 'Aprovado',
      }),
    })
  })

  await page.route('**/api/v1/certificates/CERT-E2E-1', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        certificateNumber: 'CERT-E2E-1',
        title: 'Avaliacao E2E Java',
        description: 'Certificado emitido pelo LIA Prove.',
        certificateUrl: 'https://liaprove.com/certificates/CERT-E2E-1',
        issueDate: '2026-05-15',
        score: 100,
        owner: {
          id: 'user-e2e-1',
          name: 'Profissional E2E',
          occupation: 'Desenvolvedor Java',
          experienceLevel: 'JUNIOR',
        },
      }),
    })
  })
}

async function registerProfessional(page: Page, user: { name: string; email: string }) {
  await page.goto('/register')

  await page.locator('input[autocomplete="name"]').fill(user.name)
  await page.locator('input[autocomplete="email"]').fill(user.email)
  await page.locator('input[type="password"]').fill(password)
  await page.getByPlaceholder('Desenvolvedor Java').fill('Desenvolvedor Java')
  await page.getByPlaceholder('Java, Spring Boot, SQL').fill('Java, Spring Boot, SQL')
  await page.getByPlaceholder('Comunicação, liderança').fill('Comunicação, liderança')
  await page.getByRole('button', { name: 'Criar cadastro' }).click()

  await expect(page).toHaveURL(/\/dashboard$/)
}

function uniqueId(): string {
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}
