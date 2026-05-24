<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  listAdminQuestions,
  type AdminQuestionResponse,
  type DifficultyLevel,
  type KnowledgeArea,
  type ListAdminQuestionsParams,
  type QuestionStatus,
} from '../services/adminService'

const page = ref(0)
const size = 10
const areaFilter = ref<KnowledgeArea | ''>('')
const difficultyFilter = ref<DifficultyLevel | ''>('')
const statusFilter = ref<QuestionStatus | ''>('')
const authorFilter = ref('')
const questions = ref<AdminQuestionResponse[]>([])
const loading = ref(true)
const errorMessage = ref('')

const canGoPrevious = computed(() => page.value > 0)
const canGoNext = computed(() => questions.value.length === size)

function formatDate(value: string | null | undefined): string {
  if (!value) {
    return 'Data não informada'
  }

  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(value))
}

function currentParams(): ListAdminQuestionsParams {
  return {
    knowledgeAreas: areaFilter.value ? [areaFilter.value] : undefined,
    difficultyLevel: difficultyFilter.value || undefined,
    status: statusFilter.value || undefined,
    authorId: authorFilter.value.trim() || undefined,
    page: page.value,
    size,
  }
}

async function loadQuestions(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    questions.value = await listAdminQuestions(currentParams())
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    questions.value = []
  } finally {
    loading.value = false
  }
}

async function applyFilters(): Promise<void> {
  page.value = 0
  await loadQuestions()
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
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Administração
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Questões</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Revise, filtre e abra questões para moderação e auditoria de votos e feedbacks.
          </p>
        </div>
      </section>

      <Card class="profile-summary-card">
        <template #content>
          <form class="grid gap-4 lg:grid-cols-5" @submit.prevent="applyFilters">
            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Área
              <select v-model="areaFilter" data-test="admin-question-filter-area" class="auth-input">
                <option value="">Todas</option>
                <option value="SOFTWARE_DEVELOPMENT">Desenvolvimento</option>
                <option value="DATABASE">Banco de dados</option>
                <option value="CYBERSECURITY">Cibersegurança</option>
                <option value="NETWORKS">Redes</option>
                <option value="AI">IA</option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Dificuldade
              <select v-model="difficultyFilter" data-test="admin-question-filter-difficulty" class="auth-input">
                <option value="">Todas</option>
                <option value="EASY">Fácil</option>
                <option value="MEDIUM">Média</option>
                <option value="HARD">Difícil</option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Status
              <select v-model="statusFilter" data-test="admin-question-filter-status" class="auth-input">
                <option value="">Todos</option>
                <option value="VOTING">VOTING</option>
                <option value="APPROVED">APPROVED</option>
                <option value="FINISHED">FINISHED</option>
                <option value="REJECTED">REJECTED</option>
              </select>
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Autor
              <input
                v-model="authorFilter"
                data-test="admin-question-filter-author"
                class="auth-input"
                type="search"
                placeholder="UUID do autor"
              />
            </label>

            <div class="flex items-end">
              <Button
                data-test="admin-question-apply-filters"
                type="submit"
                label="Filtrar"
                icon="pi pi-search"
                :disabled="loading"
                @click="applyFilters"
              />
            </div>
          </form>
        </template>
      </Card>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <div v-if="loading" class="status-message status-message-success">Carregando questões...</div>

      <Card v-else-if="questions.length === 0 && !errorMessage" class="profile-summary-card">
        <template #content>
          <div class="flex flex-col gap-2">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Nenhuma questão encontrada.</h2>
            <p class="text-[var(--liaprove-muted)]">Ajuste os filtros para ampliar a busca.</p>
          </div>
        </template>
      </Card>

      <section v-else class="grid gap-4 md:grid-cols-2">
        <Card v-for="question in questions" :key="question.id" class="action-card">
          <template #content>
            <div class="flex h-full flex-col gap-4">
              <div>
                <div class="flex flex-wrap items-center gap-2">
                  <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</h2>
                  <span class="question-meta-tag">{{ question.type }}</span>
                  <span class="question-meta-tag">{{ question.status }}</span>
                </div>
                <p class="mt-2 text-sm text-[var(--liaprove-muted)]">
                  Submetida em {{ formatDate(question.submissionDate) }}
                </p>
              </div>

              <p class="line-clamp-2 text-sm text-[var(--liaprove-muted)]">{{ question.description }}</p>

              <div class="flex flex-wrap gap-2">
                <span v-for="area in question.knowledgeAreas" :key="area" class="question-meta-tag">
                  {{ area }}
                </span>
              </div>

              <RouterLink
                :data-test="`open-admin-question-${question.id}`"
                :to="`/admin/questions/${question.id}`"
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
