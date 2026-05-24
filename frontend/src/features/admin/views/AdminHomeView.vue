<script setup lang="ts">
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'

type AdminAction = {
  label: string
  description: string
  to: string
  icon: string
}

const actions: AdminAction[] = [
  {
    label: 'Usuários',
    description: 'Gerencie contas, ativações, desativações e remoções administrativas.',
    to: '/admin/users',
    icon: 'pi pi-users',
  },
  {
    label: 'Questões',
    description: 'Revise submissões, modere status e audite votos e feedbacks.',
    to: '/admin/questions',
    icon: 'pi pi-verified',
  },
  {
    label: 'Tentativas',
    description: 'Acompanhe tentativas de avaliações do sistema e personalizadas.',
    to: '/admin/assessments/attempts',
    icon: 'pi pi-list-check',
  },
  {
    label: 'Algoritmo genético',
    description: 'Ajuste pesos de votos e multiplicadores usados na curadoria.',
    to: '/admin/algorithms/genetic',
    icon: 'pi pi-sliders-h',
  },
]
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Administração
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Painel administrativo</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Acesse os fluxos de moderação, auditoria e manutenção do LIA Prove.
          </p>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Card v-for="action in actions" :key="action.to" class="action-card">
          <template #content>
            <div class="flex h-full flex-col gap-4">
              <div class="flex items-center gap-3">
                <span class="action-icon">
                  <i :class="action.icon" aria-hidden="true" />
                </span>
                <div>
                  <h2 class="text-lg font-semibold text-[var(--liaprove-ink)]">{{ action.label }}</h2>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">{{ action.description }}</p>
                </div>
              </div>

              <RouterLink :to="action.to" class="mt-auto">
                <Button label="Abrir" icon="pi pi-arrow-right" icon-pos="right" size="small" />
              </RouterLink>
            </div>
          </template>
        </Card>
      </section>
    </div>
  </AuthenticatedLayout>
</template>
