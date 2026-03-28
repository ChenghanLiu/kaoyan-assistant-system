import { createRouter, createWebHistory } from 'vue-router'
import { h } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import StudentLayout from '../layout/StudentLayout.vue'
import AdminLayout from '../layout/AdminLayout.vue'
import StudentHomeView from '../views/student/StudentHomeView.vue'
import SchoolListView from '../views/student/SchoolListView.vue'
import SchoolDetailView from '../views/student/SchoolDetailView.vue'
import GuideView from '../views/student/GuideView.vue'
import PlanView from '../views/student/PlanView.vue'
import TaskView from '../views/student/TaskView.vue'
import MaterialView from '../views/student/MaterialView.vue'
import MyUploadView from '../views/student/MyUploadView.vue'
import PostListView from '../views/student/PostListView.vue'
import PostDetailView from '../views/student/PostDetailView.vue'
import ExamListView from '../views/student/ExamListView.vue'
import ExamAnswerView from '../views/student/ExamAnswerView.vue'
import ResultView from '../views/student/ResultView.vue'
import WrongQuestionView from '../views/student/WrongQuestionView.vue'
import ProfileView from '../views/student/ProfileView.vue'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import AdminUsersView from '../views/admin/AdminUsersView.vue'
import AdminSchoolsView from '../views/admin/AdminSchoolsView.vue'
import AdminMajorsView from '../views/admin/AdminMajorsView.vue'
import AdminGuidesView from '../views/admin/AdminGuidesView.vue'
import AdminMaterialReviewView from '../views/admin/AdminMaterialReviewView.vue'
import AdminPolicyNewsView from '../views/admin/AdminPolicyNewsView.vue'
import AdminPaperView from '../views/admin/AdminPaperView.vue'
import AdminQuestionView from '../views/admin/AdminQuestionView.vue'
import AdminNoticeView from '../views/admin/AdminNoticeView.vue'
import AdminConfigView from '../views/admin/AdminConfigView.vue'
import AdminLogView from '../views/admin/AdminLogView.vue'
import PagePlaceholder from '../components/PagePlaceholder.vue'

const placeholder = (title) => ({
  render() {
    return h(PagePlaceholder, {
      title,
      subtitle: '功能入口已预留',
      description: '当前页面暂未开放具体内容，可从其他模块继续查看备考信息与学习数据。'
    })
  }
})

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView, meta: { guest: true } },
  { path: '/register', component: RegisterView, meta: { guest: true } },
  {
    path: '/student',
    component: StudentLayout,
    meta: { requiresAuth: true, roles: ['STUDENT'] },
    children: [
      { path: 'home', component: StudentHomeView },
      { path: 'schools', component: SchoolListView },
      { path: 'schools/:id', component: SchoolDetailView },
      { path: 'majors/:id', component: placeholder('专业详情页') },
      { path: 'guides', component: GuideView },
      { path: 'plans', component: PlanView },
      { path: 'tasks', component: TaskView },
      { path: 'materials', component: MaterialView },
      { path: 'materials/:id', component: placeholder('资料详情页') },
      { path: 'my-uploads', component: MyUploadView },
      { path: 'posts', component: PostListView },
      { path: 'posts/:id', component: PostDetailView },
      { path: 'exams', component: ExamListView },
      { path: 'exams/:id', component: ExamAnswerView },
      { path: 'results', component: ResultView },
      { path: 'wrong-questions', component: WrongQuestionView },
      { path: 'profile', component: ProfileView }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      { path: 'dashboard', component: AdminDashboardView },
      { path: 'users', component: AdminUsersView },
      { path: 'schools', component: AdminSchoolsView },
      { path: 'majors', component: AdminMajorsView },
      { path: 'guides', component: AdminGuidesView },
      { path: 'material-reviews', component: AdminMaterialReviewView },
      { path: 'policy-news', component: AdminPolicyNewsView },
      { path: 'papers', component: AdminPaperView },
      { path: 'questions', component: AdminQuestionView },
      { path: 'notices', component: AdminNoticeView },
      { path: 'configs', component: AdminConfigView },
      { path: 'logs', component: AdminLogView }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  if (authStore.token && (!authStore.user || authStore.shouldHydrateProfile)) {
    try {
      await authStore.fetchMe()
    } catch (error) {
      authStore.logout()
    }
  }

  if (to.meta.guest && authStore.token) {
    return authStore.isAdmin ? '/admin/dashboard' : '/student/home'
  }

  if (to.meta.requiresAuth && !authStore.token) {
    ElMessage.warning('请先登录')
    return '/login'
  }

  if (to.meta.roles && !to.meta.roles.some((role) => authStore.user?.roles?.includes(role))) {
    ElMessage.error('无权限访问该页面')
    return authStore.isAdmin ? '/admin/dashboard' : '/student/home'
  }
})

export default router
