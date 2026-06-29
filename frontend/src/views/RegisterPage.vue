<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <span style="font-size:40px;">📝</span>
        <h1>用户注册</h1>
        <p>创建您的化工仓库管理系统账号</p>
      </div>
      <el-form @submit.prevent="handleRegister">
        <el-form-item>
          <el-input v-model="username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="realName" placeholder="真实姓名" size="large" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" size="large" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock" @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="role" placeholder="选择角色" size="large" style="width:100%;">
            <el-option label="普通用户" value="USER" />
            <el-option label="仓库管理员" value="MANAGER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;background:linear-gradient(135deg,#00d4ff,#7b68ee);border:none;" :loading="loading" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
        <div style="text-align:center;color:rgba(255,255,255,0.3);font-size:14px;">
          已有账号？<a @click="goLogin" style="color:#00d4ff;cursor:pointer;text-decoration:none;">立即登录</a>
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
const realName = ref('')
const password = ref('')
const confirmPassword = ref('')
const role = ref('USER')
const loading = ref(false)

const handleRegister = async () => {
  if (!username.value || !password.value || !realName.value) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (password.value !== confirmPassword.value) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  if (password.value.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/auth/register', {
      username: username.value,
      password: password.value,
      realName: realName.value,
      role: role.value
    })

    console.log('注册响应:', res.data)

    if (res.data.success) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.message || '注册失败')
    }
  } catch (e) {
    console.error('注册请求失败:', e)
    ElMessage.error('注册失败，请检查网络或后端是否启动')
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display:flex;
  justify-content:center;
  align-items:center;
  height:100vh;
  background:radial-gradient(ellipse at center,#1a1a3e,#0a0a1a);
}
.register-box {
  width:420px;
  padding:40px;
  background:rgba(255,255,255,0.05);
  backdrop-filter:blur(20px);
  border-radius:20px;
  border:1px solid rgba(255,255,255,0.08);
  box-shadow:0 20px 60px rgba(0,0,0,0.5);
}
.register-header {
  text-align:center;
  margin-bottom:32px;
}
.register-header h1 {
  color:#fff;
  font-weight:300;
  font-size:24px;
  margin:8px 0 4px;
}
.register-header p {
  color:rgba(255,255,255,0.2);
  font-size:11px;
  letter-spacing:1px;
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
:deep(.el-select .el-input__wrapper) {
  background:rgba(255,255,255,0.05);
}
:deep(.el-select .el-input__wrapper) .el-input__inner {
  color:#fff;
}
a {
  transition:color 0.3s;
}
a:hover {
  color:#7b68ee !important;
}
</style>
