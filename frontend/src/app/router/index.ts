import { createRouter, createWebHistory, type Router } from 'vue-router'
import { redirectAuthenticated, requireAuth } from './guards'
import { useAuthStore } from '@/shared/stores/auth'

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
      path: '/profile',
      name: 'profile',
      beforeEnter: requireAuth,
      component: () => import('@/features/profile/views/ProfileView.vue'),
    },
    {
      path: '/questions/voting',
      name: 'questions-voting',
      beforeEnter: requireAuth,
      component: () => import('@/features/questions/views/QuestionsVotingListView.vue'),
    },
    {
      path: '/questions/:id/voting',
      name: 'question-voting-detail',
      beforeEnter: requireAuth,
      component: () => import('@/features/questions/views/QuestionVotingDetailView.vue'),
    },
    {
      path: '/questions/new',
      name: 'question-new',
      beforeEnter: requireAuth,
      component: () => import('@/features/questions/views/QuestionSubmissionView.vue'),
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

export function handleSessionExpired(targetRouter: Router = router): void {
  const auth = useAuthStore()
  auth.logout()

  if (targetRouter.currentRoute.value.path !== '/login') {
    targetRouter.push('/login')
  }
}

window.addEventListener('liaprove:session-expired', () => handleSessionExpired())
