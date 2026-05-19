<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  listPersonalizedAssessments,
  type PersonalizedAssessmentDetailsResponse,
} from '../services/recruiterAssessmentService'

const assessments = ref<PersonalizedAssessmentDetailsResponse[]>([])
const loading = ref(false)
const errorMessage = ref('')
const copyMessage = ref('')

onMounted(() => {
  loadAssessments()
})

async function loadAssessments(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    assessments.value = await listPersonalizedAssessments()
  } catch (error) {
    assessments.value = []
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

async function copyShareLink(token: string): Promise<void> {
  const shareUrl = `${window.location.origin}/assessments/personalized/${token}/start`

  if (navigator.clipboard?.writeText) {
    await navigator.clipboard.writeText(shareUrl)
  }

  copyMessage.value = 'Link copiado.'
}

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(value))
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Recrutamento
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Área do recrutador</h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Gerencie avaliações personalizadas, analise vagas e acompanhe candidatos em um fluxo direto.
          </p>
        </div>
      </section>

      <section class="grid gap-3 md:grid-cols-3">
        <RouterLink to="/recruiter/job-analysis" class="recruiter-action-link">
          <i class="pi pi-search" aria-hidden="true" />
          <span>Analisar vaga</span>
        </RouterLink>
        <RouterLink to="/recruiter/assessments/new" class="recruiter-action-link">
          <i class="pi pi-list-check" aria-hidden="true" />
          <span>Criar avaliação</span>
        </RouterLink>
        <RouterLink to="/recruiter/questions/open/new" class="recruiter-action-link">
          <i class="pi pi-file-edit" aria-hidden="true" />
          <span>Criar questão aberta</span>
        </RouterLink>
      </section>

      <p v-if="copyMessage" class="status-message status-message-success">{{ copyMessage }}</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card class="question-card">
        <template #title>Avaliações personalizadas</template>
        <template #content>
          <p v-if="loading" class="text-[var(--liaprove-muted)]">Carregando avaliações...</p>

          <div v-else-if="assessments.length === 0" class="space-y-3 text-[var(--liaprove-muted)]">
            <p class="font-medium text-[var(--liaprove-ink)]">Nenhuma avaliação personalizada criada</p>
            <p>Comece criando uma avaliação a partir de uma vaga ou selecionando questões manualmente.</p>
            <RouterLink to="/recruiter/assessments/new">
              <Button label="Criar avaliação" icon="pi pi-plus" size="small" />
            </RouterLink>
          </div>

          <div v-else class="space-y-3">
            <article
              v-for="assessment in assessments"
              :key="assessment.id"
              class="rounded-lg border border-[var(--liaprove-line)] bg-white p-4"
            >
              <div class="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                <div class="min-w-0 space-y-2">
                  <div class="flex flex-wrap items-center gap-2">
                    <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">{{ assessment.title }}</h2>
                    <Tag :value="assessment.status" severity="info" />
                  </div>
                  <p class="line-clamp-2 text-sm text-[var(--liaprove-muted)]">{{ assessment.description }}</p>
                  <dl class="grid gap-3 text-sm sm:grid-cols-3">
                    <div>
                      <dt class="font-semibold text-[var(--liaprove-muted)]">Expiração</dt>
                      <dd>{{ formatDate(assessment.expirationDate) }}</dd>
                    </div>
                    <div>
                      <dt class="font-semibold text-[var(--liaprove-muted)]">Tentativas</dt>
                      <dd>{{ assessment.totalAttempts }}/{{ assessment.maxAttempts }}</dd>
                    </div>
                    <div>
                      <dt class="font-semibold text-[var(--liaprove-muted)]">Questões</dt>
                      <dd>{{ assessment.questions.length }}</dd>
                    </div>
                  </dl>
                </div>

                <div class="flex shrink-0 flex-wrap gap-2">
                  <Button
                    label="Copiar link"
                    icon="pi pi-link"
                    severity="secondary"
                    outlined
                    size="small"
                    @click="copyShareLink(assessment.shareableToken)"
                  />
                  <RouterLink :to="`/recruiter/assessments/${assessment.id}`">
                    <Button label="Detalhes" icon="pi pi-arrow-right" icon-pos="right" size="small" />
                  </RouterLink>
                </div>
              </div>
            </article>
          </div>
        </template>
      </Card>
    </div>
  </AuthenticatedLayout>
</template>
