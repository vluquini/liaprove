<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  evaluateAssessmentAttempt,
  generateAttemptPreAnalysis,
  getAssessmentAttemptDetails,
  type AssessmentAttemptDetailsResponse,
  type AttemptPreAnalysisResponse,
  type AttemptQuestionDetailsResponse,
} from '../services/recruiterAssessmentService'

const route = useRoute()
const attempt = ref<AssessmentAttemptDetailsResponse | null>(null)
const preAnalysis = ref<AttemptPreAnalysisResponse | null>(null)
const loading = ref(false)
const generating = ref(false)
const evaluating = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const attemptId = computed(() => String(route.params.attemptId))

onMounted(() => {
  loadAttempt()
})

async function loadAttempt(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    attempt.value = await getAssessmentAttemptDetails(attemptId.value)
  } catch (error) {
    attempt.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

async function runPreAnalysis(): Promise<void> {
  generating.value = true
  errorMessage.value = ''

  try {
    preAnalysis.value = await generateAttemptPreAnalysis(attemptId.value)
  } catch (error) {
    preAnalysis.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    generating.value = false
  }
}

async function evaluate(finalStatus: 'APPROVED' | 'FAILED'): Promise<void> {
  evaluating.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const result = await evaluateAssessmentAttempt(attemptId.value, finalStatus)
    successMessage.value = typeof result.message === 'string' ? result.message : 'Avaliação final registrada.'
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    evaluating.value = false
  }
}

function answerText(question: AttemptQuestionDetailsResponse): string {
  if (!question.answer) {
    return 'Sem resposta.'
  }

  if (question.answer.textResponse) {
    return question.answer.textResponse
  }

  if (question.answer.projectUrl) {
    return question.answer.projectUrl
  }

  if (question.answer.selectedAlternativeId) {
    const alternative = question.alternatives.find((item) => item.id === question.answer?.selectedAlternativeId)
    return alternative?.text ?? question.answer.selectedAlternativeId
  }

  return 'Sem resposta.'
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <p v-if="loading" class="text-[var(--liaprove-muted)]">Carregando tentativa...</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <p v-if="successMessage" class="status-message status-message-success">{{ successMessage }}</p>

      <template v-if="attempt">
        <section class="dashboard-hero">
          <div>
            <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
              Revisão de candidato
            </p>
            <div class="mt-2 flex flex-wrap items-center gap-3">
              <h1 class="text-3xl font-semibold text-[var(--liaprove-ink)]">{{ attempt.candidate.name }}</h1>
              <Tag :value="attempt.status" severity="info" />
            </div>
            <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
              {{ attempt.assessment.title }} · {{ attempt.candidate.email }}
            </p>
          </div>
        </section>

        <div class="grid gap-4 xl:grid-cols-[minmax(0,1fr)_24rem]">
          <div class="space-y-4">
            <Card class="question-card">
              <template #title>Respostas</template>
              <template #content>
                <div class="space-y-3">
                  <article
                    v-for="question in attempt.questions"
                    :key="question.id"
                    class="rounded-lg border border-[var(--liaprove-line)] bg-white p-4"
                  >
                    <h2 class="font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</h2>
                    <p class="mt-1 text-sm text-[var(--liaprove-muted)]">{{ question.description }}</p>
                    <p v-if="question.guideline" class="mt-2 text-sm text-[var(--liaprove-muted)]">
                      Critério: {{ question.guideline }}
                    </p>
                    <div class="mt-3 rounded-md bg-[var(--liaprove-surface)] p-3 text-sm">
                      {{ answerText(question) }}
                    </div>
                  </article>
                </div>
              </template>
            </Card>

            <Card class="question-card">
              <template #title>Pré-análise</template>
              <template #content>
                <div v-if="!preAnalysis" class="space-y-3">
                  <p class="text-[var(--liaprove-muted)]">
                    Gere uma pré-análise para apoiar a decisão final do recrutador.
                  </p>
                  <Button
                    data-test="generate-pre-analysis"
                    label="Gerar pré-análise"
                    icon="pi pi-sparkles"
                    :loading="generating"
                    @click="runPreAnalysis"
                  />
                </div>

                <div v-else class="space-y-4">
                  <p>{{ preAnalysis.analysis.summary }}</p>
                  <section>
                    <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Forças</h2>
                    <ul class="mt-2 list-disc space-y-1 pl-5">
                      <li v-for="item in preAnalysis.analysis.strengths" :key="item">{{ item }}</li>
                    </ul>
                  </section>
                  <section>
                    <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Pontos de atenção</h2>
                    <ul class="mt-2 list-disc space-y-1 pl-5">
                      <li v-for="item in preAnalysis.analysis.attentionPoints" :key="item">{{ item }}</li>
                    </ul>
                  </section>
                  <p>{{ preAnalysis.analysis.finalExplanation }}</p>
                  <div class="flex flex-wrap gap-2">
                    <Tag
                      v-for="type in preAnalysis.metadata.ignoredQuestionTypes"
                      :key="type"
                      :value="type"
                      severity="secondary"
                    />
                  </div>
                </div>
              </template>
            </Card>
          </div>

          <aside class="space-y-4">
            <Card class="question-card">
              <template #title>Candidato</template>
              <template #content>
                <dl class="space-y-3 text-sm">
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Nome</dt>
                    <dd>{{ attempt.candidate.name }}</dd>
                  </div>
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Experiência</dt>
                    <dd>{{ attempt.explainability.candidateExperienceLevel || '-' }}</dd>
                  </div>
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Hard skills</dt>
                    <dd>{{ attempt.explainability.candidateHardSkills.join(', ') || '-' }}</dd>
                  </div>
                </dl>
              </template>
            </Card>

            <Card class="question-card">
              <template #title>Explainability</template>
              <template #content>
                <div class="grid gap-2 text-sm">
                  <p>{{ attempt.explainability.totalQuestions }} questões</p>
                  <p>{{ attempt.explainability.answeredQuestions }} respondidas</p>
                  <p>{{ attempt.explainability.multipleChoiceQuestions }} múltipla escolha</p>
                  <p>{{ attempt.explainability.openQuestions }} abertas</p>
                  <p>{{ attempt.explainability.projectQuestions }} projetos</p>
                  <p v-if="attempt.accuracyRate !== null">Acurácia {{ attempt.accuracyRate }}%</p>
                </div>
              </template>
            </Card>

            <div class="grid gap-2">
              <Button
                data-test="approve-candidate"
                label="Aprovar candidato"
                icon="pi pi-check"
                :loading="evaluating"
                @click="evaluate('APPROVED')"
              />
              <Button
                data-test="fail-candidate"
                label="Reprovar candidato"
                icon="pi pi-times"
                severity="danger"
                outlined
                :loading="evaluating"
                @click="evaluate('FAILED')"
              />
            </div>
          </aside>
        </div>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
