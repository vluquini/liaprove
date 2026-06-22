<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  getAdminQuestion,
  listQuestionFeedbacks,
  listQuestionVotes,
  moderateAdminQuestion,
  updateAdminQuestion,
  type AdminQuestionResponse,
  type AlternativeResponse,
  type FeedbackQuestionResponse,
  type KnowledgeArea,
  type QuestionStatus,
  type VoteResponse,
} from '../services/adminService'

const route = useRoute()
const question = ref<AdminQuestionResponse | null>(null)
const votes = ref<VoteResponse[]>([])
const feedbacks = ref<FeedbackQuestionResponse[]>([])
const loading = ref(true)
const saving = ref(false)
const errorMessage = ref('')
const successMessages = ref<string[]>([])
const title = ref('')
const description = ref('')
const area = ref<KnowledgeArea | ''>('')
const status = ref<QuestionStatus>('VOTING')

const questionId = computed(() => String(route.params.questionId))

async function loadQuestion(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    const [questionData, voteData, feedbackData] = await Promise.all([
      getAdminQuestion(questionId.value),
      listQuestionVotes(questionId.value),
      listQuestionFeedbacks(questionId.value),
    ])

    question.value = questionData
    votes.value = voteData
    feedbacks.value = feedbackData
    title.value = questionData.title
    description.value = questionData.description
    area.value = questionData.knowledgeAreas[0] ?? ''
    status.value = questionData.status
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    question.value = null
    votes.value = []
    feedbacks.value = []
  } finally {
    loading.value = false
  }
}

async function saveQuestion(): Promise<void> {
  if (!question.value) {
    return
  }

  saving.value = true
  errorMessage.value = ''

  try {
    const updated = await updateAdminQuestion(questionId.value, {
      title: title.value,
      description: description.value,
      knowledgeAreas: area.value ? [area.value] : [],
      alternatives: question.value.alternatives ?? [],
    })
    question.value = updated
    successMessages.value = [...successMessages.value, 'Questão atualizada com sucesso.']
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    saving.value = false
  }
}

async function moderateQuestion(): Promise<void> {
  saving.value = true
  errorMessage.value = ''

  try {
    const moderated = await moderateAdminQuestion(questionId.value, status.value)
    question.value = moderated
    successMessages.value = [...successMessages.value, 'Questão moderada com sucesso.']
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    saving.value = false
  }
}

