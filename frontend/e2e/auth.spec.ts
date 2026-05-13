import { expect, test } from '@playwright/test'

test('registers, logs in, and opens the authenticated profile flow', async ({ page }) => {
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
  await expect(page.getByRole('link', { name: 'Dashboard' })).toBeVisible()
  await expect(page.getByRole('heading', { name: `Olá, ${name}` })).toBeVisible()
  await expect(page.getByText('Resumo do perfil')).toBeVisible()
  await expect(page.getByText('Tipo')).toBeVisible()
  await expect(page.getByText('PROFESSIONAL')).toBeVisible()

  await page.getByRole('button', { name: 'Sair' }).click()
  await expect(page).toHaveURL(/\/login$/)

  await page.locator('input[autocomplete="email"]').fill(email)
  await page.locator('input[type="password"]').fill(password)
  await page.getByRole('button', { name: 'Entrar' }).click()

  await expect(page).toHaveURL(/\/dashboard$/)
  await expect(page.getByRole('heading', { name: `Olá, ${name}` })).toBeVisible()

  await page.getByRole('link', { name: 'Perfil' }).click()

  await expect(page).toHaveURL(/\/profile$/)
  await expect(page.getByRole('heading', { name })).toBeVisible()
  await expect(page.getByText('Revise seus dados básicos')).toBeVisible()
  await expect(page.getByText('Nome')).toBeVisible()
  await expect(page.getByText('E-mail')).toBeVisible()
  await expect(page.locator('[data-test="profile-name"]')).toHaveValue(name)
  await expect(page.locator('[data-test="profile-email"]')).toHaveValue(email)
  await expect(page.getByRole('button', { name: 'Salvar perfil' })).toBeVisible()
  await expect(page.getByRole('button', { name: 'Alterar senha' })).toBeVisible()
  await expect(page.getByRole('button', { name: 'Desativar conta' })).toBeVisible()

  await page.getByRole('button', { name: 'Alterar senha' }).click()

  await expect(page.getByRole('dialog', { name: 'Alterar senha' })).toBeVisible()
  await expect(page.getByText('Senha atual')).toBeVisible()
  await expect(page.getByText('Nova senha')).toBeVisible()
  await page.getByRole('button', { name: 'Cancelar' }).click()
  await expect(page.getByRole('dialog', { name: 'Alterar senha' })).not.toBeVisible()
})
