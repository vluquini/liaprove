<template>
  <main class="flex min-h-screen items-center justify-center bg-slate-50 px-4 text-slate-900">
    <section class="w-full max-w-md text-center">
      <p class="text-sm font-semibold uppercase text-cyan-700">403</p>
      <h1 class="mt-3 text-3xl font-semibold text-slate-950">Acesso negado</h1>
      <p class="mt-3 text-slate-600">Seu perfil não tem permissão para acessar esta tela.</p>
      <RouterLink class="mt-6 inline-block font-medium text-cyan-700" to="/dashboard">
        Voltar ao dashboard
      </RouterLink>
    </section>
  </main>
</template>
