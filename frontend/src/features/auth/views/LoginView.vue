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
    <Card class="border border-slate-200 shadow-sm">
      <template #title>
        <span class="text-2xl font-semibold text-slate-950">Entrar</span>
      </template>

      <template #subtitle>
        <span>Acesse sua conta para continuar.</span>
      </template>

      <template #content>
        <form class="space-y-5" @submit.prevent="submit">
          <Message v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</Message>

          <label class="block">
            <span class="mb-2 block text-sm font-medium text-slate-700">Email</span>
            <InputText v-model="form.email" class="w-full" type="email" autocomplete="email" />
          </label>

          <label class="block">
            <span class="mb-2 block text-sm font-medium text-slate-700">Senha</span>
            <Password
              v-model="form.password"
              class="w-full"
              input-class="w-full"
              :feedback="false"
              toggle-mask
              autocomplete="current-password"
            />
          </label>

          <Button class="w-full" label="Entrar" type="submit" :loading="loading" />

          <p class="text-center text-sm text-slate-600">
            Ainda nao tem conta?
            <RouterLink class="font-medium text-cyan-700" to="/register">Criar cadastro</RouterLink>
          </p>
        </form>
      </template>
    </Card>
  </PublicAuthLayout>
</template>
