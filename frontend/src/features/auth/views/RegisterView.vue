<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import Password from 'primevue/password'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import PublicAuthLayout from '@/app/layouts/PublicAuthLayout.vue'
import { dashboardRouteForRole } from '@/app/router/guards'
import { normalizeApiError } from '@/shared/api/errors'
import { useAuthStore } from '@/shared/stores/auth'
import type { CreateUserRequest, ExperienceLevel, UserRole } from '@/shared/types/auth'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({
  name: '',
  email: '',
  password: '',
  occupation: '',
  bio: '',
  experienceLevel: 'JUNIOR' as ExperienceLevel,
  hardSkillsText: '',
  softSkillsText: '',
  role: 'PROFESSIONAL' as Exclude<UserRole, 'ADMIN'>,
  companyName: '',
  companyEmail: '',
})

const roleOptions = [
  { label: 'Profissional', value: 'PROFESSIONAL' },
  { label: 'Recrutador', value: 'RECRUITER' },
]

const experienceOptions = [
  { label: 'Junior', value: 'JUNIOR' },
  { label: 'Pleno', value: 'PLENO' },
  { label: 'Senior', value: 'SENIOR' },
]

const loading = ref(false)
const errorMessage = ref('')
const isRecruiter = computed(() => form.role === 'RECRUITER')

function parseList(value: string): string[] {
  return value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

function validate(): string {
  if (!form.name || !form.email || !form.password) {
    return 'Informe nome, email e senha.'
  }

  if (form.password.length < 6) {
    return 'A senha deve ter pelo menos 6 caracteres.'
  }

  if (isRecruiter.value && (!form.companyName || !form.companyEmail)) {
    return 'Recrutadores devem informar empresa e email corporativo.'
  }

  return ''
}

async function submit(): Promise<void> {
  errorMessage.value = validate()

  if (errorMessage.value) {
    return
  }

  const request: CreateUserRequest = {
    name: form.name,
    email: form.email,
    password: form.password,
    occupation: form.occupation || undefined,
    bio: form.bio || undefined,
    experienceLevel: form.experienceLevel,
    hardSkills: parseList(form.hardSkillsText),
    softSkills: parseList(form.softSkillsText),
    role: form.role,
    companyName: isRecruiter.value ? form.companyName : undefined,
    companyEmail: isRecruiter.value ? form.companyEmail : undefined,
  }

  loading.value = true

  try {
    await auth.register(request)
    await router.push(dashboardRouteForRole(auth.user?.role))
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
        <span class="text-2xl font-semibold text-slate-950">Criar cadastro</span>
      </template>

      <template #subtitle>
        <span>Cadastre-se como profissional ou recrutador.</span>
      </template>

      <template #content>
        <form class="space-y-5" @submit.prevent="submit">
          <Message v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</Message>

          <div class="grid gap-4 sm:grid-cols-2">
            <label class="block sm:col-span-2">
              <span class="mb-2 block text-sm font-medium text-slate-700">Tipo de conta</span>
              <Select v-model="form.role" class="w-full" :options="roleOptions" option-label="label" option-value="value" />
            </label>

            <label class="block">
              <span class="mb-2 block text-sm font-medium text-slate-700">Nome</span>
              <InputText v-model="form.name" class="w-full" autocomplete="name" />
            </label>

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
                autocomplete="new-password"
              />
            </label>

            <label class="block">
              <span class="mb-2 block text-sm font-medium text-slate-700">Nivel de experiencia</span>
              <Select
                v-model="form.experienceLevel"
                class="w-full"
                :options="experienceOptions"
                option-label="label"
                option-value="value"
              />
            </label>

            <label class="block sm:col-span-2">
              <span class="mb-2 block text-sm font-medium text-slate-700">Ocupacao</span>
              <InputText v-model="form.occupation" class="w-full" placeholder="Desenvolvedor Java" />
            </label>

            <label class="block sm:col-span-2">
              <span class="mb-2 block text-sm font-medium text-slate-700">Hard skills</span>
              <InputText v-model="form.hardSkillsText" class="w-full" placeholder="Java, Spring Boot, SQL" />
            </label>

            <label class="block sm:col-span-2">
              <span class="mb-2 block text-sm font-medium text-slate-700">Soft skills</span>
              <InputText v-model="form.softSkillsText" class="w-full" placeholder="Comunicacao, lideranca" />
            </label>

            <label class="block sm:col-span-2">
              <span class="mb-2 block text-sm font-medium text-slate-700">Bio</span>
              <Textarea v-model="form.bio" class="w-full" rows="3" />
            </label>

            <template v-if="isRecruiter">
              <label class="block">
                <span class="mb-2 block text-sm font-medium text-slate-700">Empresa</span>
                <InputText v-model="form.companyName" class="w-full" />
              </label>

              <label class="block">
                <span class="mb-2 block text-sm font-medium text-slate-700">Email corporativo</span>
                <InputText v-model="form.companyEmail" class="w-full" type="email" />
              </label>
            </template>
          </div>

          <Button class="w-full" label="Criar cadastro" type="submit" :loading="loading" />

          <p class="text-center text-sm text-slate-600">
            Ja tem conta?
            <RouterLink class="font-medium text-cyan-700" to="/login">Entrar</RouterLink>
          </p>
        </form>
      </template>
    </Card>
  </PublicAuthLayout>
</template>
