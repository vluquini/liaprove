import { describe, expect, it } from 'vitest'
import { formatAssessmentText, knowledgeAreaLabel } from './assessmentLabels'

describe('assessmentLabels', () => {
  it('renders knowledge area enum values as readable pt-BR labels', () => {
    expect(knowledgeAreaLabel('SOFTWARE_DEVELOPMENT')).toBe('Desenvolvimento de Software')
    expect(knowledgeAreaLabel('DATABASE')).toBe('Banco de Dados')
    expect(knowledgeAreaLabel('CYBERSECURITY')).toBe('Cibersegurança')
    expect(knowledgeAreaLabel('NETWORKS')).toBe('Redes')
    expect(knowledgeAreaLabel('AI')).toBe('Inteligência Artificial')
  })

  it('replaces backend enum tokens embedded in assessment texts', () => {
    expect(formatAssessmentText('Avaliação de SOFTWARE_DEVELOPMENT')).toBe(
      'Avaliação de Desenvolvimento de Software',
    )
    expect(formatAssessmentText('Certificado de Conclusão: Avaliação de DATABASE')).toBe(
      'Certificado de Conclusão: Avaliação de Banco de Dados',
    )
  })
})
