<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">个人中心</div>
          <div class="page-subtitle">可查看并维护当前账号的基础备考信息</div>
        </div>
        <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
      </div>
      <el-form label-position="top" style="margin-top:20px;max-width:720px" :model="form" v-loading="loading">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="form.username" readonly disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示名称">
              <el-input v-model="form.displayName" maxlength="64" show-word-limit placeholder="请输入显示名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标院校">
              <el-input v-model="form.targetSchool" maxlength="128" show-word-limit placeholder="请输入目标院校" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标专业">
              <el-input v-model="form.targetMajor" maxlength="128" show-word-limit placeholder="请输入目标专业" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchProfile, updateProfile } from '../../api/student'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const saving = ref(false)
const form = reactive({
  username: '',
  displayName: '',
  targetSchool: '',
  targetMajor: ''
})

const applyProfile = (profile) => {
  form.username = profile.username || ''
  form.displayName = profile.displayName || profile.realName || ''
  form.targetSchool = profile.targetSchool || profile.school || ''
  form.targetMajor = profile.targetMajor || profile.major || ''
  authStore.setUser(profile)
}

const loadProfile = async () => {
  loading.value = true
  try {
    const profile = await fetchProfile()
    applyProfile(profile)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    const profile = await updateProfile({
      displayName: form.displayName,
      targetSchool: form.targetSchool,
      targetMajor: form.targetMajor
    })
    applyProfile(profile)
    ElMessage.success('个人信息已保存')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>
