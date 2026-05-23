<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { formatAssessmentText } from '@/features/assessments/utils/assessmentLabels'
import { normalizeApiError } from '@/shared/api/errors'
import { listPublicMiniProjectAttempts, type PublicMiniProjectAttemptResponse } from '../services/miniProjectService'

const loading = ref(true)
const errorMessage = ref('')
const attempts = ref<PublicMiniProjectAttemptResponse[]>([])

const hasAttempts = computed(() => attempts.value.length > 0)

function formatDate(value: string | null): string {
  if (!value) {
    return 'Data não informada'
  }

  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(value))
}

async function loadAttempts(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    attempts.value = await listPublicMiniProjectAttempts()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    attempts.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadAttempts)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Avaliação comunitária
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Mini-projetos públicos</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Selecione uma entrega prática para analisar o enunciado, revisar a resposta e contribuir com a curadoria.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <div v-if="loading" class="status-message status-message-success">Carregando entregas públicas...</div>

      <Card v-else-if="!hasAttempts && !errorMessage" class="profile-summary-card">
        <template #content>
          <div class="flex flex-col gap-3">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Nenhuma entrega pública disponível.</h2>
            <p class="text-[var(--liaprove-muted)]">
              Quando houver mini-projetos concluídos para avaliação comunitária, eles aparecerão aqui.
            </p>
          </div>
        </template>
      </Card>

      <section v-else class="grid gap-4 lg:grid-cols-2">
        <Card v-for="attempt in attempts" :key="attempt.attemptId" class="mini-project-card">
          <template #content>
            <div class="flex h-full flex-col gap-5">
              <div class="flex items-start gap-3">
                <span class="action-icon">
                  <i class="pi pi-code" aria-hidden="true" />
                </span>
                <div>
                  <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">
                    {{ formatAssessmentText(attempt.assessmentTitle) || 'Mini-projeto sem título' }}
                  </h2>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                    {{ attempt.authorName || 'Autor não informado' }} · Finalizado em {{ formatDate(attempt.finishedAt) }}
                  </p>
                </div>
              </div>

              <RouterLink
                :data-test="`view-mini-project-details-${attempt.attemptId}`"
                class="p-button p-component p-button-sm w-fit"
                :to="{ name: 'mini-project-public-detail', params: { attemptId: attempt.attemptId } }"
              >
                <span class="p-button-icon p-button-icon-left pi pi-search" aria-hidden="true" />
                <span class="p-button-label">Ver detalhes</span>
              </RouterLink>
            </div>
          </template>
        </Card>
      </section>
    </div>
  </AuthenticatedLayout>
</template>
