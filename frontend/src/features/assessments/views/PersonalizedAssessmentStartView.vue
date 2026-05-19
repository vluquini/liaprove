<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import { startPersonalizedAssessment } from '../services/assessmentService'
import { saveCurrentAssessmentAttempt } from '../utils/assessmentSession'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const token = computed(() => String(route.params.token ?? ''))

async function startAssessment(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    const attempt = await startPersonalizedAssessment(token.value)
    saveCurrentAssessmentAttempt(attempt)
    await router.push(`/assessments/attempts/${attempt.attemptId}`)
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Avaliações
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">
            Iniciar avaliação personalizada
          </h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Confirme o início da avaliação enviada pelo recrutador. As questões serão exibidas somente após a tentativa
            ser criada.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card class="assessment-card">
        <template #content>
          <div class="flex flex-col gap-4">
            <div>
              <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Avaliação por convite</h2>
              <p class="mt-2 max-w-2xl text-sm text-[var(--liaprove-muted)]">
                Ao iniciar, o cronômetro da avaliação começará e a tentativa ficará vinculada à sua sessão atual.
              </p>
            </div>

            <div>
              <Button
                data-test="start-personalized-assessment"
                type="button"
                label="Iniciar avaliação personalizada"
                icon="pi pi-play-circle"
                :loading="loading"
                @click="startAssessment"
              />
            </div>
          </div>
        </template>
      </Card>
    </div>
  </AuthenticatedLayout>
</template>
