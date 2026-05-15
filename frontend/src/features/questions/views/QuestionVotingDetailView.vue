<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  castQuestionVote,
  getQuestionVotingDetails,
  reactToFeedback,
  submitQuestionFeedback,
  type DifficultyLevel,
  type KnowledgeArea,
  type QuestionDetailResponse,
  type ReactionType,
  type RelevanceLevel,
  type VoteType,
} from '../services/questionService'

const route = useRoute()

const question = ref<QuestionDetailResponse | null>(null)
const loading = ref(true)
const voting = ref<VoteType | null>(null)
const sendingFeedback = ref(false)
const reactingFeedbackId = ref<string | null>(null)
const message = ref('')
const errorMessage = ref('')

const difficultyOptions: DifficultyLevel[] = ['EASY', 'MEDIUM', 'HARD']
const relevanceOptions: RelevanceLevel[] = ['ONE', 'TWO', 'THREE', 'FOUR', 'FIVE']
const knowledgeAreaOptions: KnowledgeArea[] = ['SOFTWARE_DEVELOPMENT', 'DATABASE', 'CYBERSECURITY', 'NETWORKS', 'AI']

const feedbackForm = reactive({
  comment: '',
  difficultyLevel: 'MEDIUM' as DifficultyLevel,
  knowledgeArea: 'SOFTWARE_DEVELOPMENT' as KnowledgeArea,
  relevanceLevel: 'THREE' as RelevanceLevel,
})

const questionId = computed(() => String(route.params.id ?? ''))

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

function syncFeedbackDefaults(details: QuestionDetailResponse): void {
  feedbackForm.knowledgeArea = details.knowledgeAreas[0] ?? 'SOFTWARE_DEVELOPMENT'
  feedbackForm.relevanceLevel = details.relevanceByLLM ?? 'THREE'
}

