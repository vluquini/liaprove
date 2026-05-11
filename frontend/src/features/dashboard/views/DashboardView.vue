<script setup lang="ts">
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { useAuthStore } from '@/shared/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <AuthenticatedLayout>
    <Card class="dashboard-card">
      <template #title>
        <span class="text-[var(--liaprove-ink)]">Dashboard</span>
      </template>
      <template #content>
        <p class="text-[var(--liaprove-ink)]">Olá, {{ auth.user?.name }}. Seu perfil atual é {{ auth.user?.role }}.</p>
      </template>
    </Card>
  </AuthenticatedLayout>
</template>
