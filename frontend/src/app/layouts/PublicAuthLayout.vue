<template>
  <main class="min-h-screen bg-slate-50 px-4 py-8 text-slate-900 sm:px-6 lg:px-8">
    <div class="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-6xl items-center">
      <section class="grid w-full gap-8 lg:grid-cols-[0.9fr_1.1fr] lg:items-center">
        <div class="hidden lg:block">
          <p class="text-sm font-semibold uppercase tracking-wide text-cyan-700">LIA Prove</p>
          <h1 class="mt-4 text-4xl font-semibold leading-tight text-slate-950">
            Avaliacoes colaborativas para competencias em TI.
          </h1>
          <p class="mt-4 max-w-xl text-base leading-7 text-slate-600">
            Entre para submeter questoes, participar da curadoria comunitaria e acompanhar avaliacoes profissionais.
          </p>
        </div>

        <div class="mx-auto w-full max-w-xl">
          <slot />
        </div>
      </section>
    </div>
  </main>
</template>
