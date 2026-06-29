<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <span style="font-size:40px;">🏭</span>
        <h1>化工仓库管理系统</h1>
        <p>CHEMICAL WAREHOUSE MANAGEMENT SYSTEM</p>
      </div>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" size="large" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;background:linear-gradient(135deg,#00d4ff,#7b68ee);border:none;" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
        <div style="text-align:center;color:rgba(255,255,255,0.3);font-size:14px;">
          还没有账号？<a @click="goRegister" style="color:#00d4ff;cursor:pointer;text-decoration:none;">立即注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    })

    console.log('登录响应:', res.data)

    if (res.data.success) {
      // 存储 Token
      localStorage.setItem('token', res.data.token)
      // 存储用户信息
      localStorage.setItem('user', JSON.stringify(res.data.user))

      // 立即更新页面上的用户信息
      window.location.href = '/dashboard'
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    console.error('登录请求失败:', e)
    ElMessage.error('登录失败，请检查网络或后端是否启动')
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display:flex;
  justify-content:center;
  align-items:center;
  height:100vh;
  background:radial-gradient(ellipse at center,#1a1a3e,#0a0a1a);
}
.login-box {
  width:420px;
  padding:40px;
  background:rgba(255,255,255,0.05);
  backdrop-filter:blur(20px);
  border-radius:20px;
  border:1px solid rgba(255,255,255,0.08);
  box-shadow:0 20px 60px rgba(0,0,0,0.5);
}
.login-header {
  text-align:center;
  margin-bottom:32px;
}
.login-header h1 {
  color:#fff;
  font-weight:300;
  font-size:24px;
  margin:8px 0 4px;
}
.login-header p {
  color:rgba(255,255,255,0.2);
  font-size:11px;
  letter-spacing:3px;
}
:deep(.el-input__wrapper) {
  background:rgba(255,255,255,0.05);
  border:1px solid rgba(255,255,255,0.06);
  box-shadow:none;
}
:deep(.el-input__wrapper):hover {
  border-color:rgba(0,212,255,0.3);
}
:deep(.el-input__wrapper.is-focus) {
  border-color:#00d4ff;
  box-shadow:0 0 20px rgba(0,212,255,0.1);
}
:deep(.el-input__inner) {
  color:#fff;
}
a {
  transition:color 0.3s;
}
a:hover {
  color:#7b68ee !important;
}
</style>
