<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Tag from 'primevue/tag'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  createPersonalizedAssessment,
  listSuggestedQuestions,
  type CreatePersonalizedAssessmentRequest,
  type DifficultyLevel,
  type JobDescriptionAnalysisResponse,
  type JobDescriptionAnalysisSnapshotRequest,
  type KnowledgeArea,
  type QuestionType,
  type ScoredQuestionResponse,
} from '../services/recruiterAssessmentService'

const LAST_JOB_ANALYSIS_KEY = 'liaprove:recruiter:last-job-analysis'
const MAX_SELECTED_QUESTIONS = 10

const knowledgeAreaOptions: KnowledgeArea[] = ['SOFTWARE_DEVELOPMENT', 'DATABASE', 'CYBERSECURITY', 'NETWORKS', 'AI']
const difficultyOptions: DifficultyLevel[] = ['EASY', 'MEDIUM', 'HARD']
const questionTypeOptions: QuestionType[] = ['MULTIPLE_CHOICE', 'PROJECT', 'OPEN']

const form = reactive({
  title: '',
  description: '',
  expirationDate: '',
  maxAttempts: 1,
  evaluationTimerMinutes: 30,
  hardSkillsWeight: 60,
  softSkillsWeight: 20,
  experienceWeight: 20,
})

const selectedKnowledgeAreas = ref<KnowledgeArea[]>([])
const selectedDifficulties = ref<DifficultyLevel[]>([])
const selectedQuestionTypes = ref<QuestionType[]>([])
const suggestions = ref<ScoredQuestionResponse[]>([])
const selectedQuestions = ref<ScoredQuestionResponse[]>([])
const jobAnalysis = ref<JobDescriptionAnalysisResponse | null>(null)
const loadingSuggestions = ref(false)
const creating = ref(false)
const errorMessage = ref('')
const successShareUrl = ref('')

const selectedQuestionIds = computed(() => selectedQuestions.value.map((question) => question.id))

const selectedQuestionsLabel = computed(() => {
  const count = selectedQuestionIds.value.length
  const label = count === 1 ? '1 questão selecionada' : `${count} questões selecionadas`
  return `${label} de ${MAX_SELECTED_QUESTIONS}`
})

const hasReachedQuestionLimit = computed(() => selectedQuestionIds.value.length >= MAX_SELECTED_QUESTIONS)

onMounted(() => {
  loadSavedJobAnalysis()
})

function loadSavedJobAnalysis(): void {
  const raw = sessionStorage.getItem(LAST_JOB_ANALYSIS_KEY)
  if (!raw) {
    return
  }

  try {
    const parsed = JSON.parse(raw) as JobDescriptionAnalysisResponse
    jobAnalysis.value = parsed
    selectedKnowledgeAreas.value = [...parsed.suggestedKnowledgeAreas]
    form.hardSkillsWeight = parsed.suggestedCriteriaWeights.hardSkillsWeight
    form.softSkillsWeight = parsed.suggestedCriteriaWeights.softSkillsWeight
    form.experienceWeight = parsed.suggestedCriteriaWeights.experienceWeight
  } catch {
    sessionStorage.removeItem(LAST_JOB_ANALYSIS_KEY)
  }
}

function toggleSelection<T>(target: T[], value: T, checked: boolean): T[] {
  if (checked && !target.includes(value)) {
    return [...target, value]
  }

  if (!checked) {
    return target.filter((candidate) => candidate !== value)
  }

  return target
}

function toggleKnowledgeArea(area: KnowledgeArea, checked: boolean): void {
  selectedKnowledgeAreas.value = toggleSelection(selectedKnowledgeAreas.value, area, checked)
}

function toggleDifficulty(difficulty: DifficultyLevel, checked: boolean): void {
  selectedDifficulties.value = toggleSelection(selectedDifficulties.value, difficulty, checked)
}

function toggleQuestionType(type: QuestionType, checked: boolean): void {
  selectedQuestionTypes.value = toggleSelection(selectedQuestionTypes.value, type, checked)
}