function formatDate(value: string | null | undefined): string {
  if (!value) {
    return 'Data não informada'
  }

  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

function alternatives(): AlternativeResponse[] {
  return question.value?.alternatives ?? []
}

onMounted(loadQuestion)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
              Auditoria da questão
            </p>
            <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">
              {{ question?.title || 'Questão administrativa' }}
            </h1>
            <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
              Revise dados da questão, modere status e audite votos e feedbacks recebidos.
            </p>
          </div>

          <RouterLink to="/admin/questions">
            <Button label="Voltar" icon="pi pi-arrow-left" severity="secondary" outlined />
          </RouterLink>
        </div>
      </section>

      <p v-for="message in successMessages" :key="message" class="status-message status-message-success">
        {{ message }}
      </p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <div v-if="loading" class="status-message status-message-success">Carregando auditoria da questão...</div>

      <template v-if="question && !loading">
        <section class="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <Card class="profile-summary-card">
            <template #title>Dados da questão</template>
            <template #content>
              <div class="space-y-4">
                <div class="flex flex-wrap gap-2">
                  <span class="question-meta-tag">{{ question.type }}</span>
                  <span class="question-meta-tag">{{ question.status }}</span>
                  <span v-if="question.difficultyByCommunity" class="question-meta-tag">
                    {{ question.difficultyByCommunity }}
                  </span>
                  <span v-if="question.relevanceByCommunity" class="question-meta-tag">
                    Relevância {{ question.relevanceByCommunity }}
                  </span>
                </div>

                <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                  Título
                  <input v-model="title" data-test="admin-question-title" class="auth-input" />
                </label>

                <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                  Descrição
                  <textarea
                    v-model="description"
                    data-test="admin-question-description"
                    class="auth-input min-h-32"
                  />
                </label>

                <p class="text-sm text-[var(--liaprove-muted)]">{{ description }}</p>

                <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                  Área principal
                  <select v-model="area" data-test="admin-question-area" class="auth-input">
                    <option value="SOFTWARE_DEVELOPMENT">Desenvolvimento</option>
                    <option value="DATABASE">Banco de dados</option>
                    <option value="CYBERSECURITY">Cibersegurança</option>
                    <option value="NETWORKS">Redes</option>
                    <option value="AI">IA</option>
                  </select>
                </label>

                <div v-if="alternatives().length > 0" class="space-y-2">
                  <h3 class="font-semibold text-[var(--liaprove-ink)]">Alternativas</h3>
                  <ul class="space-y-2 text-sm text-[var(--liaprove-muted)]">
                    <li v-for="alternative in alternatives()" :key="alternative.id" class="rounded border border-[var(--liaprove-line)] p-3">
                      {{ alternative.text }}
                    </li>
                  </ul>
                </div>

                <Button
                  data-test="save-admin-question"
                  label="Salvar alterações"
                  icon="pi pi-save"
                  :disabled="saving"
                  @click="saveQuestion"
                />
              </div>
            </template>
          </Card>

          <Card class="profile-summary-card">
            <template #title>Moderação</template>
            <template #content>
              <div class="space-y-4">
                <dl class="grid gap-3 text-sm">
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Autor</dt>
                    <dd class="text-[var(--liaprove-ink)]">{{ question.authorId }}</dd>
                  </div>
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Submissão</dt>
                    <dd class="text-[var(--liaprove-ink)]">{{ formatDate(question.submissionDate) }}</dd>
                  </div>
                </dl>

                <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                  Novo status
                  <select v-model="status" data-test="admin-question-status" class="auth-input">
                    <option value="VOTING">VOTING</option>
                    <option value="APPROVED">APPROVED</option>
                    <option value="FINISHED">FINISHED</option>
                    <option value="REJECTED">REJECTED</option>
                  </select>
                </label>

                <Button
                  data-test="moderate-admin-question"
                  label="Aplicar moderação"
                  icon="pi pi-verified"
                  severity="warn"
                  :disabled="saving"
                  @click="moderateQuestion"
                />
              </div>
            </template>
          </Card>
        </section>

        <section class="grid gap-4 lg:grid-cols-2">
          <Card class="profile-summary-card">
            <template #title>Votos</template>
            <template #content>
              <p v-if="votes.length === 0" class="text-[var(--liaprove-muted)]">Nenhum voto registrado.</p>
              <div v-else class="space-y-3">
                <div v-for="questionVote in votes" :key="questionVote.id" class="rounded border border-[var(--liaprove-line)] p-3">
                  <div class="flex items-center justify-between gap-3">
                    <div>
                      <p class="font-semibold text-[var(--liaprove-ink)]">{{ questionVote.user.name }}</p>
                      <p class="text-sm text-[var(--liaprove-muted)]">{{ questionVote.user.email }}</p>
                    </div>
                    <span class="question-meta-tag">{{ questionVote.voteType }}</span>
                  </div>
                </div>
              </div>
            </template>
          </Card>

          <Card class="profile-summary-card">
            <template #title>Feedbacks</template>
            <template #content>
              <p v-if="feedbacks.length === 0" class="text-[var(--liaprove-muted)]">Nenhum feedback registrado.</p>
              <div v-else class="space-y-3">
                <div v-for="feedback in feedbacks" :key="feedback.id" class="rounded border border-[var(--liaprove-line)] p-3">
                  <div class="flex flex-col gap-2">
                    <p class="text-[var(--liaprove-ink)]">{{ feedback.comment }}</p>
                    <p class="text-sm text-[var(--liaprove-muted)]">
                      {{ feedback.author.name }} · {{ formatDate(feedback.submissionDate) }}
                    </p>
                    <p class="text-xs text-[var(--liaprove-muted)]">
                      Reações: {{ feedback.reactions.length }}
                    </p>
                  </div>
                </div>
              </div>
            </template>
          </Card>
        </section>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
