<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  startSystemAssessment,
  type DifficultyLevel,
  type KnowledgeArea,
  type StartSystemAssessmentRequest,
  type SystemAssessmentType,
} from '../services/assessmentService'
import { knowledgeAreaLabel } from '../utils/assessmentLabels'
import { saveCurrentAssessmentAttempt } from '../utils/assessmentSession'

const router = useRouter()

const knowledgeAreas: Array<{ label: string; value: KnowledgeArea }> = [
  { label: knowledgeAreaLabel('SOFTWARE_DEVELOPMENT'), value: 'SOFTWARE_DEVELOPMENT' },
  { label: knowledgeAreaLabel('DATABASE'), value: 'DATABASE' },
  { label: knowledgeAreaLabel('CYBERSECURITY'), value: 'CYBERSECURITY' },
  { label: knowledgeAreaLabel('NETWORKS'), value: 'NETWORKS' },
  { label: knowledgeAreaLabel('AI'), value: 'AI' },
]

const difficultyLevels: Array<{ label: string; value: DifficultyLevel }> = [
  { label: 'Fácil', value: 'EASY' },
  { label: 'Média', value: 'MEDIUM' },
  { label: 'Difícil', value: 'HARD' },
]

const assessmentTypes: Array<{ label: string; value: SystemAssessmentType }> = [
  { label: 'Múltipla escolha', value: 'MULTIPLE_CHOICE' },
  { label: 'Mini-projeto', value: 'PROJECT' },
]

const form = reactive<StartSystemAssessmentRequest>({
  knowledgeArea: 'SOFTWARE_DEVELOPMENT',
  difficultyLevel: 'MEDIUM',
  type: 'MULTIPLE_CHOICE',
})
const loading = ref(false)
const errorMessage = ref('')

async function startAssessment(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    const attempt = await startSystemAssessment({ ...form })
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
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Iniciar avaliação</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Escolha os critérios da avaliação do sistema. As questões ficam disponíveis apenas durante esta tentativa.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card class="assessment-card">
        <template #content>
          <form class="grid gap-5 lg:grid-cols-3" @submit.prevent="startAssessment">
            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Área de conhecimento
              <select
                v-model="form.knowledgeArea"
                data-test="knowledge-area"
                class="rounded-md border border-[var(--liaprove-line)] bg-white px-3 py-2 text-[var(--liaprove-ink)]"
              >
                <option v-for="area in knowledgeAreas" :key="area.value" :value="area.value">
                  {{ area.label }}
                </option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Dificuldade
              <select
                v-model="form.difficultyLevel"
                data-test="difficulty-level"
                class="rounded-md border border-[var(--liaprove-line)] bg-white px-3 py-2 text-[var(--liaprove-ink)]"
              >
                <option v-for="difficulty in difficultyLevels" :key="difficulty.value" :value="difficulty.value">
                  {{ difficulty.label }}
                </option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Tipo
              <select
                v-model="form.type"
                data-test="system-assessment-type"
                class="rounded-md border border-[var(--liaprove-line)] bg-white px-3 py-2 text-[var(--liaprove-ink)]"
              >
                <option v-for="type in assessmentTypes" :key="type.value" :value="type.value">
                  {{ type.label }}
                </option>
              </select>
            </label>

            <div class="flex items-end lg:col-span-3">
              <Button
                data-test="start-assessment"
                type="button"
                label="Iniciar avaliação"
                icon="pi pi-play-circle"
                :loading="loading"
                @click="startAssessment"
              />
            </div>
          </form>
        </template>
      </Card>
    </div>
  </AuthenticatedLayout>
</template>
