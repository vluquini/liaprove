<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  preAnalyzeQuestion,
  submitQuestion,
  type DifficultyLevel,
  type KnowledgeArea,
  type PreAnalyzeQuestionResponse,
  type RelevanceLevel,
  type SubmitQuestionRequest,
} from '../services/questionService'

const router = useRouter()

type SubmissionQuestionType = SubmitQuestionRequest['type']

const knowledgeAreaOptions: KnowledgeArea[] = ['SOFTWARE_DEVELOPMENT', 'DATABASE', 'CYBERSECURITY', 'NETWORKS', 'AI']
const difficultyOptions: DifficultyLevel[] = ['EASY', 'MEDIUM', 'HARD']
const relevanceOptions: RelevanceLevel[] = ['ONE', 'TWO', 'THREE', 'FOUR', 'FIVE']
const questionTypeOptions: { label: string; value: SubmissionQuestionType }[] = [
  { label: 'Múltipla escolha', value: 'MULTIPLE_CHOICE' },
  { label: 'Mini-projeto', value: 'PROJECT' },
]

const analyzing = ref(false)
const submitting = ref(false)
const message = ref('')
const errorMessage = ref('')
const preAnalysis = ref<PreAnalyzeQuestionResponse | null>(null)
const selectedLanguageSuggestions = ref<string[]>([])
const selectedBiasWarnings = ref<string[]>([])
const selectedDistractorSuggestions = ref<string[]>([])
const selectedTopicNotes = ref<string[]>([])
const acceptDifficultySuggestion = ref(false)
let preAnalysisRequestId = 0

const form = reactive({
  type: 'MULTIPLE_CHOICE' as SubmissionQuestionType,
  title: '',
  description: '',
  knowledgeAreas: [] as KnowledgeArea[],
  difficultyByCommunity: 'MEDIUM' as DifficultyLevel,
  relevanceByCommunity: 'THREE' as RelevanceLevel,
  alternatives: [
    { text: '', correct: true },
    { text: '', correct: false },
    { text: '', correct: false },
  ],
})

function toggleKnowledgeArea(area: KnowledgeArea, checked: boolean): void {
  if (checked && !form.knowledgeAreas.includes(area)) {
    form.knowledgeAreas.push(area)
    return
  }

  if (!checked) {
    form.knowledgeAreas = form.knowledgeAreas.filter((candidate) => candidate !== area)
  }
}

function setCorrectAlternative(index: number): void {
  form.alternatives.forEach((alternative, alternativeIndex) => {
    alternative.correct = alternativeIndex === index
  })
}

function validateForm(): string | null {
  const titleLength = form.title.trim().length
  if (titleLength < 10 || titleLength > 255) {
    return 'O título deve ter entre 10 e 255 caracteres.'
  }

  const descriptionLength = form.description.trim().length
  if (descriptionLength < 20 || descriptionLength > 2000) {
    return 'A descrição deve ter entre 20 e 2000 caracteres.'
  }

  if (form.knowledgeAreas.length === 0) {
    return 'Selecione ao menos uma área de conhecimento.'
  }

  if (form.type === 'PROJECT') {
    return null
  }

  const filledAlternatives = form.alternatives.filter((alternative) => alternative.text.trim())
  const correctCount = filledAlternatives.filter((alternative) => alternative.correct).length

  if (filledAlternatives.length < 3 || correctCount !== 1) {
    return 'Informe ao menos 3 alternativas e marque exatamente uma correta.'
  }

  return null
}

function buildRequest(): SubmitQuestionRequest {
  const commonRequest = {
    title: form.title.trim(),
    description: form.description.trim(),
    knowledgeAreas: [...form.knowledgeAreas],
    difficultyByCommunity: form.difficultyByCommunity,
    relevanceByCommunity: form.relevanceByCommunity,
    acceptedLanguageSuggestions: selectedLanguageSuggestions.value,
    acceptedBiasOrAmbiguityWarnings: selectedBiasWarnings.value,
    acceptedDistractorSuggestions: selectedDistractorSuggestions.value,
    acceptedDifficultyLevelByLLM: acceptDifficultySuggestion.value
      ? preAnalysis.value?.difficultyLevelByLLM ?? undefined
      : undefined,
    acceptedTopicConsistencyNotes: selectedTopicNotes.value,
  }

  if (form.type === 'PROJECT') {
    return {
      ...commonRequest,
      type: 'PROJECT',
    }
  }

  return {
    ...commonRequest,
    type: 'MULTIPLE_CHOICE',
    alternatives: form.alternatives
      .filter((alternative) => alternative.text.trim())
      .map((alternative) => ({ text: alternative.text.trim(), correct: alternative.correct })),
  }
}

