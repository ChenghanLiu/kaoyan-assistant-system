<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-title">个人中心</div>
      <el-descriptions :column="2" border style="margin-top: 20px">
        <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
        <el-descriptions-item label="显示名称">{{ profile.displayName || profile.realName }}</el-descriptions-item>
        <el-descriptions-item label="目标院校">{{ profile.targetSchool }}</el-descriptions-item>
        <el-descriptions-item label="目标专业">{{ profile.targetMajor }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchProfile } from '../../api/student'

const profile = ref({})

onMounted(async () => {
  profile.value = await fetchProfile()
})
</script>
