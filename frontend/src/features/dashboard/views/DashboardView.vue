<script setup lang="ts">
import Card from 'primevue/card'
import Button from 'primevue/button'
import type { UserRole } from '@/shared/types/auth'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { useAuthStore } from '@/shared/stores/auth'

const auth = useAuthStore()

type Action = {
  label: string
  description: string
  to: string
  icon: string
  roles: UserRole[]
}

const actions: Action[] = [
  {
    label: 'Iniciar avaliação',
    description: 'Comece uma avaliação do sistema.',
    to: '/assessments/start',
    icon: 'pi pi-play-circle',
    roles: ['PROFESSIONAL', 'ADMIN'],
  },
  {
    label: 'Submeter questão',
    description: 'Envie uma questão para curadoria.',
    to: '/questions/new',
    icon: 'pi pi-plus-circle',
    roles: ['PROFESSIONAL', 'ADMIN'],
  },
  {
    label: 'Votar em questões',
    description: 'Ajude a revisar itens da comunidade.',
    to: '/questions/voting',
    icon: 'pi pi-check-square',
    roles: ['PROFESSIONAL', 'RECRUITER', 'ADMIN'],
  },
  {
    label: 'Analisar vaga',
    description: 'Extraia competências a partir de uma descrição.',
    to: '/recruiter/job-analysis',
    icon: 'pi pi-search',
    roles: ['RECRUITER', 'ADMIN'],
  },
  {
    label: 'Criar avaliação personalizada',
    description: 'Monte uma avaliação para candidatos.',
    to: '/recruiter/assessments/new',
    icon: 'pi pi-list-check',
    roles: ['RECRUITER', 'ADMIN'],
  },
  {
    label: 'Questão aberta',
    description: 'Crie uma questão aberta para processos seletivos.',
    to: '/recruiter/questions/open/new',
    icon: 'pi pi-file-edit',
    roles: ['RECRUITER', 'ADMIN'],
  },
  {
    label: 'Gerenciar usuários',
    description: 'Modere contas e acessos.',
    to: '/admin/users',
    icon: 'pi pi-users',
    roles: ['ADMIN'],
  },
  {
    label: 'Moderar questões',
    description: 'Revise questões submetidas.',
    to: '/admin/questions',
    icon: 'pi pi-verified',
    roles: ['ADMIN'],
  },
  {
    label: 'Algoritmo genético',
    description: 'Ajuste pesos de votos no contexto do TCC.',
    to: '/admin/algorithms/genetic',
    icon: 'pi pi-sliders-h',
    roles: ['ADMIN'],
  },
]

function canShow(action: Action): boolean {
  return auth.user ? action.roles.includes(auth.user.role) : false
}
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Dashboard
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Olá, {{ auth.user?.name }}</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Acesse os principais fluxos disponíveis para seu perfil na plataforma.
          </p>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        <Card v-for="action in actions.filter(canShow)" :key="action.label" class="action-card">
          <template #content>
            <div class="flex h-full flex-col gap-4">
              <div class="flex items-start gap-3">
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

      <Card class="profile-summary-card">
        <template #title>Resumo do perfil</template>
        <template #content>
          <dl class="grid gap-4 sm:grid-cols-3">
            <div>
              <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Tipo</dt>
              <dd class="mt-1 text-[var(--liaprove-ink)]">{{ auth.user?.role }}</dd>
            </div>
            <div>
              <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Ocupação</dt>
              <dd class="mt-1 text-[var(--liaprove-ink)]">{{ auth.user?.occupation || 'Não informada' }}</dd>
            </div>
            <div>
              <dt class="text-xs font-semibold uppercase text-[var(--liaprove-muted)]">Experiência</dt>
              <dd class="mt-1 text-[var(--liaprove-ink)]">{{ auth.user?.experienceLevel || 'Não informada' }}</dd>
            </div>
          </dl>
        </template>
      </Card>
    </div>
  </AuthenticatedLayout>
</template>
