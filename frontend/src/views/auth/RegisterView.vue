<template>
  <div class="auth-shell">
    <section class="auth-brand">
      <div>
        <div style="font-size:14px;letter-spacing:2px;opacity:.85">ACCOUNT SETUP</div>
        <h1>创建学习账号</h1>
        <p style="max-width: 520px; line-height: 1.8; opacity: .92">
          完成注册后即可进入学习中心，补充目标院校与专业，开始使用资料、计划与模拟练习功能。
        </p>
      </div>
      <div>注册后将自动进入个人学习空间。</div>
    </section>
    <section class="auth-panel">
      <div class="auth-card">
        <div class="page-title">创建账号</div>
        <div class="page-subtitle">填写基本信息后立即开始备考</div>
        <el-form :model="form" label-position="top" style="margin-top: 20px">
          <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
          <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
          <el-form-item label="显示名称"><el-input v-model="form.displayName" /></el-form-item>
          <el-form-item label="目标院校"><el-input v-model="form.targetSchool" /></el-form-item>
          <el-form-item label="目标专业"><el-input v-model="form.targetMajor" /></el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleRegister" :loading="loading">完成注册</el-button>
          <el-button text style="width:100%;margin-top:8px" @click="$router.push('/login')">已有账号？返回登录</el-button>
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
const form = reactive({
  username: '',
  password: '',
  displayName: '',
  email: '',
  phone: '',
  targetSchool: '',
  targetMajor: ''
})

const handleRegister = async () => {
  loading.value = true
  try {
    await authStore.register(form)
    ElMessage.success('注册成功')
    router.push('/student/home')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}
</script>
