import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterPage.vue'),
    meta: { title: '注册' }
  },
  // 删掉 db-config 路由 ↓
  // {
  //   path: '/db-config',
  //   name: 'DBConfig',
  //   component: () => import('@/views/DBConfigPage.vue'),
  //   meta: { title: '数据库配置' }
  // },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/DashboardPage.vue'),
    meta: { title: '数字大屏' }
  },
  {
    path: '/materials',
    name: 'Materials',
    component: () => import('@/views/MaterialsPage.vue'),
    meta: { title: '物料管理' }
  },
  {
    path: '/inbound',
    name: 'Inbound',
    component: () => import('@/views/InboundPage.vue'),
    meta: { title: '入库管理' }
  },
  {
    path: '/outbound',
    name: 'Outbound',
    component: () => import('@/views/OutboundPage.vue'),
    meta: { title: '出库管理' }
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('@/views/InventoryPage.vue'),
    meta: { title: '库存查询' }
  },
  {
    path: '/safety',
    name: 'SafetyLog',
    component: () => import('@/views/SafetyPage.vue'),
    meta: { title: '安全台账' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/SettingsPage.vue'),
    meta: { title: '系统设置' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
