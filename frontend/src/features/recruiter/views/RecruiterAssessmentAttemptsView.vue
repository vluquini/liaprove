<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  listAssessmentAttempts,
  type AssessmentAttemptSummaryResponse,
} from '../services/recruiterAssessmentService'

const route = useRoute()
const attempts = ref<AssessmentAttemptSummaryResponse[]>([])
const loading = ref(false)
const errorMessage = ref('')

const assessmentId = computed(() => String(route.params.assessmentId))

onMounted(() => {
  loadAttempts()
})

async function loadAttempts(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    attempts.value = await listAssessmentAttempts(assessmentId.value)
  } catch (error) {
    attempts.value = []
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

function accuracyLabel(value: number | null): string {
  return value === null ? '-' : `${value}%`
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Candidatos
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Tentativas da avaliação</h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Acompanhe candidatos, status e desempenho antes da avaliação final.
          </p>
        </div>
      </section>

      <p v-if="loading" class="text-[var(--liaprove-muted)]">Carregando tentativas...</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card class="question-card">
        <template #content>
          <p v-if="!loading && attempts.length === 0" class="text-[var(--liaprove-muted)]">
            Nenhuma tentativa registrada.
          </p>

          <div v-else class="space-y-3">
            <article
              v-for="attempt in attempts"
              :key="attempt.attemptId"
              class="rounded-lg border border-[var(--liaprove-line)] bg-white p-4"
            >
              <div class="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
                <div>
                  <h2 class="font-semibold text-[var(--liaprove-ink)]">{{ attempt.candidateName }}</h2>
                  <p class="text-sm text-[var(--liaprove-muted)]">{{ attempt.candidateEmail }}</p>
                  <div class="mt-2 flex flex-wrap gap-2">
                    <Tag :value="attempt.status" severity="info" />
                    <Tag :value="accuracyLabel(attempt.accuracyRate)" severity="success" />
                  </div>
                </div>
                <RouterLink :to="`/recruiter/attempts/${attempt.attemptId}`">
                  <Button label="Abrir" icon="pi pi-arrow-right" icon-pos="right" size="small" />
                </RouterLink>
              </div>
            </article>
          </div>
        </template>
      </Card>
    </div>
  </AuthenticatedLayout>
</template>
