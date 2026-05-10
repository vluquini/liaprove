import { createRouter, createWebHistory } from 'vue-router'
import { redirectAuthenticated, requireAuth } from './guards'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/login',
      name: 'login',
      beforeEnter: redirectAuthenticated,
      component: () => import('@/features/auth/views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      beforeEnter: redirectAuthenticated,
      component: () => import('@/features/auth/views/RegisterView.vue'),
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      beforeEnter: requireAuth,
      component: () => import('@/features/dashboard/views/DashboardView.vue'),
    },
    {
      path: '/forbidden',
      name: 'forbidden',
      component: () => import('@/features/errors/views/ForbiddenView.vue'),
    },
    {
      path: '/not-found',
      name: 'not-found',
      component: () => import('@/features/errors/views/NotFoundView.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/not-found',
    },
  ],
})

window.addEventListener('liaprove:session-expired', () => {
  if (router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
})
