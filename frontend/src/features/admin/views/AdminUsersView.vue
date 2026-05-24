<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  activateAdminUser,
  deactivateAdminUser,
  deleteAdminUser,
  listAdminUsers,
  type AdminUserResponse,
  type ListAdminUsersParams,
} from '../services/adminService'
import type { UserRole } from '@/shared/types/auth'

const page = ref(0)
const size = 20
const nameFilter = ref('')
const roleFilter = ref<UserRole | ''>('')
const users = ref<AdminUserResponse[]>([])
const loading = ref(true)
const errorMessage = ref('')
const successMessages = ref<string[]>([])

const canGoPrevious = computed(() => page.value > 0)
const canGoNext = computed(() => users.value.length === size)

function userSkills(user: AdminUserResponse): string {
  const skills = [...(user.hardSkills ?? []), ...(user.softSkills ?? [])]
  return skills.length > 0 ? skills.join(', ') : 'Sem competências informadas'
}

function companySummary(user: AdminUserResponse): string {
  if (user.role !== 'RECRUITER') {
    return user.occupation || 'Ocupação não informada'
  }

  const company = user.companyName || 'Empresa não informada'
  const email = user.companyEmail || 'Email corporativo não informado'
  return `${company} · ${email}`
}

function currentParams(): ListAdminUsersParams {
  return {
    name: nameFilter.value.trim() || undefined,
    role: roleFilter.value || undefined,
    page: page.value,
    size,
  }
}

async function loadUsers(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    users.value = await listAdminUsers(currentParams())
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    users.value = []
  } finally {
    loading.value = false
  }
}

async function applyFilters(): Promise<void> {
  page.value = 0
  successMessages.value = []
  await loadUsers()
}

async function activateUser(id: string): Promise<void> {
  await runUserAction(() => activateAdminUser(id), 'Usuário ativado com sucesso.')
}

async function deactivateUser(id: string): Promise<void> {
  await runUserAction(() => deactivateAdminUser(id), 'Usuário desativado com sucesso.')
}

async function removeUser(id: string): Promise<void> {
  if (!window.confirm('Remover este usuário permanentemente?')) {
    return
  }

  await runUserAction(() => deleteAdminUser(id), 'Usuário removido com sucesso.')
}

async function runUserAction(action: () => Promise<void>, message: string): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    await action()
    successMessages.value = [...successMessages.value, message]
    await loadUsers()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    loading.value = false
  }
}

async function previousPage(): Promise<void> {
  if (!canGoPrevious.value) {
    return
  }

  page.value -= 1
  await loadUsers()
}

async function nextPage(): Promise<void> {
  if (!canGoNext.value) {
    return
  }

  page.value += 1
  await loadUsers()
}

onMounted(loadUsers)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Administração
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Usuários</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Consulte contas cadastradas e execute ações administrativas de moderação.
          </p>
        </div>
      </section>

      <Card class="profile-summary-card">
        <template #content>
          <form class="grid gap-4 lg:grid-cols-[1fr_220px_auto]" @submit.prevent="applyFilters">
            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Nome
              <input
                v-model="nameFilter"
                data-test="admin-user-filter-name"
                class="auth-input"
                type="search"
                placeholder="Buscar por nome"
              />
            </label>

            <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
              Perfil
              <select v-model="roleFilter" data-test="admin-user-filter-role" class="auth-input">
                <option value="">Todos</option>
                <option value="PROFESSIONAL">Profissional</option>
                <option value="RECRUITER">Recrutador</option>
                <option value="ADMIN">Administrador</option>
              </select>
            </label>

            <div class="flex items-end">
              <Button
                data-test="admin-user-apply-filters"
                type="submit"
                label="Filtrar"
                icon="pi pi-search"
                :disabled="loading"
                @click="applyFilters"
              />
            </div>
          </form>
        </template>
      </Card>

      <p
        v-for="message in successMessages"
        :key="message"
        class="status-message status-message-success"
      >
        {{ message }}
      </p>
      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>

      <div v-if="loading" class="status-message status-message-success">Carregando usuários...</div>

      <Card v-else-if="users.length === 0 && !errorMessage" class="profile-summary-card">
        <template #content>
          <div class="flex flex-col gap-2">
            <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Nenhum usuário encontrado.</h2>
            <p class="text-[var(--liaprove-muted)]">Ajuste os filtros para ampliar a busca.</p>
          </div>
        </template>
      </Card>

      <section v-else class="grid gap-4">
        <Card v-for="user in users" :key="user.id" class="action-card">
          <template #content>
            <div class="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
              <div class="min-w-0 space-y-3">
                <div class="flex flex-wrap items-center gap-2">
                  <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">{{ user.name }}</h2>
                  <span class="question-meta-tag">{{ user.role }}</span>
                  <span v-if="user.experienceLevel" class="question-meta-tag">{{ user.experienceLevel }}</span>
                </div>

                <div class="grid gap-2 text-sm text-[var(--liaprove-muted)] md:grid-cols-2">
                  <p>{{ user.email }}</p>
                  <p>{{ companySummary(user) }}</p>
                </div>

                <p class="text-sm text-[var(--liaprove-muted)]">{{ userSkills(user) }}</p>
              </div>

              <div class="flex flex-wrap gap-2 lg:justify-end">
                <Button
                  :data-test="`activate-user-${user.id}`"
                  label="Ativar"
                  icon="pi pi-check"
                  size="small"
                  severity="success"
                  outlined
                  :disabled="loading"
                  @click="activateUser(user.id)"
                />
                <Button
                  :data-test="`deactivate-user-${user.id}`"
                  label="Desativar"
                  icon="pi pi-ban"
                  size="small"
                  severity="warn"
                  outlined
                  :disabled="loading"
                  @click="deactivateUser(user.id)"
                />
                <Button
                  :data-test="`delete-user-${user.id}`"
                  label="Remover"
                  icon="pi pi-trash"
                  size="small"
                  severity="danger"
                  outlined
                  :disabled="loading"
                  @click="removeUser(user.id)"
                />
              </div>
            </div>
          </template>
        </Card>
      </section>

      <div class="flex items-center justify-between gap-3">
        <Button
          label="Anterior"
          icon="pi pi-arrow-left"
          severity="secondary"
          outlined
          :disabled="loading || !canGoPrevious"
          @click="previousPage"
        />
        <span class="text-sm font-semibold text-[var(--liaprove-muted)]">Página {{ page + 1 }}</span>
        <Button
          label="Próxima"
          icon="pi pi-arrow-right"
          icon-pos="right"
          severity="secondary"
          outlined
          :disabled="loading || !canGoNext"
          @click="nextPage"
        />
      </div>
    </div>
  </AuthenticatedLayout>
</template>
