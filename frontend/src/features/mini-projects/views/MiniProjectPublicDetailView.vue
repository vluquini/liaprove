<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { formatAssessmentText, knowledgeAreaLabel } from '@/features/assessments/utils/assessmentLabels'
import { normalizeApiError } from '@/shared/api/errors'
import {
  castMiniProjectAttemptVote,
  getPublicMiniProjectAttemptDetails,
  reactToMiniProjectAttemptFeedback,
  submitMiniProjectAttemptFeedback,
  type MiniProjectReactionType,
  type MiniProjectVoteType,
  type PublicMiniProjectAttemptDetailResponse,
} from '../services/miniProjectService'

const route = useRoute()

const detail = ref<PublicMiniProjectAttemptDetailResponse | null>(null)
const loading = ref(true)
const voting = ref<MiniProjectVoteType | null>(null)
const sendingFeedback = ref(false)
const reactingFeedbackId = ref<string | null>(null)
const feedbackComment = ref('')
const message = ref('')
const errorMessage = ref('')

const attemptId = computed(() => String(route.params.attemptId ?? ''))

const difficultyLabels = {
  EASY: 'Fácil',
  MEDIUM: 'Média',
  HARD: 'Difícil',
}

const relevanceLabels = {
  ONE: 'Muito baixa',
  TWO: 'Baixa',
  THREE: 'Moderada',
  FOUR: 'Alta',
  FIVE: 'Muito alta',
}

function formatDate(value: string | null): string {
  if (!value) {
    return 'Data não informada'
  }

  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(value))
}

