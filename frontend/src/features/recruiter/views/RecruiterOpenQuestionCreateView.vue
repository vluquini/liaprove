<script setup lang="ts">
import { reactive, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  createOpenQuestion,
  type DifficultyLevel,
  type KnowledgeArea,
  type OpenQuestionVisibility,
  type RelevanceLevel,
} from '../services/recruiterAssessmentService'

const knowledgeAreaOptions: KnowledgeArea[] = ['SOFTWARE_DEVELOPMENT', 'DATABASE', 'CYBERSECURITY', 'NETWORKS', 'AI']
const difficultyOptions: DifficultyLevel[] = ['EASY', 'MEDIUM', 'HARD']
const relevanceOptions: RelevanceLevel[] = ['ONE', 'TWO', 'THREE', 'FOUR', 'FIVE']
const visibilityOptions: OpenQuestionVisibility[] = ['PRIVATE', 'SHARED']

const form = reactive({
  title: '',
  description: '',
  knowledgeAreas: [] as KnowledgeArea[],
  difficultyByCommunity: 'MEDIUM' as DifficultyLevel,
  relevanceByCommunity: 'THREE' as RelevanceLevel,
  guideline: '',
  visibility: 'PRIVATE' as OpenQuestionVisibility,
})

const submitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

function toggleKnowledgeArea(area: KnowledgeArea, checked: boolean): void {
  if (checked && !form.knowledgeAreas.includes(area)) {
    form.knowledgeAreas.push(area)
    return
  }

  if (!checked) {
    form.knowledgeAreas = form.knowledgeAreas.filter((candidate) => candidate !== area)
  }
}

function validateForm(): string | null {
  if (
    form.title.trim().length < 10
    || form.description.trim().length < 20
    || form.knowledgeAreas.length === 0
    || !form.difficultyByCommunity
    || !form.relevanceByCommunity
    || form.guideline.trim().length < 10
  ) {
    return 'Informe título, descrição, área, dificuldade, relevância e critério de avaliação.'
  }

  return null
}

async function submitOpenQuestion(): Promise<void> {
  const validationMessage = validateForm()
  if (validationMessage) {
    errorMessage.value = validationMessage
    successMessage.value = ''
    return
  }

  submitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const response = await createOpenQuestion({
      title: form.title.trim(),
      description: form.description.trim(),
      knowledgeAreas: [...form.knowledgeAreas],
      difficultyByCommunity: form.difficultyByCommunity,
      relevanceByCommunity: form.relevanceByCommunity,
      guideline: form.guideline.trim(),
      visibility: form.visibility,
    })
    successMessage.value = `Questão criada: ${response.title}`
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
            Questão aberta
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Criar questão aberta</h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Crie uma questão discursiva para avaliações personalizadas, com critério de correção explícito.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <p v-if="successMessage" class="status-message status-message-success">{{ successMessage }}</p>

      <Card class="question-form-card">
        <template #content>
          <form class="space-y-5" @submit.prevent="submitOpenQuestion">
            <div class="grid gap-4 lg:grid-cols-2">
              <label class="profile-field lg:col-span-2">
                <span>Título</span>
                <InputText data-test="open-title" v-model="form.title" />
              </label>

              <label class="profile-field lg:col-span-2">
                <span>Descrição</span>
                <Textarea data-test="open-description" v-model="form.description" rows="5" />
              </label>

              <label class="profile-field lg:col-span-2">
                <span>Critério de avaliação</span>
                <Textarea data-test="open-guideline" v-model="form.guideline" rows="4" />
              </label>

              <label class="profile-field">
                <span>Dificuldade</span>
                <select data-test="open-difficulty" v-model="form.difficultyByCommunity" class="p-inputtext p-component">
                  <option v-for="difficulty in difficultyOptions" :key="difficulty" :value="difficulty">
                    {{ difficulty }}
                  </option>
                </select>
              </label>

              <label class="profile-field">
                <span>Relevância</span>
                <select data-test="open-relevance" v-model="form.relevanceByCommunity" class="p-inputtext p-component">
                  <option v-for="relevance in relevanceOptions" :key="relevance" :value="relevance">
                    {{ relevance }}
                  </option>
                </select>
              </label>

              <label class="profile-field">
                <span>Visibilidade</span>
                <select data-test="open-visibility" v-model="form.visibility" class="p-inputtext p-component">
                  <option v-for="visibility in visibilityOptions" :key="visibility" :value="visibility">
                    {{ visibility }}
                  </option>
                </select>
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

            <Button
              data-test="create-open-question"
              type="button"
              label="Criar questão aberta"
              icon="pi pi-send"
              :loading="submitting"
              @click="submitOpenQuestion"
            />
          </form>
        </template>
      </Card>
    </div>
  </AuthenticatedLayout>
</template>
