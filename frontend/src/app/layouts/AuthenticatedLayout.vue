<script setup lang="ts">
import Button from 'primevue/button'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/shared/stores/auth'

const auth = useAuthStore()
const router = useRouter()

function logout(): void {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <main class="min-h-screen bg-slate-50 text-slate-900">
    <header class="border-b border-slate-200 bg-white">
      <div class="mx-auto flex max-w-6xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
        <RouterLink to="/dashboard" class="text-lg font-semibold text-slate-950">LIA Prove</RouterLink>
        <div class="flex items-center gap-3">
          <span class="hidden text-sm text-slate-600 sm:inline">{{ auth.user?.name }}</span>
          <Button label="Sair" severity="secondary" outlined size="small" @click="logout" />
        </div>
      </div>
    </header>

    <section class="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
      <slot />
    </section>
  </main>
</template>