function resetAcceptedSuggestions(): void {
  selectedLanguageSuggestions.value = []
  selectedBiasWarnings.value = []
  selectedDistractorSuggestions.value = []
  selectedTopicNotes.value = []
  acceptDifficultySuggestion.value = false
}

function selectQuestionType(type: SubmissionQuestionType): void {
  if (form.type === type) {
    return
  }

  form.type = type
  preAnalysisRequestId += 1
  analyzing.value = false
  preAnalysis.value = null
  message.value = ''
  errorMessage.value = ''
  resetAcceptedSuggestions()
}

async function runPreAnalysis(): Promise<void> {
  const validationMessage = validateForm()
  if (validationMessage) {
    errorMessage.value = validationMessage
    return
  }

  const requestId = ++preAnalysisRequestId
  analyzing.value = true
  message.value = ''
  errorMessage.value = ''
  resetAcceptedSuggestions()

  try {
    const response = await preAnalyzeQuestion(buildRequest())
    if (requestId !== preAnalysisRequestId) {
      return
    }

    preAnalysis.value = response
    message.value = 'Pré-análise concluída. Revise as sugestões antes do envio.'
  } catch (error) {
    if (requestId !== preAnalysisRequestId) {
      return
    }

    preAnalysis.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    if (requestId === preAnalysisRequestId) {
      analyzing.value = false
    }
  }
}