async function loadDetails(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    const details = await getQuestionVotingDetails(questionId.value)
    question.value = details
    syncFeedbackDefaults(details)
  } catch (error) {
    question.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

async function castVote(voteType: VoteType): Promise<void> {
  voting.value = voteType
  message.value = ''
  errorMessage.value = ''

  try {
    await castQuestionVote(questionId.value, voteType)
    message.value = 'Voto registrado com sucesso.'
    await loadDetails()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    voting.value = null
  }
}

async function sendFeedback(): Promise<void> {
  if (!feedbackForm.comment.trim()) {
    errorMessage.value = 'Informe um comentário para enviar o feedback.'
    return
  }

  sendingFeedback.value = true
  message.value = ''
  errorMessage.value = ''

  try {
    await submitQuestionFeedback(questionId.value, {
      comment: feedbackForm.comment.trim(),
      difficultyLevel: feedbackForm.difficultyLevel,
      knowledgeArea: feedbackForm.knowledgeArea,
      relevanceLevel: feedbackForm.relevanceLevel,
    })
    feedbackForm.comment = ''
    message.value = 'Feedback enviado com sucesso.'
    await loadDetails()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    sendingFeedback.value = false
  }
}

async function react(feedbackId: string, reactionType: ReactionType): Promise<void> {
  reactingFeedbackId.value = feedbackId
  message.value = ''
  errorMessage.value = ''

  try {
    await reactToFeedback(feedbackId, reactionType)
    message.value = 'Reação registrada.'
    await loadDetails()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    reactingFeedbackId.value = null
  }
}

onMounted(loadDetails)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Votação
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">
            {{ question?.title || 'Detalhe da questão' }}
          </h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Analise o enunciado, vote e deixe um feedback para melhorar a curadoria comunitária.
          </p>
        </div>
      </section>

      <p v-if="message" class="status-message status-message-success">{{ message }}</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <div v-if="loading" class="status-message status-message-success">Carregando detalhe da questão...</div>

      <template v-if="question && !loading">
        <div class="grid gap-4 lg:grid-cols-[minmax(0,1fr)_20rem]">
          <Card class="question-card">
            <template #content>
              <div class="space-y-5">
                <div>
                  <div class="flex flex-wrap gap-2">
                    <span v-for="area in question.knowledgeAreas" :key="area" class="question-meta-tag">
                      {{ area }}
                    </span>
                  </div>
                  <p class="mt-4 text-sm font-semibold text-[var(--liaprove-muted)]">
                    Por {{ question.author.name }} em {{ formatDate(question.submissionDate) }}
                  </p>
                  <p class="mt-4 whitespace-pre-line text-[var(--liaprove-ink)]">{{ question.description }}</p>
                </div>

                <div class="space-y-3">
                  <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">Alternativas</h2>
                  <div
                    v-for="alternative in question.alternatives"
                    :key="alternative.id"
                    class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3 text-[var(--liaprove-ink)]"
                  >
                    {{ alternative.text }}
                  </div>
                </div>
              </div>
            </template>
          </Card>

          <Card class="question-card">
            <template #content>
              <div class="space-y-5">
                <div class="grid grid-cols-2 gap-3">
                  <div class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3">
                    <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Aprovações</dt>
                    <dd class="mt-1 text-2xl font-semibold text-[var(--liaprove-ink)]">
                      {{ question.voteSummary.approves }}
                    </dd>
                  </div>
                  <div class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3">
                    <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Rejeições</dt>
                    <dd class="mt-1 text-2xl font-semibold text-[var(--liaprove-ink)]">
                      {{ question.voteSummary.rejects }}
                    </dd>
                  </div>
                </div>

                <div class="flex flex-col gap-2">
                  <Button
                    data-test="approve-question"
                    label="Aprovar"
                    icon="pi pi-thumbs-up"
                    :loading="voting === 'APPROVE'"
                    @click="castVote('APPROVE')"
                  />
                  <Button
                    data-test="reject-question"
                    label="Rejeitar"
                    icon="pi pi-thumbs-down"
                    severity="danger"
                    outlined
                    :loading="voting === 'REJECT'"
                    @click="castVote('REJECT')"
                  />
                </div>
              </div>
            </template>
          </Card>
        </div>

        <Card class="question-card">
          <template #title>Enviar feedback</template>
          <template #content>
            <form class="grid gap-4 lg:grid-cols-3" @submit.prevent="sendFeedback">
              <label class="profile-field lg:col-span-3">
                <span>Comentário</span>
                <Textarea data-test="feedback-comment" v-model="feedbackForm.comment" rows="4" />
              </label>

              <label class="profile-field">
                <span>Dificuldade sugerida</span>
                <Select v-model="feedbackForm.difficultyLevel" :options="difficultyOptions" />
              </label>

              <label class="profile-field">
                <span>Área principal</span>
                <Select v-model="feedbackForm.knowledgeArea" :options="knowledgeAreaOptions" />
              </label>

              <label class="profile-field">
                <span>Relevância sugerida</span>
                <Select v-model="feedbackForm.relevanceLevel" :options="relevanceOptions" />
              </label>

              <div class="lg:col-span-3">
                <Button
                  data-test="send-feedback"
                  type="button"
                  label="Enviar feedback"
                  icon="pi pi-send"
                  :loading="sendingFeedback"
                  @click="sendFeedback"
                />
              </div>
            </form>
          </template>
        </Card>

        <section class="space-y-3">
          <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Feedbacks recebidos</h2>
          <Card v-if="question.feedbacks.length === 0" class="question-card">
            <template #content>
              <p class="text-[var(--liaprove-muted)]">Ainda não há feedbacks para esta questão.</p>
            </template>
          </Card>
          <Card v-for="feedback in question.feedbacks" :key="feedback.id" class="question-card">
            <template #content>
              <div class="flex flex-col gap-3">
                <div>
                  <p class="font-semibold text-[var(--liaprove-ink)]">{{ feedback.author.name }}</p>
                  <p class="text-sm text-[var(--liaprove-muted)]">{{ formatDate(feedback.submissionDate) }}</p>
                </div>
                <p class="text-[var(--liaprove-ink)]">{{ feedback.comment }}</p>
                <div class="flex flex-wrap gap-2">
                  <Button
                    :data-test="`like-feedback-${feedback.id}`"
                    label="Curtir"
                    icon="pi pi-thumbs-up"
                    size="small"
                    severity="secondary"
                    outlined
                    :loading="reactingFeedbackId === feedback.id"
                    @click="react(feedback.id, 'LIKE')"
                  />
                  <Button
                    :data-test="`dislike-feedback-${feedback.id}`"
                    label="Discordar"
                    icon="pi pi-thumbs-down"
                    size="small"
                    severity="secondary"
                    outlined
                    :loading="reactingFeedbackId === feedback.id"
                    @click="react(feedback.id, 'DISLIKE')"
                  />
                  <span class="text-sm text-[var(--liaprove-muted)]">
                    {{ feedback.reactions.length }} reação(ões)
                  </span>
                </div>
              </div>
            </template>
          </Card>
        </section>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
