<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  getPersonalizedAssessment,
  type PersonalizedAssessmentDetailsResponse,
} from '../services/recruiterAssessmentService'

const route = useRoute()
const assessment = ref<PersonalizedAssessmentDetailsResponse | null>(null)
const loading = ref(false)
const errorMessage = ref('')

const assessmentId = computed(() => String(route.params.assessmentId))
const shareUrl = computed(() => {
  if (!assessment.value) {
    return ''
  }

  return `${window.location.origin}/assessments/personalized/${assessment.value.shareableToken}/start`
})

onMounted(() => {
  loadAssessment()
})

async function loadAssessment(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    assessment.value = await getPersonalizedAssessment(assessmentId.value)
  } catch (error) {
    assessment.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <p v-if="loading" class="text-[var(--liaprove-muted)]">Carregando avaliação...</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <template v-if="assessment">
        <section class="dashboard-hero">
          <div>
            <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
              Avaliação personalizada
            </p>
            <div class="mt-2 flex flex-wrap items-center gap-3">
              <h1 class="text-3xl font-semibold text-[var(--liaprove-ink)]">{{ assessment.title }}</h1>
              <Tag :value="assessment.status" severity="info" />
            </div>
            <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">{{ assessment.description }}</p>
          </div>
          <div class="mt-4 flex flex-wrap gap-2">
            <RouterLink :to="`/recruiter/assessments/${assessment.id}/attempts`">
              <Button label="Tentativas" icon="pi pi-users" size="small" />
            </RouterLink>
            <RouterLink :to="`/recruiter/assessments/${assessment.id}/edit`">
              <Button label="Editar" icon="pi pi-pencil" severity="secondary" outlined size="small" />
            </RouterLink>
          </div>
        </section>

        <div class="grid gap-4 xl:grid-cols-[minmax(0,1fr)_24rem]">
          <div class="space-y-4">
            <Card class="question-card">
              <template #title>Link compartilhável</template>
              <template #content>
                <p class="break-all rounded-md border border-[var(--liaprove-line)] bg-white p-3 font-medium">
                  {{ shareUrl }}
                </p>
              </template>
            </Card>

            <Card class="question-card">
              <template #title>Questões selecionadas</template>
              <template #content>
                <div class="space-y-3">
                  <article
                    v-for="question in assessment.questions"
                    :key="question.id"
                    class="rounded-lg border border-[var(--liaprove-line)] bg-white p-3"
                  >
                    <h2 class="font-semibold text-[var(--liaprove-ink)]">{{ question.title }}</h2>
                    <div class="mt-2 flex flex-wrap gap-2">
                      <Tag v-for="area in question.knowledgeAreas" :key="area" :value="area" severity="info" />
                      <Tag v-if="question.difficultyByCommunity" :value="question.difficultyByCommunity" />
                    </div>
                  </article>
                </div>
              </template>
            </Card>

            <Card v-if="assessment.jobDescriptionAnalysis" class="question-card">
              <template #title>Análise da vaga</template>
              <template #content>
                <div class="space-y-3">
                  <p class="text-[var(--liaprove-muted)]">
                    {{ assessment.jobDescriptionAnalysis.originalJobDescription }}
                  </p>
                  <div class="flex flex-wrap gap-2">
                    <Tag
                      v-for="skill in assessment.jobDescriptionAnalysis.suggestedHardSkills"
                      :key="skill"
                      :value="skill"
                      severity="success"
                    />
                  </div>
                </div>
              </template>
            </Card>
          </div>

          <aside class="space-y-4">
            <Card class="question-card">
              <template #title>Configurações</template>
              <template #content>
                <dl class="space-y-3 text-sm">
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Tentativas</dt>
                    <dd>{{ assessment.totalAttempts }}/{{ assessment.maxAttempts }}</dd>
                  </div>
                  <div>
                    <dt class="font-semibold text-[var(--liaprove-muted)]">Tempo</dt>
                    <dd>{{ assessment.evaluationTimerMinutes }} min</dd>
                  </div>
                </dl>
              </template>
            </Card>

            <Card v-if="assessment.criteriaWeights" class="question-card">
              <template #title>Pesos</template>
              <template #content>
                <div class="space-y-2 text-sm">
                  <p>Hard skills {{ assessment.criteriaWeights.hardSkillsWeight }}%</p>
                  <p>Soft skills {{ assessment.criteriaWeights.softSkillsWeight }}%</p>
                  <p>Experiência {{ assessment.criteriaWeights.experienceWeight }}%</p>
                </div>
              </template>
            </Card>
          </aside>
        </div>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
