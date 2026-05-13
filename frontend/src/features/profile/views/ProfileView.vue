<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import { useAuthStore } from '@/shared/stores/auth'
import type { ExperienceLevel } from '@/shared/types/auth'
import {
  changePassword,
  deactivateOwnAccount,
  getUserProfile,
  updateUserProfile,
  type UserProfileResponse,
} from '../services/userService'

const auth = useAuthStore()
const router = useRouter()

const loading = ref(true)
const saving = ref(false)
const passwordDialogVisible = ref(false)
const passwordSaving = ref(false)
const message = ref('')
const errorMessage = ref('')

const experienceOptions: ExperienceLevel[] = ['JUNIOR', 'PLENO', 'SENIOR']

const form = reactive({
  name: '',
  email: '',
  occupation: '',
  bio: '',
  experienceLevel: null as ExperienceLevel | null,
  hardSkillsText: '',
  softSkillsText: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
})

const pageTitle = computed(() => (loading.value ? 'Perfil' : form.name || 'Perfil'))

function skillsToText(skills?: string[] | null): string {
  return skills?.join(', ') ?? ''
}

function textToSkills(value: string): string[] {
  return value
    .split(',')
    .map((skill) => skill.trim())
    .filter(Boolean)
}

function fillForm(profile: UserProfileResponse): void {
  form.name = profile.name ?? ''
  form.email = profile.email ?? ''
  form.occupation = profile.occupation ?? ''
  form.bio = profile.bio ?? ''
  form.experienceLevel = profile.experienceLevel ?? null
  form.hardSkillsText = skillsToText(profile.hardSkills)
  form.softSkillsText = skillsToText(profile.softSkills)
}

async function loadProfile(): Promise<void> {
  if (!auth.user) {
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    fillForm(await getUserProfile(auth.user.id))
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

async function saveProfile(): Promise<void> {
  if (!auth.user) {
    return
  }

  saving.value = true
  message.value = ''
  errorMessage.value = ''

  try {
    const updated = await updateUserProfile(auth.user.id, {
      name: form.name,
      email: form.email,
      occupation: form.occupation,
      bio: form.bio,
      experienceLevel: form.experienceLevel,
      hardSkills: textToSkills(form.hardSkillsText),
      softSkills: textToSkills(form.softSkillsText),
    })

    fillForm(updated)
    auth.updateUser(updated)
    message.value = 'Perfil atualizado com sucesso.'
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    saving.value = false
  }
}

async function submitPasswordChange(): Promise<void> {
  if (!auth.user) {
    return
  }

  passwordSaving.value = true
  message.value = ''
  errorMessage.value = ''

  try {
    await changePassword(auth.user.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordDialogVisible.value = false
    message.value = 'Senha alterada com sucesso.'
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    passwordSaving.value = false
  }
}

async function deactivateAccount(): Promise<void> {
  errorMessage.value = ''
  message.value = ''

  try {
    await deactivateOwnAccount()
    auth.logout()
    message.value = 'Conta desativada. Você será redirecionado para o login.'
    window.setTimeout(() => router.push('/login'), 5000)
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  }
}

onMounted(loadProfile)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase text-[var(--liaprove-accent-strong)]">Perfil</p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">{{ pageTitle }}</h1>
          <p class="mt-2 text-[var(--liaprove-muted)]">
            Revise seus dados básicos e mantenha suas competências atualizadas.
          </p>
        </div>
      </section>

      <p v-if="message" class="status-message status-message-success">{{ message }}</p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <Card class="profile-card">
        <template #content>
          <form class="grid gap-5 lg:grid-cols-2" @submit.prevent="saveProfile">
            <label class="profile-field">
              <span>Nome</span>
              <InputText data-test="profile-name" v-model="form.name" :disabled="loading" />
            </label>

            <label class="profile-field">
              <span>E-mail</span>
              <InputText data-test="profile-email" v-model="form.email" type="email" :disabled="loading" />
            </label>

            <label class="profile-field">
              <span>Ocupação</span>
              <InputText data-test="profile-occupation" v-model="form.occupation" :disabled="loading" />
            </label>

            <label class="profile-field">
              <span>Nível de experiência</span>
              <Select v-model="form.experienceLevel" :options="experienceOptions" :disabled="loading" />
            </label>

            <label class="profile-field lg:col-span-2">
              <span>Bio</span>
              <Textarea data-test="profile-bio" v-model="form.bio" rows="4" :disabled="loading" />
            </label>

            <label class="profile-field">
              <span>Hard skills</span>
              <InputText v-model="form.hardSkillsText" :disabled="loading" placeholder="Java, Spring, Vue" />
            </label>

            <label class="profile-field">
              <span>Soft skills</span>
              <InputText v-model="form.softSkillsText" :disabled="loading" placeholder="Comunicação, liderança" />
            </label>

            <div class="flex flex-wrap gap-3 lg:col-span-2">
              <Button
                data-test="save-profile"
                type="button"
                label="Salvar perfil"
                icon="pi pi-save"
                :loading="saving"
                @click="saveProfile"
              />
              <Button
                data-test="open-password-dialog"
                type="button"
                label="Alterar senha"
                icon="pi pi-lock"
                severity="secondary"
                outlined
                @click="passwordDialogVisible = true"
              />
              <Button
                data-test="deactivate-account"
                type="button"
                label="Desativar conta"
                icon="pi pi-ban"
                severity="danger"
                outlined
                @click="deactivateAccount"
              />
            </div>
          </form>
        </template>
      </Card>
    </div>

    <Dialog
      v-model:visible="passwordDialogVisible"
      modal
      header="Alterar senha"
      append-to="self"
      class="profile-password-dialog w-[min(28rem,92vw)]"
    >
      <form class="space-y-4" @submit.prevent="submitPasswordChange">
        <label class="profile-field">
          <span>Senha atual</span>
          <InputText data-test="old-password" v-model="passwordForm.oldPassword" type="password" />
        </label>
        <label class="profile-field">
          <span>Nova senha</span>
          <InputText data-test="new-password" v-model="passwordForm.newPassword" type="password" />
        </label>
        <div class="flex justify-end gap-2">
          <Button type="button" label="Cancelar" severity="secondary" outlined @click="passwordDialogVisible = false" />
          <Button
            data-test="confirm-password-change"
            type="button"
            label="Confirmar"
            icon="pi pi-check"
            :loading="passwordSaving"
            @click="submitPasswordChange"
          />
        </div>
      </form>
    </Dialog>
  </AuthenticatedLayout>
</template>
