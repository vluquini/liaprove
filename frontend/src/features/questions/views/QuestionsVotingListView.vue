<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import { listVotingQuestions, type QuestionSummaryResponse } from '../services/questionService'

const page = ref(0)
const size = 10
const loading = ref(true)
const errorMessage = ref('')
const questions = ref<QuestionSummaryResponse[]>([])

const canGoPrevious = computed(() => page.value > 0)
const canGoNext = computed(() => questions.value.length === size)

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(value))
}

async function loadQuestions(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    questions.value = await listVotingQuestions({ page: page.value, size })
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    questions.value = []
  } finally {
    loading.value = false
  }
}

async function previousPage(): Promise<void> {
  if (!canGoPrevious.value) {
    return
  }

  page.value -= 1
  await loadQuestions()
}

async function nextPage(): Promise<void> {
  if (!canGoNext.value) {
    return
  }

  page.value += 1
  await loadQuestions()
}

onMounted(loadQuestions)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
              Curadoria
            </p>
            <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Questões em votação</h1>
            <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
              Revise questões submetidas pela comunidade e ajude a decidir o que entra no banco de avaliações.
            </p>
          </div>

          <RouterLink to="/questions/new">
            <Button label="Submeter questão" icon="pi pi-plus-circle" />
          </RouterLink>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <div v-if="loading" class="status-message status-message-success">Carregando questões para curadoria...</div>

      <Card v-else-if="questions.length === 0 && !errorMessage" class="profile-summary-card">
        <template #content>
          <div class="flex flex-col gap-3">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Nenhuma questão em votação no momento.</h2>
            <p class="text-[var(--liaprove-muted)]">
              Quando houver novas submissões disponíveis, elas aparecerão aqui para revisão comunitária.
            </p>
          </div>
        </template>
      </Card>

      <section v-else class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        <Card v-for="question in questions" :key="question.id" class="action-card question-card">
          <template #content>
            <div class="flex h-full flex-col gap-4">
              <div class="flex items-center gap-3">
                <span class="action-icon">
                  <i class="pi pi-check-square" aria-hidden="true" />
                </span>
                <div>
                  <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</h2>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                    Submetida em {{ formatDate(question.submissionDate) }}
                  </p>
                </div>
              </div>

              <div class="flex flex-wrap gap-2">
                <span v-for="area in question.knowledgeAreas" :key="area" class="question-meta-tag">
                  {{ area }}
                </span>
              </div>

              <RouterLink
                :data-test="`open-question-${question.id}`"
                :to="`/questions/${question.id}/voting`"
                class="mt-auto"
              >
                <Button label="Ver detalhes" icon="pi pi-arrow-right" icon-pos="right" size="small" />
              </RouterLink>
            </div>
          </template>
        </Card>
      </section>

      <div class="flex items-center justify-between gap-3">
        <Button
          label="Anterior"
          icon="pi pi-arrow-left"
          severity="secondary"
          outlined
          :disabled="loading || !canGoPrevious"
          @click="previousPage"
        />
        <span class="text-sm font-semibold text-[var(--liaprove-muted)]">Página {{ page + 1 }}</span>
        <Button
          label="Próxima"
          icon="pi pi-arrow-right"
          icon-pos="right"
          severity="secondary"
          outlined
          :disabled="loading || !canGoNext"
          @click="nextPage"
        />
      </div>
    </div>
  </AuthenticatedLayout>
</template>
