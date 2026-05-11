<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import Password from 'primevue/password'
import PublicAuthLayout from '@/app/layouts/PublicAuthLayout.vue'
import { dashboardRouteForRole } from '@/app/router/guards'
import { normalizeApiError } from '@/shared/api/errors'
import { useAuthStore } from '@/shared/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const form = reactive({
  email: '',
  password: '',
})

const loading = ref(false)
const errorMessage = ref('')

async function submit(): Promise<void> {
  errorMessage.value = ''

  if (!form.email || !form.password) {
    errorMessage.value = 'Informe email e senha.'
    return
  }

  loading.value = true

  try {
    await auth.login({ email: form.email, password: form.password })
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : dashboardRouteForRole(auth.user?.role)
    await router.push(redirect)
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <PublicAuthLayout>
    <Card class="auth-card">
      <template #title>
        <span class="auth-display text-3xl font-bold text-[var(--liaprove-ink)]">Entrar</span>
      </template>

      <template #subtitle>
        <span>Use suas credenciais para acessar seu painel.</span>
      </template>

      <template #content>
        <form class="mt-2 space-y-5" @submit.prevent="submit">
          <Message v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</Message>

          <label class="auth-field block">
            <span class="mb-2 block text-sm font-bold text-[var(--liaprove-ink)]">Email</span>
            <InputText v-model="form.email" class="w-full" type="email" autocomplete="email" />
          </label>

          <label class="auth-field block">
            <span class="mb-2 block text-sm font-bold text-[var(--liaprove-ink)]">Senha</span>
            <Password
              v-model="form.password"
              class="w-full"
              input-class="w-full"
              :feedback="false"
              toggle-mask
              autocomplete="current-password"
            />
          </label>

          <Button class="auth-primary w-full" label="Entrar" type="submit" :loading="loading" />

          <p class="border-t border-[var(--liaprove-line)] pt-5 text-center text-sm text-[var(--liaprove-muted)]">
            Ainda não tem conta?
            <RouterLink class="auth-link" to="/register">Criar cadastro</RouterLink>
          </p>
        </form>
      </template>
    </Card>
  </PublicAuthLayout>
</template>