async function sendQuestion(): Promise<void> {
  const validationMessage = validateForm()
  if (validationMessage) {
    errorMessage.value = validationMessage
    return
  }

  submitting.value = true
  message.value = ''
  errorMessage.value = ''

  try {
    await submitQuestion(buildRequest())
    await router.push('/questions/voting')
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
            Submissão
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Submeter questão</h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Crie uma questão para curadoria comunitária e, se desejar, execute a pré-análise por IA antes do envio.
          </p>
        </div>
      </section>

      <p v-if="message" class="status-message status-message-success">{{ message }}</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <div class="grid gap-4 xl:grid-cols-[minmax(0,1fr)_24rem]">
        <Card class="question-form-card">
          <template #content>
            <form class="space-y-5" @submit.prevent="sendQuestion">
              <div class="grid gap-4 lg:grid-cols-2">
                <fieldset class="space-y-2 lg:col-span-2">
                  <legend class="text-sm font-bold text-[var(--liaprove-ink)]">Tipo</legend>
                  <div class="flex flex-wrap gap-2">
                    <Button
                      v-for="option in questionTypeOptions"
                      :key="option.value"
                      :data-test="`question-type-${option.value}`"
                      type="button"
                      :label="option.label"
                      severity="secondary"
                      :outlined="form.type !== option.value"
                      :aria-pressed="form.type === option.value"
                      @click="selectQuestionType(option.value)"
                    />
                  </div>
                </fieldset>

                <label class="profile-field lg:col-span-2">
                  <span>Título</span>
                  <InputText data-test="question-title" v-model="form.title" />
                </label>

                <label class="profile-field lg:col-span-2">
                  <span>Descrição</span>
                  <Textarea data-test="question-description" v-model="form.description" rows="5" />
                </label>

                <label class="profile-field">
                  <span>Dificuldade</span>
                  <Select v-model="form.difficultyByCommunity" :options="difficultyOptions" />
                </label>

                <label class="profile-field">
                  <span>Relevância</span>
                  <Select v-model="form.relevanceByCommunity" :options="relevanceOptions" />
                </label>
              </div>

              <fieldset class="space-y-2">
                <legend class="text-sm font-bold text-[var(--liaprove-ink)]">Áreas de conhecimento</legend>
                <div class="flex flex-wrap gap-3">
                  <label v-for="area in knowledgeAreaOptions" :key="area" class="question-check-option">
                    <input
                      :data-test="`area-${area}`"
                      type="checkbox"
                      :checked="form.knowledgeAreas.includes(area)"
                      @change="toggleKnowledgeArea(area, ($event.target as HTMLInputElement).checked)"
                    />
                    <span>{{ area }}</span>
                  </label>
                </div>
              </fieldset>

              <fieldset v-if="form.type === 'MULTIPLE_CHOICE'" class="space-y-3">
                <legend class="text-sm font-bold text-[var(--liaprove-ink)]">Alternativas</legend>
                <div v-for="(alternative, index) in form.alternatives" :key="index" class="grid gap-2 sm:grid-cols-[1fr_auto]">
                  <InputText
                    :data-test="`alternative-${index}`"
                    v-model="alternative.text"
                    :placeholder="`Alternativa ${index + 1}`"
                  />
                  <label class="question-check-option justify-center">
                    <input
                      :checked="alternative.correct"
                      name="correct-alternative"
                      type="radio"
                      @change="setCorrectAlternative(index)"
                    />
                    <span>Correta</span>
                  </label>
                </div>
              </fieldset>

              <div class="flex flex-wrap gap-3">
                <Button
                  data-test="pre-analyze-question"
                  type="button"
                  label="Executar pré-análise"
                  icon="pi pi-sparkles"
                  severity="secondary"
                  outlined
                  :loading="analyzing"
                  @click="runPreAnalysis"
                />
                <Button
                  data-test="submit-question"
                  type="button"
                  label="Enviar questão"
                  icon="pi pi-send"
                  :loading="submitting"
                  @click="sendQuestion"
                />
              </div>
            </form>
          </template>
        </Card>

        <Card class="question-card">
          <template #title>Pré-análise</template>
          <template #content>
            <div v-if="!preAnalysis" class="space-y-3 text-[var(--liaprove-muted)]">
              <p>Execute a pré-análise para revisar clareza, ambiguidades, distratores e coerência temática.</p>
            </div>

            <div v-else class="space-y-5">
              <section v-if="preAnalysis.languageSuggestions.length" class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Linguagem</h2>
                <label
                  v-for="(suggestion, index) in preAnalysis.languageSuggestions"
                  :key="suggestion"
                  class="question-check-option"
                >
                  <input
                    :data-test="`accept-language-${index}`"
                    v-model="selectedLanguageSuggestions"
                    type="checkbox"
                    :value="suggestion"
                  />
                  <span>{{ suggestion }}</span>
                </label>
              </section>

              <section v-if="preAnalysis.biasOrAmbiguityWarnings.length" class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Ambiguidade</h2>
                <label
                  v-for="warning in preAnalysis.biasOrAmbiguityWarnings"
                  :key="warning"
                  class="question-check-option"
                >
                  <input v-model="selectedBiasWarnings" type="checkbox" :value="warning" />
                  <span>{{ warning }}</span>
                </label>
              </section>

              <section v-if="preAnalysis.distractorSuggestions.length" class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Distratores</h2>
                <label
                  v-for="suggestion in preAnalysis.distractorSuggestions"
                  :key="suggestion"
                  class="question-check-option"
                >
                  <input v-model="selectedDistractorSuggestions" type="checkbox" :value="suggestion" />
                  <span>{{ suggestion }}</span>
                </label>
              </section>

              <section v-if="preAnalysis.difficultyLevelByLLM" class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Dificuldade sugerida</h2>
                <label class="question-check-option">
                  <input v-model="acceptDifficultySuggestion" type="checkbox" />
                  <span>{{ preAnalysis.difficultyLevelByLLM }}</span>
                </label>
              </section>

              <section v-if="preAnalysis.topicConsistencyNotes.length" class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Coerência</h2>
                <label v-for="note in preAnalysis.topicConsistencyNotes" :key="note" class="question-check-option">
                  <input v-model="selectedTopicNotes" type="checkbox" :value="note" />
                  <span>{{ note }}</span>
                </label>
              </section>
            </div>
          </template>
        </Card>
      </div>
    </div>
  </AuthenticatedLayout>
</template>
