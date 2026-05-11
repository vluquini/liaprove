<template>
  <main class="auth-shell min-h-screen px-4 py-6 text-[var(--liaprove-ink)] sm:px-6 lg:px-8">
    <div class="mx-auto flex min-h-[calc(100vh-3rem)] w-full max-w-6xl items-center">
      <section class="grid w-full gap-8 lg:grid-cols-[1fr_1.02fr] lg:items-center">
        <div class="hidden lg:block">
          <div class="flex items-center gap-3">
            <span class="auth-mark">LP</span>
            <div>
              <p class="auth-kicker text-sm font-bold uppercase text-[var(--liaprove-accent-strong)]">LIA Prove</p>
              <p class="text-sm text-[var(--liaprove-muted)]">Curadoria, avaliações e certificados</p>
            </div>
          </div>

          <div class="mt-12 max-w-xl">
            <p class="auth-kicker text-sm font-bold uppercase text-[var(--liaprove-warm)]">Acesso da plataforma</p>
            <h1 class="auth-display mt-4 text-5xl font-bold leading-tight text-[var(--liaprove-ink)]">
              Competências técnicas avaliadas com critério coletivo.
            </h1>
            <p class="mt-5 max-w-lg text-base leading-7 text-[var(--liaprove-muted)]">
              Uma área de trabalho objetiva para profissionais, recrutadores e administradores acompanharem as etapas
              de avaliação.
            </p>
          </div>
        </div>

        <div class="mx-auto w-full max-w-xl lg:max-w-lg">
          <div class="mb-6 flex items-center gap-3 lg:hidden">
            <span class="auth-mark">LP</span>
            <div>
              <p class="auth-kicker text-sm font-bold uppercase text-[var(--liaprove-accent-strong)]">LIA Prove</p>
              <p class="text-sm text-[var(--liaprove-muted)]">Acesso da plataforma</p>
            </div>
          </div>
          <slot />
        </div>
      </section>
    </div>
  </main>
</template>
