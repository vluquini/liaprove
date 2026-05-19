import { createRouter, createWebHistory, type Router } from 'vue-router'
import { redirectAuthenticated, requireAuth } from './guards'
import { useAuthStore } from '@/shared/stores/auth'
import type { UserRole } from '@/shared/types/auth'

const recruiterRoles: UserRole[] = ['RECRUITER', 'ADMIN']
const pendingFeatureView = { template: '<div />' }

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
      path: '/certificates/:certificateNumber',
      name: 'certificate-public',
      component: () => import('@/features/certificates/views/CertificateVerificationView.vue'),
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
      path: '/assessments/start',
      name: 'assessment-start',
      beforeEnter: requireAuth,
      component: () => import('@/features/assessments/views/AssessmentStartView.vue'),
    },
    {
      path: '/assessments/personalized/:token/start',
      name: 'personalized-assessment-start',
      beforeEnter: requireAuth,
      component: pendingFeatureView,
    },
    {
      path: '/assessments/attempts/:attemptId',
      name: 'assessment-attempt',
      beforeEnter: requireAuth,
      component: () => import('@/features/assessments/views/AssessmentAttemptView.vue'),
    },
    {
      path: '/assessments/attempts/:attemptId/result',
      name: 'assessment-result',
      beforeEnter: requireAuth,
      component: () => import('@/features/assessments/views/AssessmentResultView.vue'),
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
      path: '/recruiter',
      name: 'recruiter-home',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: () => import('@/features/recruiter/views/RecruiterHomeView.vue'),
    },
    {
      path: '/recruiter/job-analysis',
      name: 'recruiter-job-analysis',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: () => import('@/features/recruiter/views/RecruiterJobAnalysisView.vue'),
    },
    {
      path: '/recruiter/assessments/new',
      name: 'recruiter-assessment-new',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: () => import('@/features/recruiter/views/RecruiterAssessmentCreateView.vue'),
    },
    {
      path: '/recruiter/assessments/:assessmentId',
      name: 'recruiter-assessment-detail',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: pendingFeatureView,
    },
    {
      path: '/recruiter/assessments/:assessmentId/edit',
      name: 'recruiter-assessment-edit',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: pendingFeatureView,
    },
    {
      path: '/recruiter/assessments/:assessmentId/attempts',
      name: 'recruiter-assessment-attempts',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: pendingFeatureView,
    },
    {
      path: '/recruiter/attempts/:attemptId',
      name: 'recruiter-attempt-detail',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: pendingFeatureView,
    },
    {
      path: '/recruiter/questions/open/new',
      name: 'recruiter-open-question-new',
      beforeEnter: requireAuth,
      meta: { roles: recruiterRoles },
      component: pendingFeatureView,
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
