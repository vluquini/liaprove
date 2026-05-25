<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import Button from 'primevue/button'
import Card from 'primevue/card'
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
import { normalizeApiError } from '@/shared/api/errors'
import {
  adjustGeneticWeights,
  getRoleMultiplier,
  listRecruiterVoteWeights,
  setRecruiterMultiplier,
  setRecruiterVoteWeight,
  setRoleMultiplier,
  type AdjustGeneticWeightsResponse,
  type RecruiterVoteWeightResponse,
} from '../services/adminService'

type RoleMultiplierKey = 'PROFESSIONAL' | 'RECRUITER'

const roleKeys: RoleMultiplierKey[] = ['PROFESSIONAL', 'RECRUITER']
const roleMultipliers = ref<Record<RoleMultiplierKey, number | null>>({
  PROFESSIONAL: null,
  RECRUITER: null,
})
const recruiters = ref<RecruiterVoteWeightResponse[]>([])
const recruiterMultiplierInputs = ref<Record<string, number | null>>({})
const recruiterWeightInputs = ref<Record<string, number | null>>({})
const adjustmentResult = ref<AdjustGeneticWeightsResponse | null>(null)
const adjustmentMode = ref<'dry' | 'real' | null>(null)
const loading = ref(true)
const saving = ref(false)
const adjusting = ref(false)
const errorMessage = ref('')
const successMessages = ref<string[]>([])

const adjustmentEntries = computed(() => Object.entries(adjustmentResult.value ?? {}))

async function loadData(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    const [professionalMultiplier, recruiterMultiplier, recruiterWeights] = await Promise.all([
      getRoleMultiplier('PROFESSIONAL'),
      getRoleMultiplier('RECRUITER'),
      listRecruiterVoteWeights({ page: 0, size: 20 }),
    ])

    roleMultipliers.value.PROFESSIONAL = professionalMultiplier
    roleMultipliers.value.RECRUITER = recruiterMultiplier
    recruiters.value = recruiterWeights
    recruiterMultiplierInputs.value = toRecruiterInputMap(recruiterWeights, 'multiplier')
    recruiterWeightInputs.value = toRecruiterInputMap(recruiterWeights, 'voteWeight')
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
    recruiters.value = []
  } finally {
    loading.value = false
  }
}

async function saveRoleMultiplier(role: RoleMultiplierKey): Promise<void> {
  await runSavingAction(async () => {
    await setRoleMultiplier(role, Number(roleMultipliers.value[role]))
    successMessages.value.push(`Multiplicador de ${role} atualizado.`)
  })
}

async function saveRecruiterMultiplier(recruiter: RecruiterVoteWeightResponse): Promise<void> {
  await runSavingAction(async () => {
    const multiplier = Number(recruiterMultiplierInputs.value[recruiter.id])
    await setRecruiterMultiplier(recruiter.id, multiplier)
    recruiter.multiplier = multiplier
    successMessages.value.push(`Multiplicador de ${recruiter.name} atualizado.`)
  })
}

async function saveRecruiterWeight(recruiter: RecruiterVoteWeightResponse): Promise<void> {
  await runSavingAction(async () => {
    const weight = Number(recruiterWeightInputs.value[recruiter.id])
    await setRecruiterVoteWeight(recruiter.id, weight)
    recruiter.voteWeight = weight
    successMessages.value.push(`Peso de ${recruiter.name} atualizado.`)
  })
}

async function runAdjustment(dryRun: boolean): Promise<void> {
  adjusting.value = true
  errorMessage.value = ''
  successMessages.value = []
  adjustmentResult.value = null

  try {
    adjustmentResult.value = await adjustGeneticWeights(dryRun)
    adjustmentMode.value = dryRun ? 'dry' : 'real'
    successMessages.value.push(dryRun ? 'Resultado simulado calculado.' : 'Pesos ajustados com sucesso.')

    if (!dryRun) {
      await loadData()
    }
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    adjusting.value = false
  }
}

async function runSavingAction(action: () => Promise<void>): Promise<void> {
  saving.value = true
  errorMessage.value = ''

  try {
    await action()
  } catch (error) {
    errorMessage.value = normalizeApiError(error).message
  } finally {
    saving.value = false
  }
}

function toRecruiterInputMap(
  recruiterWeights: RecruiterVoteWeightResponse[],
  field: 'multiplier' | 'voteWeight',
): Record<string, number | null> {
  return Object.fromEntries(recruiterWeights.map((recruiter) => [recruiter.id, recruiter[field]]))
}

function formatNumber(value: number | null): string {
  return value === null ? 'Nao definido' : String(value)
}

onMounted(loadData)
</script>