function toggleQuestion(question: ScoredQuestionResponse, checked: boolean): void {
  if (checked) {
    if (hasReachedQuestionLimit.value || selectedQuestionIds.value.includes(question.id)) {
      return
    }

    selectedQuestions.value = [...selectedQuestions.value, question]
    suggestions.value = suggestions.value.filter((candidate) => candidate.id !== question.id)
    return
  }

  selectedQuestions.value = selectedQuestions.value.filter((candidate) => candidate.id !== question.id)
}

async function loadSuggestions(): Promise<void> {
  loadingSuggestions.value = true
  errorMessage.value = ''

  try {
    const response = await listSuggestedQuestions({
      knowledgeAreas: selectedKnowledgeAreas.value,
      difficultyLevels: selectedDifficulties.value,
      questionTypes: selectedQuestionTypes.value,
      excludeIds: selectedQuestionIds.value,
      page: 1,
      pageSize: 10,
    })
    suggestions.value = response.questions.filter((question) => !selectedQuestionIds.value.includes(question.id))
  } catch (error) {
    suggestions.value = []
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loadingSuggestions.value = false
  }
}

async function submitAssessment(): Promise<void> {
  const validationMessage = validateForm()
  if (validationMessage) {
    errorMessage.value = validationMessage
    return
  }

  creating.value = true
  errorMessage.value = ''
  successShareUrl.value = ''

  try {
    const response = await createPersonalizedAssessment(buildRequest())
    successShareUrl.value = `${window.location.origin}/assessments/personalized/${response.shareableToken}/start`
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    creating.value = false
  }
}

function validateForm(): string | null {
  if (
    !form.title.trim()
    || !form.description.trim()
    || !form.expirationDate
    || new Date(form.expirationDate).getTime() <= Date.now()
    || Number(form.maxAttempts) < 1
    || Number(form.evaluationTimerMinutes) < 5
    || selectedQuestionIds.value.length === 0
  ) {
    return 'Informe título, descrição, expiração futura, tentativas, tempo e ao menos uma questão.'
  }

  if (selectedQuestionIds.value.length > MAX_SELECTED_QUESTIONS) {
    return `Selecione no máximo ${MAX_SELECTED_QUESTIONS} questões.`
  }

  if (form.hardSkillsWeight + form.softSkillsWeight + form.experienceWeight !== 100) {
    return 'Os pesos devem somar 100.'
  }

  return null
}

function buildRequest(): CreatePersonalizedAssessmentRequest {
  return {
    title: form.title.trim(),
    description: form.description.trim(),
    questionIds: selectedQuestionIds.value,
    expirationDate: form.expirationDate,
    maxAttempts: Number(form.maxAttempts),
    evaluationTimerMinutes: Number(form.evaluationTimerMinutes),
    hardSkillsWeight: Number(form.hardSkillsWeight),
    softSkillsWeight: Number(form.softSkillsWeight),
    experienceWeight: Number(form.experienceWeight),
    jobDescriptionAnalysis: toJobDescriptionSnapshot(jobAnalysis.value),
  }
}

