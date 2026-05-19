<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  getPersonalizedAssessment,
  updatePersonalizedAssessment,
  type PersonalizedAssessmentDetailsResponse,
  type PersonalizedAssessmentStatus,
} from '../services/recruiterAssessmentService'

const route = useRoute()
const router = useRouter()
const assessment = ref<PersonalizedAssessmentDetailsResponse | null>(null)
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')

const form = reactive({
  expirationDate: '',
  maxAttempts: 1,
  status: 'ACTIVE' as PersonalizedAssessmentStatus,
  hardSkillsWeight: 60,
  softSkillsWeight: 20,
  experienceWeight: 20,
})

const assessmentId = computed(() => String(route.params.assessmentId))

onMounted(() => {
  loadAssessment()
})

async function loadAssessment(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    assessment.value = await getPersonalizedAssessment(assessmentId.value)
    hydrateForm(assessment.value)
  } catch (error) {
    assessment.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

function hydrateForm(value: PersonalizedAssessmentDetailsResponse): void {
  form.expirationDate = value.expirationDate.slice(0, 16)
  form.maxAttempts = value.maxAttempts
  form.status = value.status
  form.hardSkillsWeight = value.criteriaWeights?.hardSkillsWeight ?? 60
  form.softSkillsWeight = value.criteriaWeights?.softSkillsWeight ?? 20
  form.experienceWeight = value.criteriaWeights?.experienceWeight ?? 20
}

async function saveAssessment(): Promise<void> {
  if (form.hardSkillsWeight + form.softSkillsWeight + form.experienceWeight !== 100) {
    errorMessage.value = 'Os pesos devem somar 100.'
    return
  }

  saving.value = true
  errorMessage.value = ''

  try {
    await updatePersonalizedAssessment(assessmentId.value, {
      expirationDate: form.expirationDate,
      maxAttempts: Number(form.maxAttempts),
      status: form.status,
      hardSkillsWeight: Number(form.hardSkillsWeight),
      softSkillsWeight: Number(form.softSkillsWeight),
      experienceWeight: Number(form.experienceWeight),
    })
    await router.push(`/recruiter/assessments/${assessmentId.value}`)
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    saving.value = false
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
              Edição
            </p>
            <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">{{ assessment.title }}</h1>
            <p class="mt-2 max-w-3xl text-[var(--liaprove-muted)]">
              Atualize somente os campos suportados pelo backend: expiração, tentativas, status e pesos.
            </p>
          </div>
        </section>

        <Card class="question-form-card">
          <template #content>
            <form class="grid gap-4 lg:grid-cols-2" @submit.prevent="saveAssessment">
              <label class="profile-field">
                <span>Expiração</span>
                <input
                  data-test="edit-expiration"
                  v-model="form.expirationDate"
                  class="p-inputtext p-component"
                  type="datetime-local"
                />
              </label>

              <label class="profile-field">
                <span>Tentativas máximas</span>
                <input
                  data-test="edit-max-attempts"
                  v-model.number="form.maxAttempts"
                  class="p-inputtext p-component"
                  min="1"
                  type="number"
                />
              </label>

              <label class="profile-field">
                <span>Status</span>
                <select data-test="edit-status" v-model="form.status" class="p-inputtext p-component">
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="DEACTIVATED">DEACTIVATED</option>
                  <option value="EXPIRED">EXPIRED</option>
                </select>
              </label>

              <label class="profile-field">
                <span>Hard skills</span>
                <input
                  data-test="edit-weight-hard"
                  v-model.number="form.hardSkillsWeight"
                  class="p-inputtext p-component"
                  type="number"
                />
              </label>

              <label class="profile-field">
                <span>Soft skills</span>
                <input
                  data-test="edit-weight-soft"
                  v-model.number="form.softSkillsWeight"
                  class="p-inputtext p-component"
                  type="number"
                />
              </label>

              <label class="profile-field">
                <span>Experiência</span>
                <input
                  data-test="edit-weight-experience"
                  v-model.number="form.experienceWeight"
                  class="p-inputtext p-component"
                  type="number"
                />
              </label>

              <div class="lg:col-span-2">
                <Button
                  data-test="save-assessment"
                  type="button"
                  label="Salvar"
                  icon="pi pi-save"
                  :loading="saving"
                  @click="saveAssessment"
                />
              </div>
            </form>
          </template>
        </Card>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
