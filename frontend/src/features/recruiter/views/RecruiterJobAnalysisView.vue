<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  analyzeJobDescription,
  type JobDescriptionAnalysisResponse,
} from '../services/recruiterAssessmentService'

const LAST_JOB_ANALYSIS_KEY = 'liaprove:recruiter:last-job-analysis'

const router = useRouter()
const jobDescription = ref('')
const analysis = ref<JobDescriptionAnalysisResponse | null>(null)
const analyzing = ref(false)
const errorMessage = ref('')

const weights = computed(() => analysis.value?.suggestedCriteriaWeights)

async function submitAnalysis(): Promise<void> {
  if (!jobDescription.value.trim()) {
    errorMessage.value = 'Informe a descrição da vaga.'
    return
  }

  analyzing.value = true
  errorMessage.value = ''

  try {
    analysis.value = await analyzeJobDescription(jobDescription.value.trim())
  } catch (error) {
    analysis.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    analyzing.value = false
  }
}

async function useAnalysis(): Promise<void> {
  if (!analysis.value) {
    return
  }

  sessionStorage.setItem(LAST_JOB_ANALYSIS_KEY, JSON.stringify(analysis.value))
  await router.push('/recruiter/assessments/new')
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Inteligência de vaga
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Analisar vaga</h1>
          <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
            Extraia áreas, competências e pesos sugeridos para montar uma avaliação personalizada.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <div class="grid gap-4 xl:grid-cols-[minmax(0,1fr)_24rem]">
        <Card class="question-form-card">
          <template #title>Descrição da vaga</template>
          <template #content>
            <form class="space-y-4" @submit.prevent="submitAnalysis">
              <label class="profile-field">
                <span>Vaga</span>
                <Textarea
                  data-test="job-description"
                  v-model="jobDescription"
                  rows="10"
                  placeholder="Cole aqui responsabilidades, requisitos técnicos e contexto da posição."
                />
              </label>

              <Button
                data-test="analyze-job-description"
                type="button"
                label="Analisar vaga"
                icon="pi pi-search"
                :loading="analyzing"
                @click="submitAnalysis"
              />
            </form>
          </template>
        </Card>

        <Card class="question-card">
          <template #title>Resultado</template>
          <template #content>
            <div v-if="!analysis" class="space-y-2 text-[var(--liaprove-muted)]">
              <p>A análise aparecerá aqui depois do processamento.</p>
            </div>

            <div v-else class="space-y-5">
              <section class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Áreas</h2>
                <div class="flex flex-wrap gap-2">
                  <Tag v-for="area in analysis.suggestedKnowledgeAreas" :key="area" :value="area" severity="info" />
                </div>
              </section>

              <section class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Hard skills</h2>
                <div class="flex flex-wrap gap-2">
                  <Tag v-for="skill in analysis.suggestedHardSkills" :key="skill" :value="skill" severity="success" />
                </div>
              </section>

              <section class="space-y-2">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Soft skills</h2>
                <div class="flex flex-wrap gap-2">
                  <Tag v-for="skill in analysis.suggestedSoftSkills" :key="skill" :value="skill" severity="secondary" />
                </div>
              </section>

              <section v-if="weights" class="grid gap-2 text-sm">
                <h2 class="text-sm font-bold uppercase text-[var(--liaprove-muted)]">Pesos sugeridos</h2>
                <div class="rounded-md border border-[var(--liaprove-line)] p-3">
                  <span class="font-semibold">Hard skills</span>
                  <span class="float-right">{{ weights.hardSkillsWeight }}%</span>
                </div>
                <div class="rounded-md border border-[var(--liaprove-line)] p-3">
                  <span class="font-semibold">Soft skills</span>
                  <span class="float-right">{{ weights.softSkillsWeight }}%</span>
                </div>
                <div class="rounded-md border border-[var(--liaprove-line)] p-3">
                  <span class="font-semibold">Experiência</span>
                  <span class="float-right">{{ weights.experienceWeight }}%</span>
                </div>
              </section>

              <Button
                data-test="use-analysis"
                label="Usar na avaliação"
                icon="pi pi-arrow-right"
                icon-pos="right"
                @click="useAnalysis"
              />
            </div>
          </template>
        </Card>
      </div>
    </div>
  </AuthenticatedLayout>
</template>
