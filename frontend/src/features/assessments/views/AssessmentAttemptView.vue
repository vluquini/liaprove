<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  submitAssessment,
  type AssessmentAttemptResponse,
  type SubmitAssessmentAnswerRequest,
} from '../services/assessmentService'
import { formatAssessmentText } from '../utils/assessmentLabels'
import {
  clearCurrentAssessmentAttempt,
  readCurrentAssessmentAttempt,
  saveAssessmentResult,
} from '../utils/assessmentSession'

const route = useRoute()
const router = useRouter()
const attemptId = computed(() => String(route.params.attemptId ?? ''))
const attempt = ref<AssessmentAttemptResponse | null>(readCurrentAssessmentAttempt(attemptId.value))
const selectedAlternatives = reactive<Record<string, string>>({})
const projectUrls = reactive<Record<string, string>>({})
const textResponses = reactive<Record<string, string>>({})
const submitting = ref(false)
const errorMessage = ref('')
const assessmentTitle = computed(() =>
  attempt.value ? formatAssessmentText(attempt.value.assessmentTitle) : 'Tentativa indisponível',
)

function hasAlternatives(question: AssessmentAttemptResponse['questions'][number]): boolean {
  return Boolean(question.alternatives?.length)
}

function buildAnswers(): SubmitAssessmentAnswerRequest[] {
  if (!attempt.value) {
    return []
  }

  return attempt.value.questions.map((question) => ({
    questionId: question.id,
    selectedAlternativeId: selectedAlternatives[question.id] ?? null,
    projectUrl: projectUrls[question.id]?.trim() || null,
    textResponse: textResponses[question.id]?.trim() || null,
  }))
}

function validateAnswers(): string | null {
  if (!attempt.value) {
    return 'Não foi possível carregar esta tentativa neste dispositivo.'
  }

  const unansweredMultipleChoice = attempt.value.questions.some(
    (question) => hasAlternatives(question) && !selectedAlternatives[question.id],
  )

  return unansweredMultipleChoice ? 'Responda todas as questões antes de enviar.' : null
}

async function submit(): Promise<void> {
  const validation = validateAnswers()

  if (validation) {
    errorMessage.value = validation
    return
  }

  submitting.value = true
  errorMessage.value = ''

  try {
    const result = await submitAssessment(attemptId.value, { answers: buildAnswers() })
    saveAssessmentResult(attemptId.value, result)
    clearCurrentAssessmentAttempt()
    await router.push(`/assessments/attempts/${attemptId.value}/result`)
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Tentativa
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">
            {{ assessmentTitle }}
          </h1>
          <p v-if="attempt" class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Responda as questões desta avaliação. Depois do envio, o relatório exibido é temporário.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card v-if="!attempt" class="assessment-card">
        <template #content>
          <div class="flex flex-col gap-4">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">
              Não foi possível carregar esta tentativa neste dispositivo.
            </h2>
            <p class="text-[var(--liaprove-muted)]">
              Por segurança, as questões não são recarregadas depois. Inicie uma nova avaliação para continuar.
            </p>
            <RouterLink to="/assessments/start">
              <Button label="Iniciar nova avaliação" icon="pi pi-play-circle" />
            </RouterLink>
          </div>
        </template>
      </Card>

      <template v-else>
        <div class="flex flex-wrap gap-2">
          <span class="assessment-progress-pill">{{ attempt.questions.length }} questões</span>
          <span class="assessment-progress-pill">{{ attempt.evaluationTimerMinutes }} min</span>
        </div>

        <section class="space-y-4">
          <Card v-for="(question, index) in attempt.questions" :key="question.id" class="assessment-question-card">
            <template #content>
              <div class="space-y-4">
                <div>
                  <p class="text-sm font-semibold text-[var(--liaprove-accent-strong)]">Questão {{ index + 1 }}</p>
                  <h2 class="mt-1 text-xl font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</h2>
                  <p class="mt-2 text-[var(--liaprove-muted)]">{{ question.description }}</p>
                </div>

                <div v-if="hasAlternatives(question)" class="space-y-3">
                  <label
                    v-for="alternative in question.alternatives"
                    :key="alternative.id"
                    class="flex gap-3 rounded-md border border-[var(--liaprove-line)] bg-white p-3 text-[var(--liaprove-ink)]"
                  >
                    <input
                      v-model="selectedAlternatives[question.id]"
                      type="radio"
                      :name="`question-${question.id}`"
                      :value="alternative.id"
                      :data-test="`answer-${question.id}-${alternative.id}`"
                    />
                    <span>{{ alternative.text }}</span>
                  </label>
                </div>

                <label v-else class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                  Link da entrega
                  <input
                    v-model="projectUrls[question.id]"
                    :data-test="`project-url-${question.id}`"
                    class="rounded-md border border-[var(--liaprove-line)] bg-white px-3 py-2 text-[var(--liaprove-ink)]"
                    placeholder="https://github.com/usuario/projeto"
                  />
                </label>
              </div>
            </template>
          </Card>
        </section>

        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <RouterLink to="/dashboard">
            <Button label="Voltar ao dashboard" icon="pi pi-arrow-left" severity="secondary" outlined />
          </RouterLink>
          <Button
            data-test="submit-assessment"
            label="Enviar respostas"
            icon="pi pi-send"
            icon-pos="right"
            :loading="submitting"
            @click="submit"
          />
        </div>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
