<template>
  <div class="auth-shell">
    <section class="auth-brand">
      <div>
        <div style="font-size:14px;letter-spacing:2px;opacity:.85">KAOYAN PREP ASSISTANT</div>
        <h1>考研备考助手</h1>
        <p style="max-width: 520px; line-height: 1.8; opacity: .92">
          一站式考研信息与学习平台，覆盖院校查询、招生信息、学习计划、资料管理、交流讨论与模拟练习。
        </p>
      </div>
      <div>
        <div>体验账号</div>
        <div style="margin-top: 12px">管理员：admin / 123456</div>
        <div>学生：student / 123456</div>
      </div>
    </section>
    <section class="auth-panel">
      <div class="auth-card">
        <div class="page-title">考研备考助手</div>
        <div class="page-subtitle">一站式考研信息与学习平台</div>
        <el-form :model="form" label-position="top" style="margin-top: 20px" @keyup.enter="handleLogin">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">立即登录</el-button>
          <el-button text style="width:100%;margin-top:8px" @click="$router.push('/register')">没有账号？去注册</el-button>
        </el-form>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: 'student', password: '123456' })

const handleLogin = async () => {
  loading.value = true
  try {
    const user = await authStore.login(form)
    ElMessage.success('登录成功')
    router.push(user.roles.includes('ADMIN') ? '/admin/dashboard' : '/student/home')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}
</script>