function toJobDescriptionSnapshot(
  analysis: JobDescriptionAnalysisResponse | null,
): JobDescriptionAnalysisSnapshotRequest | null {
  if (!analysis) {
    return null
  }

  return {
    originalJobDescription: analysis.originalJobDescription,
    suggestedKnowledgeAreas: analysis.suggestedKnowledgeAreas,
    suggestedHardSkills: analysis.suggestedHardSkills,
    suggestedSoftSkills: analysis.suggestedSoftSkills,
    suggestedHardSkillsWeight: analysis.suggestedCriteriaWeights.hardSkillsWeight,
    suggestedSoftSkillsWeight: analysis.suggestedCriteriaWeights.softSkillsWeight,
    suggestedExperienceWeight: analysis.suggestedCriteriaWeights.experienceWeight,
  }
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Avaliação personalizada
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Criar avaliação</h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Defina os dados da avaliação, selecione questões sugeridas e gere um link para candidatos.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card v-if="successShareUrl" class="question-card">
        <template #title>Avaliação criada</template>
        <template #content>
          <div class="space-y-3">
            <p class="text-[var(--liaprove-muted)]">Compartilhe este link com o candidato.</p>
            <p class="break-all rounded-md border border-[var(--liaprove-line)] bg-white p-3 font-medium">
              {{ successShareUrl }}
            </p>
          </div>
        </template>
      </Card>

      <div class="grid gap-4 xl:grid-cols-[minmax(0,1fr)_24rem]">
        <div class="space-y-4">
          <Card class="question-form-card">
            <template #title>Dados básicos</template>
            <template #content>
              <div class="grid gap-4 lg:grid-cols-2">
                <label class="profile-field lg:col-span-2">
                  <span>Título</span>
                  <InputText data-test="assessment-title" v-model="form.title" />
                </label>

                <label class="profile-field lg:col-span-2">
                  <span>Descrição</span>
                  <Textarea data-test="assessment-description" v-model="form.description" rows="4" />
                </label>

                <label class="profile-field">
                  <span>Expiração</span>
                  <input
                    data-test="assessment-expiration"
                    v-model="form.expirationDate"
                    class="p-inputtext p-component"
                    type="datetime-local"
                  />
                </label>

                <label class="profile-field">
                  <span>Tentativas máximas</span>
                  <input
                    data-test="assessment-max-attempts"
                    v-model.number="form.maxAttempts"
                    class="p-inputtext p-component"
                    min="1"
                    type="number"
                  />
                </label>

                <label class="profile-field">
                  <span>Tempo em minutos</span>
                  <input
                    data-test="assessment-timer"
                    v-model.number="form.evaluationTimerMinutes"
                    class="p-inputtext p-component"
                    min="5"
                    type="number"
                  />
                </label>
              </div>
            </template>
          </Card>

          <Card class="question-form-card">
            <template #title>Filtros de sugestões</template>
            <template #content>
              <div class="space-y-4">
                <fieldset class="space-y-2">
                  <legend class="text-sm font-bold text-[var(--liaprove-ink)]">Áreas</legend>
                  <div class="flex flex-wrap gap-3">
                    <label v-for="area in knowledgeAreaOptions" :key="area" class="question-check-option">
                      <input
                        :data-test="`knowledge-area-${area}`"
                        type="checkbox"
                        :checked="selectedKnowledgeAreas.includes(area)"
                        @change="toggleKnowledgeArea(area, ($event.target as HTMLInputElement).checked)"
                      />
                      <span>{{ area }}</span>
                    </label>
                  </div>
                </fieldset>

                <fieldset class="space-y-2">
                  <legend class="text-sm font-bold text-[var(--liaprove-ink)]">Dificuldades</legend>
                  <div class="flex flex-wrap gap-3">
                    <label v-for="difficulty in difficultyOptions" :key="difficulty" class="question-check-option">
                      <input
                        :data-test="`difficulty-${difficulty}`"
                        type="checkbox"
                        :checked="selectedDifficulties.includes(difficulty)"
                        @change="toggleDifficulty(difficulty, ($event.target as HTMLInputElement).checked)"
                      />
                      <span>{{ difficulty }}</span>
                    </label>
                  </div>
                </fieldset>

                <fieldset class="space-y-2">
                  <legend class="text-sm font-bold text-[var(--liaprove-ink)]">Tipos</legend>
                  <div class="flex flex-wrap gap-3">
                    <label v-for="type in questionTypeOptions" :key="type" class="question-check-option">
                      <input
                        :data-test="`question-type-${type}`"
                        type="checkbox"
                        :checked="selectedQuestionTypes.includes(type)"
                        @change="toggleQuestionType(type, ($event.target as HTMLInputElement).checked)"
                      />
                      <span>{{ type }}</span>
                    </label>
                  </div>
                </fieldset>

                <Button
                  data-test="load-suggestions"
                  type="button"
                  label="Buscar questões"
                  icon="pi pi-search"
                  :loading="loadingSuggestions"
                  @click="loadSuggestions"
                />
              </div>
            </template>
          </Card>

          <Card class="question-card">
            <template #title>Questões sugeridas</template>
            <template #content>
              <div class="space-y-3">
                <p class="text-sm font-semibold text-[var(--liaprove-muted)]">{{ selectedQuestionsLabel }}</p>
                <section v-if="selectedQuestions.length > 0" class="space-y-3">
                  <h2 class="text-sm font-bold text-[var(--liaprove-ink)]">Questões selecionadas</h2>
                  <article
                    v-for="question in selectedQuestions"
                    :key="question.id"
                    class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3"
                  >
                    <label class="flex items-start gap-3">
                      <input
                        :data-test="`selected-question-${question.id}`"
                        type="checkbox"
                        checked
                        @change="toggleQuestion(question, ($event.target as HTMLInputElement).checked)"
                      />
                      <span class="min-w-0">
                        <span class="block font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</span>
                        <span class="mt-2 flex flex-wrap gap-2">
                          <Tag
                            v-for="area in question.knowledgeAreas"
                            :key="area"
                            :value="area"
                            severity="info"
                          />
                          <Tag :value="`Score ${Math.round(question.score * 100)}%`" severity="success" />
                        </span>
                      </span>
                    </label>
                  </article>
                </section>

                <section v-if="suggestions.length > 0" class="space-y-3">
                  <h2 class="text-sm font-bold text-[var(--liaprove-ink)]">Novas sugestões</h2>
                  <article
                    v-for="question in suggestions"
                    :key="question.id"
                    class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3"
                  >
                    <label class="flex items-start gap-3">
                      <input
                        :data-test="`select-question-${question.id}`"
                        type="checkbox"
                        :disabled="hasReachedQuestionLimit"
                        @change="toggleQuestion(question, ($event.target as HTMLInputElement).checked)"
                      />
                      <span class="min-w-0">
                        <span class="block font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</span>
                        <span class="mt-2 flex flex-wrap gap-2">
                          <Tag
                            v-for="area in question.knowledgeAreas"
                            :key="area"
                            :value="area"
                            severity="info"
                          />
                          <Tag :value="`Score ${Math.round(question.score * 100)}%`" severity="success" />
                        </span>
                      </span>
                    </label>
                  </article>
                </section>

                <p v-if="selectedQuestions.length === 0 && suggestions.length === 0" class="text-[var(--liaprove-muted)]">
                  Busque sugestões para selecionar questões.
                </p>
              </div>
            </template>
          </Card>
        </div>

        <aside class="space-y-4">
          <Card v-if="jobAnalysis" class="question-card">
            <template #title>Análise da vaga</template>
            <template #content>
              <div class="space-y-4">
                <p class="text-sm text-[var(--liaprove-muted)]">{{ jobAnalysis.originalJobDescription }}</p>
                <section class="space-y-2">
                  <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Hard skills</h2>
                  <div class="flex flex-wrap gap-2">
                    <Tag v-for="skill in jobAnalysis.suggestedHardSkills" :key="skill" :value="skill" severity="success" />
                  </div>
                </section>
                <section class="space-y-2">
                  <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Soft skills</h2>
                  <div class="flex flex-wrap gap-2">
                    <Tag v-for="skill in jobAnalysis.suggestedSoftSkills" :key="skill" :value="skill" severity="secondary" />
                  </div>
                </section>
                <p class="text-sm text-[var(--liaprove-muted)]">
                  Pesos: {{ jobAnalysis.suggestedCriteriaWeights.hardSkillsWeight }}% /
                  {{ jobAnalysis.suggestedCriteriaWeights.softSkillsWeight }}% /
                  {{ jobAnalysis.suggestedCriteriaWeights.experienceWeight }}%
                </p>
              </div>
            </template>
          </Card>

          <Card class="question-form-card">
            <template #title>Pesos</template>
            <template #content>
              <div class="space-y-3">
                <label class="profile-field">
                  <span>Hard skills</span>
                  <input data-test="weight-hard" v-model.number="form.hardSkillsWeight" class="p-inputtext p-component" type="number" />
                </label>
                <label class="profile-field">
                  <span>Soft skills</span>
                  <input data-test="weight-soft" v-model.number="form.softSkillsWeight" class="p-inputtext p-component" type="number" />
                </label>
                <label class="profile-field">
                  <span>Experiência</span>
                  <input data-test="weight-experience" v-model.number="form.experienceWeight" class="p-inputtext p-component" type="number" />
                </label>
              </div>
            </template>
          </Card>

          <Button
            data-test="create-assessment"
            class="w-full"
            type="button"
            label="Criar avaliação"
            icon="pi pi-check"
            :loading="creating"
            @click="submitAssessment"
          />
        </aside>
      </div>
    </div>
  </AuthenticatedLayout>
</template>
