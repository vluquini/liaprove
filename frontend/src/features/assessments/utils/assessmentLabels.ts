import type { KnowledgeArea } from '../services/assessmentService'

const knowledgeAreaLabels: Record<KnowledgeArea, string> = {
  SOFTWARE_DEVELOPMENT: 'Desenvolvimento de Software',
  DATABASE: 'Banco de Dados',
  CYBERSECURITY: 'Cibersegurança',
  NETWORKS: 'Redes',
  AI: 'Inteligência Artificial',
}

export function knowledgeAreaLabel(value: string | null | undefined): string {
  if (!value) {
    return ''
  }

  return knowledgeAreaLabels[value as KnowledgeArea] ?? value
}

export function formatAssessmentText(value: string | null | undefined): string {
  if (!value) {
    return ''
  }

  return Object.entries(knowledgeAreaLabels).reduce(
    (text, [enumValue, label]) => text.replaceAll(enumValue, label),
    value,
  )
}
