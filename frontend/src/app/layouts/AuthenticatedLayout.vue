<script setup lang="ts">
import Button from 'primevue/button'
import type { UserRole } from '@/shared/types/auth'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/shared/stores/auth'

const auth = useAuthStore()
const router = useRouter()

type NavItem = {
  label: string
  to: string
  icon: string
  roles?: UserRole[]
}

const navigation: NavItem[] = [
  { label: 'Dashboard', to: '/dashboard', icon: 'pi pi-home' },
  { label: 'Perfil', to: '/profile', icon: 'pi pi-user' },
  { label: 'Avaliações', to: '/assessments/start', icon: 'pi pi-play-circle' },
  { label: 'Questões', to: '/questions/voting', icon: 'pi pi-check-square' },
  { label: 'Recrutador', to: '/recruiter/job-analysis', icon: 'pi pi-briefcase', roles: ['RECRUITER', 'ADMIN'] },
  { label: 'Admin', to: '/admin/users', icon: 'pi pi-shield', roles: ['ADMIN'] },
]

function canShow(item: NavItem): boolean {
  return !item.roles || auth.hasAnyRole(item.roles)
}

function logout(): void {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <main class="app-shell min-h-screen text-[var(--liaprove-ink)]">
    <header class="border-b border-[var(--liaprove-line)] bg-white/95">
      <div class="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between gap-4">
          <RouterLink to="/dashboard" class="flex items-center gap-3 font-semibold text-[var(--liaprove-ink)]">
            <span class="auth-mark">LP</span>
            <span>LIA Prove</span>
          </RouterLink>
          <div class="flex items-center gap-3">
            <span class="hidden text-sm text-[var(--liaprove-muted)] sm:inline">{{ auth.user?.name }}</span>
            <Button
              data-test="logout-button"
              class="shell-logout-button"
              label="Sair"
              icon="pi pi-sign-out"
              severity="secondary"
              outlined
              size="small"
              @click="logout"
            />
          </div>
        </div>
        <nav aria-label="Navegação principal" class="flex gap-2 overflow-x-auto pb-1">
          <RouterLink
            v-for="item in navigation.filter(canShow)"
            :key="item.to"
            :to="item.to"
            class="shell-nav-link"
          >
            <i :class="item.icon" aria-hidden="true" />
            <span>{{ item.label }}</span>
          </RouterLink>
        </nav>
      </div>
    </header>

    <section class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <slot />
    </section>
  </main>
</template>
