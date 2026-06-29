<template>
  <div id="app">
    <!-- 登录/注册页面不显示侧边栏 -->
    <div v-if="$route.path === '/login' || $route.path === '/register'">
      <router-view />
    </div>
    <!-- 主布局 -->
    <div v-else style="display:flex;height:100vh;">
      <el-aside width="220px" style="background:rgba(10,10,30,0.95);border-right:1px solid rgba(255,255,255,0.05);flex-shrink:0;">
        <div class="logo">
          <span style="font-size:24px;">🏭</span>
          <span style="font-weight:bold;font-size:18px;background:linear-gradient(90deg,#00d4ff,#7b68ee);-webkit-background-clip:text;-webkit-text-fill-color:transparent;">化工WMS</span>
        </div>
        <div style="padding:0 12px 12px;color:rgba(255,255,255,0.3);font-size:12px;border-bottom:1px solid rgba(255,255,255,0.05);padding-bottom:12px;">
          👤 {{ user?.realName || user?.username || '未登录' }}
          <span v-if="user" style="margin-left:8px;font-size:10px;background:rgba(0,212,255,0.2);padding:1px 8px;border-radius:10px;color:#00d4ff;">
            {{ user?.role }}
          </span>
        </div>
        <el-menu
          :default-active="$route.path"
          router
          background-color="transparent"
          text-color="#8899bb"
          active-text-color="#00d4ff"
          style="border:none;"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>数字大屏</span>
          </el-menu-item>
          <el-menu-item index="/materials" v-if="user && (user.role === 'ADMIN' || user.role === 'MANAGER')">
            <el-icon><Box /></el-icon>
            <span>物料管理</span>
          </el-menu-item>
          <el-menu-item index="/inbound" v-if="user && (user.role === 'ADMIN' || user.role === 'MANAGER')">
            <el-icon><Upload /></el-icon>
            <span>入库管理</span>
          </el-menu-item>
          <el-menu-item index="/outbound" v-if="user && (user.role === 'ADMIN' || user.role === 'MANAGER')">
            <el-icon><Download /></el-icon>
            <span>出库管理</span>
          </el-menu-item>
          <el-menu-item index="/inventory">
            <el-icon><List /></el-icon>
            <span>库存查询</span>
          </el-menu-item>
          <el-menu-item index="/safety" v-if="user && (user.role === 'ADMIN' || user.role === 'MANAGER')">
            <el-icon><Document /></el-icon>
            <span>安全台账</span>
          </el-menu-item>
          <el-menu-item index="/settings" v-if="user && user.role === 'ADMIN'">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
          <el-menu-item @click="logout" style="margin-top:20px;color:#ff4757;">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main style="padding:0;background:#0a0a1a;overflow:hidden;">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataLine, Box, Upload, Download, List, Document, Setting, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const user = ref(null)

const loadUser = () => {
  const userStr = localStorage.getItem('user')
  console.log('读取到的用户数据:', userStr)
  if (userStr && userStr !== 'null' && userStr !== 'undefined') {
    try {
      user.value = JSON.parse(userStr)
      console.log('解析后的用户对象:', user.value)
    } catch (e) {
      console.error('解析用户数据失败', e)
      localStorage.removeItem('user')
      user.value = null
    }
  } else {
    user.value = null
    console.log('没有用户数据，请重新登录')
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = null
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  loadUser()
})
</script>

<style>
* { margin:0; padding:0; box-sizing:border-box; }
html, body, #app { height:100%; background:#0a0a1a; font-family:'Segoe UI',sans-serif; }

.logo {
  display:flex;
  align-items:center;
  gap:10px;
  padding:20px 20px 16px;
}

.el-menu-item {
  border-radius:8px !important;
  margin:2px 12px;
}
.el-menu-item.is-active {
  background:rgba(0,212,255,0.1) !important;
}
.el-menu-item:hover {
  background:rgba(255,255,255,0.05) !important;
}
</style>
