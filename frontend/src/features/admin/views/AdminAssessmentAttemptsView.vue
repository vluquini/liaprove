<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  listAdminAssessmentAttempts,
  type AdminAssessmentAttemptSummaryResponse,
  type AssessmentAttemptStatus,
  type ListAdminAssessmentAttemptsParams,
} from '../services/adminService'

const typeFilter = ref<'true' | 'false' | ''>('')
const statusFilter = ref<AssessmentAttemptStatus | ''>('')
const startDateFilter = ref('')
const endDateFilter = ref('')
const attempts = ref<AdminAssessmentAttemptSummaryResponse[]>([])
const loading = ref(true)
const errorMessage = ref('')

function currentParams(): ListAdminAssessmentAttemptsParams {
  return {
    isPersonalized: typeFilter.value === '' ? undefined : typeFilter.value === 'true',
    statuses: statusFilter.value ? [statusFilter.value] : undefined,
    startDate: startDateFilter.value || undefined,
    endDate: endDateFilter.value || undefined,
  }
}

async function loadAttempts(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    attempts.value = await listAdminAssessmentAttempts(currentParams())
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    attempts.value = []
  } finally {
    loading.value = false
  }
}

async function applyFilters(): Promise<void> {
  await loadAttempts()
}

function assessmentType(attempt: AdminAssessmentAttemptSummaryResponse): string {
  return attempt.assessment.personalized ? 'Personalizada' : 'Sistema'
}

function formatAccuracy(value: number | null): string {
  return value === null ? 'Sem nota' : `${value}%`
}

function formatDate(value: string | null | undefined): string {
  if (!value) {
    return 'Data não informada'
  }

  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

function skillsSummary(attempt: AdminAssessmentAttemptSummaryResponse): string {
  const skills = [...(attempt.candidate.hardSkills ?? []), ...(attempt.candidate.softSkills ?? [])]
  return skills.length > 0 ? skills.join(', ') : 'Competências não informadas'
}

onMounted(loadAttempts)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Administração
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Tentativas</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Audite tentativas de avaliações do sistema e avaliações personalizadas.
          </p>
        </div>
      </section>

      <Card class="profile-summary-card">
        <template #content>
          <form class="grid gap-4 lg:grid-cols-[180px_180px_1fr_1fr_auto]" @submit.prevent="applyFilters">
            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Tipo
              <select v-model="typeFilter" data-test="admin-attempt-filter-type" class="auth-input">
                <option value="">Todos</option>
                <option value="false">Sistema</option>
                <option value="true">Personalizada</option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Status
              <select v-model="statusFilter" data-test="admin-attempt-filter-status" class="auth-input">
                <option value="">Todos</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="APPROVED">APPROVED</option>
                <option value="FAILED">FAILED</option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Início
              <input
                v-model="startDateFilter"
                data-test="admin-attempt-filter-start"
                class="auth-input"
                type="text"
                placeholder="2026-05-01T00:00:00"
              />
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Fim
              <input
                v-model="endDateFilter"
                data-test="admin-attempt-filter-end"
                class="auth-input"
                type="text"
                placeholder="2026-05-31T23:59:59"
              />
            </label>

            <div class="flex items-end">
              <Button
                data-test="admin-attempt-apply-filters"
                type="submit"
                label="Filtrar"
                icon="pi pi-search"
                :disabled="loading"
                @click="applyFilters"
              />
            </div>
          </form>
        </template>
      </Card>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <div v-if="loading" class="status-message status-message-success">Carregando tentativas...</div>

      <Card v-else-if="attempts.length === 0 && !errorMessage" class="profile-summary-card">
        <template #content>
          <div class="flex flex-col gap-2">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Nenhuma tentativa encontrada.</h2>
            <p class="text-[var(--liaprove-muted)]">Ajuste os filtros para ampliar a busca.</p>
          </div>
        </template>
      </Card>

      <section v-else class="grid gap-4">
        <Card v-for="attempt in attempts" :key="attempt.attemptId" class="action-card">
          <template #content>
            <div class="grid gap-4 lg:grid-cols-[1.2fr_1fr_auto] lg:items-start">
              <div class="space-y-3">
                <div class="flex flex-wrap items-center gap-2">
                  <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">
                    {{ attempt.assessment.title }}
                  </h2>
                  <span class="question-meta-tag">{{ assessmentType(attempt) }}</span>
                  <span class="question-meta-tag">{{ attempt.status }}</span>
                </div>
                <p class="text-sm text-[var(--liaprove-muted)]">
                  Iniciada em {{ formatDate(attempt.startedAt) }}
                </p>
                <p class="text-sm text-[var(--liaprove-muted)]">
                  Finalizada em {{ formatDate(attempt.finishedAt) }}
                </p>
              </div>

              <div class="space-y-2 text-sm">
                <p class="font-semibold text-[var(--liaprove-ink)]">{{ attempt.candidate.name }}</p>
                <p class="text-[var(--liaprove-muted)]">{{ attempt.candidate.email }}</p>
                <p class="text-[var(--liaprove-muted)]">{{ attempt.candidate.experienceLevel || 'Experiência não informada' }}</p>
                <p class="text-[var(--liaprove-muted)]">{{ skillsSummary(attempt) }}</p>
              </div>

              <div class="rounded border border-[var(--liaprove-line)] p-4 text-center">
                <p class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Acurácia</p>
                <p class="mt-1 text-2xl font-semibold text-[var(--liaprove-ink)]">{{ formatAccuracy(attempt.accuracyRate) }}</p>
              </div>
            </div>
          </template>
        </Card>
      </section>
    </div>
  </AuthenticatedLayout>
</template>
