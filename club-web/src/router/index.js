import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '首页概览', roles: ['admin', 'leader', 'student'] } },
      { path: 'users', name: 'UserList', component: () => import('../views/user/UserList.vue'), meta: { title: '用户管理', roles: ['admin'] } },
      { path: 'clubs', name: 'ClubList', component: () => import('../views/club/ClubList.vue'), meta: { title: '社团广场', roles: ['admin', 'leader', 'student'] } },
      { path: 'clubs/audit', name: 'ClubAudit', component: () => import('../views/club/ClubAudit.vue'), meta: { title: '社团审核', roles: ['admin'] } },
      { path: 'clubs/apply', name: 'ClubApply', component: () => import('../views/club/ClubApply.vue'), meta: { title: '申请建社', roles: ['admin', 'leader', 'student'] } },
      { path: 'clubs/my', name: 'MyClub', component: () => import('../views/club/MyClub.vue'), meta: { title: '我的社团', roles: ['admin', 'leader', 'student'] } },
      { path: 'clubs/:id', name: 'ClubDetail', component: () => import('../views/club/ClubDetail.vue'), meta: { title: '社团详情', roles: ['admin', 'leader', 'student'] } },
      { path: 'clubs/:clubId/members', name: 'MemberManage', component: () => import('../views/member/MemberManage.vue'), meta: { title: '成员管理', roles: ['admin', 'leader'] } },
      { path: 'activities', name: 'ActivityList', component: () => import('../views/activity/ActivityList.vue'), meta: { title: '活动列表', roles: ['admin', 'leader', 'student'] } },
      { path: 'activities/manage', name: 'ActivityManage', component: () => import('../views/activity/ActivityManage.vue'), meta: { title: '活动管理', roles: ['admin', 'leader'] } },
      { path: 'activities/my', name: 'MyActivity', component: () => import('../views/activity/MyActivity.vue'), meta: { title: '我的报名', roles: ['admin', 'leader', 'student'] } },
      { path: 'activities/:id', name: 'ActivityDetail', component: () => import('../views/activity/ActivityDetail.vue'), meta: { title: '活动详情', roles: ['admin', 'leader', 'student'] } },
      { path: 'notices', name: 'NoticeList', component: () => import('../views/notice/NoticeList.vue'), meta: { title: '公告通知', roles: ['admin', 'leader', 'student'] } },
      { path: 'profile', name: 'Profile', component: () => import('../views/profile/Profile.vue'), meta: { title: '个人中心', roles: ['admin', 'leader', 'student'] } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const store = useUserStore()
  if (to.meta.public) {
    if (store.isLoggedIn && (to.name === 'Login' || to.name === 'Register')) {
      return '/dashboard'
    }
    return true
  }
  if (!store.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  const roles = to.meta.roles
  if (roles && !roles.includes(store.role)) {
    return '/dashboard'
  }
  return true
})

export default router