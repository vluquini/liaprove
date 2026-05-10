<script setup lang="ts">
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { useAuthStore } from '@/shared/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <AuthenticatedLayout>
    <Card>
      <template #title>Dashboard</template>
      <template #content>
        <p class="text-slate-700">Ola, {{ auth.user?.name }}. Seu perfil atual e {{ auth.user?.role }}.</p>
      </template>
    </Card>
  </AuthenticatedLayout>
</template>
