<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import { normalizeApiError } from '@/shared/api/errors'
import { formatAssessmentText } from '@/features/assessments/utils/assessmentLabels'
import {
  verifyCertificate,
  type CertificateVerificationResponse,
} from '../services/certificateService'

const route = useRoute()
const certificateNumber = computed(() => String(route.params.certificateNumber ?? ''))
const certificate = ref<CertificateVerificationResponse | null>(null)
const loading = ref(true)
const errorMessage = ref('')

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(`${value}T00:00:00`))
}

function formatCertificateText(value: string): string {
  return formatAssessmentText(value)
}

async function loadCertificate(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    certificate.value = await verifyCertificate(certificateNumber.value)
  } catch (error) {
    certificate.value = null
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

onMounted(loadCertificate)
</script>

<template>
  <main class="app-shell min-h-screen px-4 py-8 text-[var(--liaprove-ink)] sm:px-6 lg:px-8">
    <section class="mx-auto max-w-4xl space-y-6">
      <div class="flex items-center justify-between gap-4">
        <RouterLink to="/login" class="flex items-center gap-3 font-semibold text-[var(--liaprove-ink)]">
          <span class="auth-mark">LP</span>
          <span>LIA Prove</span>
        </RouterLink>
      </div>

      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Certificados
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Validação pública</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Confira a autenticidade de um certificado emitido pelo LIA Prove.
          </p>
        </div>
      </section>

      <div v-if="loading" class="status-message status-message-success">Carregando certificado...</div>
      <p v-else-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card v-else-if="certificate" class="certificate-panel">
        <template #content>
          <div class="space-y-6">
            <div class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <p class="assessment-progress-pill">Certificado válido</p>
                <h2 class="mt-3 text-2xl font-semibold text-[var(--liaprove-ink)]">
                  {{ formatCertificateText(certificate.title) }}
                </h2>
                <p class="mt-2 text-[var(--liaprove-muted)]">{{ formatCertificateText(certificate.description) }}</p>
              </div>
              <div class="rounded-md border border-[var(--liaprove-line)] bg-white px-4 py-3 text-right">
                <p class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Pontuação</p>
                <p class="text-2xl font-semibold text-[var(--liaprove-ink)]">{{ certificate.score }}</p>
              </div>
            </div>

            <dl class="grid gap-4 md:grid-cols-2">
              <div>
                <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Número</dt>
                <dd class="mt-1 break-all text-[var(--liaprove-ink)]">{{ certificate.certificateNumber }}</dd>
              </div>
              <div>
                <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Emissão</dt>
                <dd class="mt-1 text-[var(--liaprove-ink)]">{{ formatDate(certificate.issueDate) }}</dd>
              </div>
              <div>
                <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Profissional</dt>
                <dd class="mt-1 text-[var(--liaprove-ink)]">{{ certificate.owner.name }}</dd>
              </div>
              <div>
                <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Ocupação</dt>
                <dd class="mt-1 text-[var(--liaprove-ink)]">
                  {{ certificate.owner.occupation }} - {{ certificate.owner.experienceLevel }}
                </dd>
              </div>
            </dl>

            <RouterLink to="/login">
              <Button label="Acessar LIA Prove" icon="pi pi-arrow-right" icon-pos="right" />
            </RouterLink>
          </div>
        </template>
      </Card>
    </section>
  </main>
</template>
