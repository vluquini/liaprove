import { expect, test, type Page } from '@playwright/test'

const password = 'Teste123!'

test('recruiter dashboard shows core question and assessment actions', async ({ page }) => {
  const id = uniqueId()
  const name = `Recrutador ${id}`
  const email = `recruiter-${id}@example.com`

  await page.goto('/register')

  await page.locator('.p-select').first().click()
  await page.getByRole('option', { name: 'Recrutador' }).click()
  await page.locator('input[autocomplete="name"]').fill(name)
  await page.locator('input[autocomplete="email"]').fill(email)
  await page.locator('input[type="password"]').fill(password)
  await page.getByPlaceholder('Desenvolvedor Java').fill('Recrutador Técnico')
  await page.getByPlaceholder('Java, Spring Boot, SQL').fill('Java, Spring Boot, SQL')
  await page.getByPlaceholder('Comunicação, liderança').fill('Comunicação, liderança')
  await page.locator('label').filter({ hasText: 'Empresa' }).locator('input').fill('LIA Recruiting')
  await page.locator('label').filter({ hasText: 'E-mail corporativo' }).locator('input').fill(`corp-${id}@lia.example.com`)
  await page.getByRole('button', { name: 'Criar cadastro' }).click()

  await expect(page).toHaveURL(/\/dashboard$/)
  await expect(page.getByRole('heading', { name: `Olá, ${name}` })).toBeVisible()
  await expect(page.getByText('RECRUITER')).toBeVisible()
  await expect(page.getByText('Iniciar avaliação')).toBeVisible()
  await expect(page.getByText('Submeter questão')).toBeVisible()
  await expect(page.getByText('Votar em questões')).toBeVisible()
  await expect(page.getByText('Analisar vaga')).toBeVisible()
})

test('professional submits a question, votes, and sends feedback', async ({ page }) => {
  const id = uniqueId()
  const name = `Profissional ${id}`
  const email = `professional-${id}@example.com`
  const questionTitle = `Como validar transacoes REST ${id}?`
  const questionDescription =
    'Qual abordagem melhora a consistencia de dados em uma API REST com banco relacional durante operacoes criticas?'
  const feedback = `Feedback E2E ${id}: questão clara e relevante para curadoria.`

  await registerProfessional(page, { name, email })

  await page.goto('/questions/new')
  await expect(page.getByRole('heading', { name: 'Submeter questão' })).toBeVisible()

  await page.locator('[data-test="question-title"]').fill(questionTitle)
  await page.locator('[data-test="question-description"]').fill(questionDescription)
  await page.locator('[data-test="area-SOFTWARE_DEVELOPMENT"]').check()
  await page.locator('[data-test="alternative-0"]').fill('Usar transacoes no caso de uso.')
  await page.locator('[data-test="alternative-1"]').fill('Ignorar rollback em erros.')
  await page.locator('[data-test="alternative-2"]').fill('Persistir parcialmente os dados.')

  await page.getByRole('button', { name: 'Executar pré-análise' }).click()

  await expect(page.getByText('E2E: revise a clareza do enunciado.')).toBeVisible()
  await page.locator('[data-test="accept-language-0"]').check()
  await page.getByRole('button', { name: 'Enviar questão' }).click()

  await expect(page).toHaveURL(/\/questions\/voting$/)
  await expect(page.getByRole('heading', { name: 'Questões em votação' })).toBeVisible()
  await expect(page.getByText(questionTitle)).toBeVisible()

  const questionCard = page.locator('.question-card').filter({ hasText: questionTitle })
  await questionCard.getByRole('button', { name: 'Ver detalhes' }).click()

  await expect(page).toHaveURL(/\/questions\/.+\/voting$/)
  await expect(page.getByRole('heading', { name: questionTitle })).toBeVisible()
  await expect(page.getByText(questionDescription)).toBeVisible()
  await expect(page.getByText('Usar transacoes no caso de uso.')).toBeVisible()

  await page.getByRole('button', { name: 'Aprovar' }).click()
  await expect(page.getByText('Voto registrado com sucesso.')).toBeVisible()

  await page.locator('[data-test="feedback-comment"]').fill(feedback)
  await page.getByRole('button', { name: 'Enviar feedback' }).click()
  await expect(page.getByText('Feedback enviado com sucesso.')).toBeVisible()
  await expect(page.getByText(feedback)).toBeVisible()
})

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