<template>
  <AuthenticatedLayout>
    <div class="space-y-6">
      <section class="dashboard-hero">
        <div>
          <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
            Administração
          </p>
          <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Algoritmo Genético</h1>
          <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
            Ajuste multiplicadores e execute simulações para calibrar o peso dos votos de recrutadores.
          </p>
        </div>
      </section>

      <p v-if="errorMessage" class="status-message status-message-error">{{ errorMessage }}</p>
      <p
        v-for="message in successMessages"
        :key="message"
        class="status-message status-message-success"
      >
        {{ message }}
      </p>
      <div v-if="loading" class="status-message status-message-success">Carregando parâmetros...</div>

      <section v-else class="grid gap-6 xl:grid-cols-[360px_1fr]">
        <div class="space-y-6">
          <Card class="profile-summary-card">
            <template #content>
              <div class="space-y-5">
                <div>
                  <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Multiplicadores por papel</h2>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                    Controle o impacto inicial de profissionais e recrutadores no cálculo.
                  </p>
                </div>

                <form
                  v-for="role in roleKeys"
                  :key="role"
                  class="grid gap-3 rounded border border-[var(--liaprove-line)] p-4"
                  @submit.prevent="saveRoleMultiplier(role)"
                >
                  <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                    {{ role }}
                    <input
                      v-model.number="roleMultipliers[role]"
                      :data-test="`admin-ga-role-${role}`"
                      class="auth-input"
                      type="number"
                      min="0"
                      step="0.01"
                    />
                  </label>
                  <Button
                    :data-test="`admin-ga-save-role-${role}`"
                    type="button"
                    label="Salvar"
                    icon="pi pi-save"
                    :disabled="saving"
                    @click="saveRoleMultiplier(role)"
                  />
                </form>
              </div>
            </template>
          </Card>

          <Card class="profile-summary-card">
            <template #content>
              <div class="space-y-4">
                <div>
                  <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Execução</h2>
                  <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                    Rode uma simulação antes de persistir um novo ajuste de pesos.
                  </p>
                </div>

                <div class="grid gap-3 sm:grid-cols-2">
                  <Button
                    data-test="admin-ga-run-dry"
                    label="Simular"
                    icon="pi pi-play"
                    severity="secondary"
                    :disabled="adjusting"
                    @click="runAdjustment(true)"
                  />
                  <Button
                    data-test="admin-ga-run-real"
                    label="Ajustar pesos"
                    icon="pi pi-check"
                    :disabled="adjusting"
                    @click="runAdjustment(false)"
                  />
                </div>

                <div
                  v-if="adjustmentResult"
                  class="rounded border border-[var(--liaprove-line)] bg-white p-4"
                >
                  <h3 class="font-semibold text-[var(--liaprove-ink)]">
                    {{ adjustmentMode === 'dry' ? 'Resultado simulado' : 'Pesos ajustados' }}
                  </h3>
                  <dl class="mt-3 grid gap-2 text-sm">
                    <div
                      v-for="[recruiterId, weight] in adjustmentEntries"
                      :key="recruiterId"
                      class="flex items-center justify-between gap-4"
                    >
                      <dt class="font-medium text-[var(--liaprove-muted)]">{{ recruiterId }}</dt>
                      <dd class="font-semibold text-[var(--liaprove-ink)]">{{ weight }}</dd>
                    </div>
                  </dl>
                </div>
              </div>
            </template>
          </Card>
        </div>

        <Card class="profile-summary-card">
          <template #content>
            <div class="space-y-5">
              <div>
                <h2 class="text-xl font-semibold text-[var(--liaprove-ink)]">Recrutadores</h2>
                <p class="mt-1 text-sm text-[var(--liaprove-muted)]">
                  Revise pesos atuais e faça ajustes pontuais sem reprocessar todo o algoritmo.
                </p>
              </div>

              <div v-if="recruiters.length === 0" class="status-message status-message-success">
                Nenhum recrutador com peso registrado.
              </div>

              <section v-else class="grid gap-4">
                <article
                  v-for="recruiter in recruiters"
                  :key="recruiter.id"
                  class="rounded border border-[var(--liaprove-line)] p-4"
                >
                  <div class="grid gap-4 lg:grid-cols-[1fr_190px_190px]">
                    <div class="space-y-2">
                      <div>
                        <h3 class="text-lg font-semibold text-[var(--liaprove-ink)]">{{ recruiter.name }}</h3>
                        <p class="text-sm text-[var(--liaprove-muted)]">{{ recruiter.email }}</p>
                      </div>
                      <div class="flex flex-wrap gap-2">
                        <span v-if="recruiter.companyName" class="question-meta-tag">
                          {{ recruiter.companyName }}
                        </span>
                        <span v-if="recruiter.companyEmail" class="question-meta-tag">
                          {{ recruiter.companyEmail }}
                        </span>
                      </div>
                      <p class="text-sm text-[var(--liaprove-muted)]">
                        Peso atual {{ formatNumber(recruiter.voteWeight) }} · Multiplicador atual
                        {{ formatNumber(recruiter.multiplier) }}
                      </p>
                    </div>

                    <form class="grid gap-3" @submit.prevent="saveRecruiterMultiplier(recruiter)">
                      <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                        Multiplicador
                        <input
                          v-model.number="recruiterMultiplierInputs[recruiter.id]"
                          :data-test="`admin-ga-recruiter-multiplier-${recruiter.id}`"
                          class="auth-input"
                          type="number"
                          min="0"
                          step="0.01"
                        />
                      </label>
                      <Button
                        :data-test="`admin-ga-save-recruiter-multiplier-${recruiter.id}`"
                        type="button"
                        label="Salvar"
                        icon="pi pi-save"
                        severity="secondary"
                        :disabled="saving"
                        @click="saveRecruiterMultiplier(recruiter)"
                      />
                    </form>

                    <form class="grid gap-3" @submit.prevent="saveRecruiterWeight(recruiter)">
                      <label class="flex flex-col gap-2 text-sm font-semibold text-[var(--liaprove-ink)]">
                        Peso
                        <input
                          v-model.number="recruiterWeightInputs[recruiter.id]"
                          :data-test="`admin-ga-recruiter-weight-${recruiter.id}`"
                          class="auth-input"
                          type="number"
                          min="0"
                          step="0.01"
                        />
                      </label>
                      <Button
                        :data-test="`admin-ga-save-recruiter-weight-${recruiter.id}`"
                        type="button"
                        label="Salvar"
                        icon="pi pi-save"
                        :disabled="saving"
                        @click="saveRecruiterWeight(recruiter)"
                      />
                    </form>
                  </div>
                </article>
              </section>
            </div>
          </template>
        </Card>
      </section>
    </div>
  </AuthenticatedLayout>
</template>
