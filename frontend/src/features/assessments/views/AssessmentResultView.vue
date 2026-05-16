<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { readAssessmentResult } from '../utils/assessmentSession'

const route = useRoute()
const attemptId = computed(() => String(route.params.attemptId ?? ''))
const result = computed(() => readAssessmentResult(attemptId.value))
const accuracyLabel = computed(() => {
  if (result.value?.accuracyRate === null || result.value?.accuracyRate === undefined) {
    return '--'
  }

  return `${result.value.accuracyRate}%`
})
const statusLabel = computed(() => {
  switch (result.value?.status) {
    case 'APPROVED':
      return 'Aprovado'
    case 'FAILED':
      return 'Não aprovado'
    case 'COMPLETED':
      return 'Concluído'
    default:
      return 'Em andamento'
  }
})
const certificateRoute = computed(() => toCertificateRoute(result.value?.certificateUrl))

function toCertificateRoute(certificateUrl: string | null | undefined): string | null {
  if (!certificateUrl?.trim()) {
    return null
  }

  const match = certificateUrl.trim().match(/(?:^|\/)certificates\/([^/?#]+)/)
  if (!match?.[1]) {
    return null
  }

  return `/certificates/${match[1]}`
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Avaliações
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Resultado da avaliação</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Este relatório fica disponível temporariamente neste navegador depois do envio.
          </p>
        </div>
      </section>

      <Card v-if="!result" class="assessment-card">
        <template #content>
          <div class="flex flex-col gap-4">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">
              Resultado indisponível neste dispositivo.
            </h2>
            <p class="text-[var(--liaprove-muted)]">
              O resultado não foi encontrado na sessão atual. Inicie uma nova avaliação para gerar um novo relatório.
            </p>
            <RouterLink to="/assessments/start">
              <Button label="Iniciar nova avaliação" icon="pi pi-play-circle" />
            </RouterLink>
          </div>
        </template>
      </Card>

      <template v-else>
        <Card class="assessment-card">
          <template #content>
            <div class="grid gap-5 md:grid-cols-3">
              <div>
                <p class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Status</p>
                <p class="mt-2 text-2xl font-semibold text-[var(--liaprove-ink)]">{{ statusLabel }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Aproveitamento</p>
                <p class="mt-2 text-2xl font-semibold text-[var(--liaprove-ink)]">{{ accuracyLabel }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Tentativa</p>
                <p class="mt-2 break-all text-sm font-semibold text-[var(--liaprove-ink)]">{{ attemptId }}</p>
              </div>
            </div>

            <p class="mt-5 status-message status-message-success">{{ result.message }}</p>
          </template>
        </Card>

        <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
          <RouterLink to="/dashboard">
            <Button label="Voltar ao dashboard" icon="pi pi-home" severity="secondary" outlined />
          </RouterLink>
          <RouterLink to="/assessments/start">
            <Button label="Iniciar nova avaliação" icon="pi pi-play-circle" severity="secondary" outlined />
          </RouterLink>
          <RouterLink v-if="certificateRoute" :to="certificateRoute" data-test="open-certificate">
            <Button label="Ver certificado" icon="pi pi-verified" />
          </RouterLink>
        </div>
      </template>
    </div>
  </AuthenticatedLayout>
</template>
