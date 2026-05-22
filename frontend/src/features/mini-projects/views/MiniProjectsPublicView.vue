<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { formatAssessmentText } from '@/features/assessments/utils/assessmentLabels'
import { normalizeApiError } from '@/shared/api/errors'
import {
  castMiniProjectAttemptVote,
  listPublicMiniProjectAttempts,
  submitMiniProjectAttemptFeedback,
  type MiniProjectVoteType,
  type PublicMiniProjectAttemptResponse,
} from '../services/miniProjectService'

const loading = ref(true)
const errorMessage = ref('')
const attempts = ref<PublicMiniProjectAttemptResponse[]>([])
const feedbackByAttemptId = reactive<Record<string, string>>({})
const actionMessages = reactive<Record<string, string>>({})
const actionErrors = reactive<Record<string, string>>({})
const votingAttemptIds = ref<Set<string>>(new Set())
const feedbackAttemptIds = ref<Set<string>>(new Set())
const votedAttemptIds = ref<Set<string>>(new Set())

const hasAttempts = computed(() => attempts.value.length > 0)

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

function addLoadingAttempt(target: typeof votingAttemptIds | typeof feedbackAttemptIds, attemptId: string): void {
  target.value = new Set(target.value).add(attemptId)
}

function removeLoadingAttempt(target: typeof votingAttemptIds | typeof feedbackAttemptIds, attemptId: string): void {
  const next = new Set(target.value)
  next.delete(attemptId)
  target.value = next
}

function markAttemptVoted(attemptId: string): void {
  votedAttemptIds.value = new Set(votedAttemptIds.value).add(attemptId)
}

function appendActionMessage(attemptId: string, message: string): void {
  actionMessages[attemptId] = [actionMessages[attemptId], message].filter(Boolean).join(' ')
}

async function loadAttempts(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    attempts.value = await listPublicMiniProjectAttempts()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    attempts.value = []
  } finally {
    loading.value = false
  }
}

async function vote(attemptId: string, voteType: MiniProjectVoteType): Promise<void> {
  actionMessages[attemptId] = ''
  actionErrors[attemptId] = ''
  addLoadingAttempt(votingAttemptIds, attemptId)

  try {
    await castMiniProjectAttemptVote(attemptId, voteType)
    appendActionMessage(attemptId, 'Voto registrado.')
    markAttemptVoted(attemptId)
  } catch (error) {
    actionErrors[attemptId] = normalizeApiError(error).message
  } finally {
    removeLoadingAttempt(votingAttemptIds, attemptId)
  }
}

async function submitFeedback(attemptId: string): Promise<void> {
  const comment = feedbackByAttemptId[attemptId]?.trim()
  if (!comment) {
    actionErrors[attemptId] = 'Informe um comentário antes de enviar.'
    return
  }

  actionErrors[attemptId] = ''
  addLoadingAttempt(feedbackAttemptIds, attemptId)

  try {
    await submitMiniProjectAttemptFeedback(attemptId, { comment })
    feedbackByAttemptId[attemptId] = ''
    appendActionMessage(attemptId, 'Feedback enviado.')
  } catch (error) {
    actionErrors[attemptId] = normalizeApiError(error).message
  } finally {
    removeLoadingAttempt(feedbackAttemptIds, attemptId)
  }
}

onMounted(loadAttempts)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Avaliação comunitária
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Mini-projetos públicos</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Revise entregas práticas compartilhadas pela comunidade e contribua com votos e feedbacks objetivos.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <div v-if="loading" class="status-message status-message-success">Carregando entregas públicas...</div>

      <Card v-else-if="!hasAttempts && !errorMessage" class="profile-summary-card">
        <template #content>
          <div class="flex flex-col gap-3">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Nenhuma entrega pública disponível.</h2>
            <p class="text-[var(--liaprove-muted)]">
              Quando houver mini-projetos concluídos para avaliação comunitária, eles aparecerão aqui.
            </p>
          </div>
        </template>
      </Card>

      <section v-else class="grid gap-4 lg:grid-cols-2">
        <Card v-for="attempt in attempts" :key="attempt.attemptId" class="mini-project-card">
          <template #content>
            <div class="flex h-full flex-col gap-5">
              <div class="flex items-start gap-3">
                <span class="action-icon">
                  <i class="pi pi-code" aria-hidden="true" />
                </span>
                <div>
                  <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">
                    {{ formatAssessmentText(attempt.assessmentTitle) || 'Mini-projeto sem título' }}
                  </h2>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                    {{ attempt.authorName || 'Autor não informado' }} · Finalizado em {{ formatDate(attempt.finishedAt) }}
                  </p>
                </div>
              </div>

              <a
                v-if="attempt.repositoryLink"
                :data-test="`mini-project-link-${attempt.attemptId}`"
                class="mini-project-delivery-link"
                :href="attempt.repositoryLink"
                rel="noopener noreferrer"
                target="_blank"
              >
                <i class="pi pi-external-link" aria-hidden="true" />
                <span>Abrir entrega</span>
              </a>
              <p v-else class="status-message status-message-success">Entrega sem link público informado.</p>

              <div class="flex flex-wrap gap-2">
                <Button
                  :data-test="`approve-mini-project-${attempt.attemptId}`"
                  label="Aprovar"
                  icon="pi pi-thumbs-up"
                  size="small"
                  :disabled="votedAttemptIds.has(attempt.attemptId) || votingAttemptIds.has(attempt.attemptId)"
                  :loading="votingAttemptIds.has(attempt.attemptId)"
                  @click="vote(attempt.attemptId, 'APPROVE')"
                />
                <Button
                  :data-test="`reject-mini-project-${attempt.attemptId}`"
                  label="Rejeitar"
                  icon="pi pi-thumbs-down"
                  size="small"
                  severity="secondary"
                  outlined
                  :disabled="votedAttemptIds.has(attempt.attemptId) || votingAttemptIds.has(attempt.attemptId)"
                  :loading="votingAttemptIds.has(attempt.attemptId)"
                  @click="vote(attempt.attemptId, 'REJECT')"
                />
              </div>

              <form class="mini-project-feedback-form" @submit.prevent="submitFeedback(attempt.attemptId)">
                <label class="profile-field">
                  <span>Feedback</span>
                  <Textarea
                    v-model="feedbackByAttemptId[attempt.attemptId]"
                    :data-test="`feedback-mini-project-${attempt.attemptId}`"
                    rows="4"
                    maxlength="1000"
                    placeholder="Comente pontos fortes, clareza da entrega ou melhorias necessárias."
                  />
                </label>
                <Button
                  :data-test="`submit-feedback-mini-project-${attempt.attemptId}`"
                  label="Enviar feedback"
                  icon="pi pi-send"
                  size="small"
                  type="button"
                  :disabled="feedbackAttemptIds.has(attempt.attemptId)"
                  :loading="feedbackAttemptIds.has(attempt.attemptId)"
                  @click="submitFeedback(attempt.attemptId)"
                />
              </form>

              <p v-if="actionMessages[attempt.attemptId]" class="status-message status-message-success">
                {{ actionMessages[attempt.attemptId] }}
              </p>
              <p v-if="actionErrors[attempt.attemptId]" class="status-message status-message-error">
                {{ actionErrors[attempt.attemptId] }}
              </p>
            </div>
          </template>
        </Card>
      </section>
    </div>
  </AuthenticatedLayout>
</template>
