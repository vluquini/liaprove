/// <reference types="node" />

import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'
import { describe, expect, it } from 'vitest'

const cssPath = resolve(process.cwd(), 'src/shared/styles/main.css')

describe('main styles', () => {
  it('keeps dashboard cards readable over the PrimeVue theme', () => {
    const css = readFileSync(cssPath, 'utf8')

    expect(css).toContain('--p-card-background: #ffffff')
    expect(css).toContain('--p-card-color: var(--liaprove-ink)')
    expect(css).toContain('.action-card :is(.p-card-title, .p-card-content)')
    expect(css).toContain('.profile-summary-card :is(.p-card-title, .p-card-content)')
    expect(css).toContain('.profile-password-dialog')
    expect(css).toContain('.profile-password-dialog .profile-field > span')
  })

  it('styles recruiter quick action links as readable buttons', () => {
    const css = readFileSync(cssPath, 'utf8')

    expect(css).toContain('.recruiter-action-link')
    expect(css).toContain('display: inline-flex')
    expect(css).toContain('gap: 0.75rem')
    expect(css).toContain('.recruiter-action-link i')
  })

  it('keeps question curation screens readable over the PrimeVue theme', () => {
    const css = readFileSync(cssPath, 'utf8')

    expect(css).toContain('.question-card')
    expect(css).toContain('.question-form-card')
    expect(css).toContain('--p-card-background: #ffffff')
    expect(css).toContain('color: var(--liaprove-ink) !important')
    expect(css).toContain('.question-panel-dark')
    expect(css).toContain('color: #ffffff !important')
    expect(css).toContain('.question-check-option')
  })

  it('keeps assessment and certificate screens readable over the PrimeVue theme', () => {
    const css = readFileSync(cssPath, 'utf8')

    expect(css).toContain('.assessment-card')
    expect(css).toContain('.assessment-question-card')
    expect(css).toContain('.certificate-panel')
    expect(css).toContain('.assessment-progress-pill')
    expect(css).toContain('border-radius: 8px')
  })
})
