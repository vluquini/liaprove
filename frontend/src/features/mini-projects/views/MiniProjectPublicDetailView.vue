<script setup lang="ts">
import AuthenticatedLayout from '@/app/layouts/AuthenticatedLayout.vue'
</script>

<template>
  <AuthenticatedLayout>
    <section class="dashboard-hero">
      <div>
        <p class="auth-kicker text-sm font-semibold uppercase tracking-wide text-[var(--liaprove-accent-strong)]">
          Avaliação comunitária
        </p>
        <h1 class="mt-2 text-3xl font-semibold text-[var(--liaprove-ink)]">Detalhe do mini-projeto</h1>
        <p class="mt-2 max-w-2xl text-[var(--liaprove-muted)]">
          O detalhe completo da avaliação pública será carregado aqui.
        </p>
      </div>
    </section>
  </AuthenticatedLayout>
</template>
