import { expect, test } from '@playwright/test'

test('registers a professional user and logs in with the same credentials', async ({ page }) => {
  const email = `e2e-${Date.now()}-${Math.random().toString(16).slice(2)}@example.com`
  const password = 'Teste123!'
  const name = 'Usuário E2E'

  await page.goto('/register')

  await expect(page.getByText('Criar cadastro').first()).toBeVisible()
  await page.locator('input[autocomplete="name"]').fill(name)
  await page.locator('input[autocomplete="email"]').fill(email)
  await page.locator('input[type="password"]').fill(password)
  await page.getByPlaceholder('Desenvolvedor Java').fill('Desenvolvedor Java')
  await page.getByPlaceholder('Java, Spring Boot, SQL').fill('Java, Spring Boot, SQL')
  await page.getByPlaceholder('Comunicação, liderança').fill('Comunicação, liderança')
  await page.getByRole('button', { name: 'Criar cadastro' }).click()

  await expect(page).toHaveURL(/\/dashboard$/)
  await expect(page.getByText('Dashboard')).toBeVisible()
  await expect(page.getByText(`Olá, ${name}. Seu perfil atual é PROFESSIONAL.`)).toBeVisible()

  await page.getByRole('button', { name: 'Sair' }).click()
  await expect(page).toHaveURL(/\/login$/)

  await page.locator('input[autocomplete="email"]').fill(email)
  await page.locator('input[type="password"]').fill(password)
  await page.getByRole('button', { name: 'Entrar' }).click()

  await expect(page).toHaveURL(/\/dashboard$/)
  await expect(page.getByText(`Olá, ${name}. Seu perfil atual é PROFESSIONAL.`)).toBeVisible()
})