function formatDateTime(value: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

async function loadDetails(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    detail.value = await getPublicMiniProjectAttemptDetails(attemptId.value)
  } catch (error) {
    detail.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

async function castVote(voteType: MiniProjectVoteType): Promise<void> {
  voting.value = voteType
  message.value = ''
  errorMessage.value = ''

  try {
    await castMiniProjectAttemptVote(attemptId.value, voteType)
    await loadDetails()
    message.value = 'Voto registrado com sucesso.'
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    voting.value = null
  }
}

async function sendFeedback(): Promise<void> {
  const comment = feedbackComment.value.trim()
  if (!comment) {
    errorMessage.value = 'Informe um comentário para enviar o feedback.'
    return
  }

  sendingFeedback.value = true
  message.value = ''
  errorMessage.value = ''

  try {
    await submitMiniProjectAttemptFeedback(attemptId.value, { comment })
    feedbackComment.value = ''
    await loadDetails()
    message.value = 'Feedback enviado com sucesso.'
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    sendingFeedback.value = false
  }
}

async function react(feedbackId: string, reactionType: MiniProjectReactionType): Promise<void> {
  reactingFeedbackId.value = feedbackId
  message.value = ''
  errorMessage.value = ''

  try {
    await reactToMiniProjectAttemptFeedback(feedbackId, reactionType)
    await loadDetails()
    message.value = 'Reação registrada.'
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
            Avaliação comunitária
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">
            {{ detail?.question.title || 'Detalhe do mini-projeto' }}
          </h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Analise o enunciado, compare com a entrega submetida e contribua com voto e feedback.
          </p>
        </div>
      </section>

      <p v-if="message" class="status-message status-message-success">{{ message }}</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <div v-if="loading" class="status-message status-message-success">Carregando detalhe do mini-projeto...</div>

      <template v-if="detail && !loading">
        <div class="grid gap-4 lg:grid-cols-[minmax(0,1fr)_20rem]">
          <Card class="question-card">
            <template #content>
              <div class="space-y-5">
                <div>
                  <p class="text-sm font-semibold text-[var(--liaprove-muted)]">
                    {{ formatAssessmentText(detail.assessmentTitle) || 'Avaliação sem título' }}
                  </p>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                    {{ detail.authorName || 'Autor não informado' }} · Finalizado em {{ formatDate(detail.finishedAt) }}
                  </p>
                </div>

                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="area in detail.question.knowledgeAreas"
                    :key="area"
                    class="question-meta-tag"
                  >
                    {{ knowledgeAreaLabel(area) }}
                  </span>
                  <span class="question-meta-tag">{{ difficultyLabels[detail.question.difficulty] }}</span>
                  <span class="question-meta-tag">{{ relevanceLabels[detail.question.relevance] }}</span>
                </div>

                <div>
                  <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">Enunciado</h2>
                  <p class="mt-3 whitespace-pre-line text-[var(--liaprove-ink)]">
                    {{ detail.question.description }}
                  </p>
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
                      {{ detail.voteSummary.approves }}
                    </dd>
                  </div>
                  <div class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3">
                    <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Rejeições</dt>
                    <dd class="mt-1 text-2xl font-semibold text-[var(--liaprove-ink)]">
                      {{ detail.voteSummary.rejects }}
                    </dd>
                  </div>
                </div>

                <div class="flex flex-col gap-2">
                  <Button
                    data-test="approve-mini-project"
                    label="Aprovar"
                    icon="pi pi-thumbs-up"
                    :loading="voting === 'APPROVE'"
                    @click="castVote('APPROVE')"
                  />
                  <Button
                    data-test="reject-mini-project"
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
          <template #title>Entrega submetida</template>
          <template #content>
            <div class="space-y-4">
              <a
                v-if="detail.repositoryLink"
                data-test="mini-project-delivery-link"
                class="mini-project-delivery-link"
                :href="detail.repositoryLink"
                rel="noopener noreferrer"
                target="_blank"
              >
                <i class="pi pi-external-link" aria-hidden="true" />
                <span>Abrir entrega</span>
              </a>
              <p v-if="detail.textResponse" class="whitespace-pre-line text-[var(--liaprove-ink)]">
                {{ detail.textResponse }}
              </p>
              <p v-if="!detail.repositoryLink && !detail.textResponse" class="text-[var(--liaprove-muted)]">
                Nenhuma entrega textual ou link foi informado.
              </p>
            </div>
          </template>
        </Card>

        <Card class="question-card">
          <template #title>Enviar feedback</template>
          <template #content>
            <form class="space-y-4" @submit.prevent="sendFeedback">
              <label class="profile-field">
                <span>Comentário</span>
                <Textarea
                  v-model="feedbackComment"
                  data-test="mini-project-feedback-comment"
                  rows="4"
                  maxlength="1000"
                  placeholder="Comente pontos fortes, clareza da entrega ou melhorias necessárias."
                />
              </label>

              <Button
                data-test="send-mini-project-feedback"
                type="button"
                label="Enviar feedback"
                icon="pi pi-send"
                :loading="sendingFeedback"
                @click="sendFeedback"
              />
            </form>
          </template>
        </Card>

        <section class="space-y-3">
          <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Feedbacks recebidos</h2>
          <Card v-if="detail.feedbacks.length === 0" class="question-card">
            <template #content>
              <p class="text-[var(--liaprove-muted)]">Ainda não há feedbacks para este mini-projeto.</p>
            </template>
          </Card>
          <Card v-for="feedback in detail.feedbacks" :key="feedback.id" class="question-card">
            <template #content>
              <div class="flex flex-col gap-3">
                <div>
                  <p class="font-semibold text-[var(--liaprove-ink)]">{{ feedback.author.name }}</p>
                  <p class="text-sm text-[var(--liaprove-muted)]">{{ formatDateTime(feedback.submissionDate) }}</p>
                </div>
                <p class="text-[var(--liaprove-ink)]">{{ feedback.comment }}</p>
                <div class="flex flex-wrap items-center gap-2">
                  <Button
                    :data-test="`like-mini-project-feedback-${feedback.id}`"
                    label="Curtir"
                    icon="pi pi-thumbs-up"
                    size="small"
                    severity="secondary"
                    outlined
                    :loading="reactingFeedbackId === feedback.id"
                    @click="react(feedback.id, 'LIKE')"
                  />
                  <Button
                    :data-test="`dislike-mini-project-feedback-${feedback.id}`"
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
