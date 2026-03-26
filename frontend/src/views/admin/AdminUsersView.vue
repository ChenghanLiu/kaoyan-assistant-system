<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">用户管理</div>
        <div class="page-subtitle">查看账号信息并维护启用状态</div>
      </div>
    </div>
    <el-table :data="users">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="displayName" label="显示名称" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="enabled" label="启用状态">
        <template #default="{ row }">
          <el-switch :model-value="row.enabled" @change="(value) => handleChange(row, value)" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchUsers, updateUserEnabled } from '../../api/admin'

const users = ref([])

const loadData = async () => {
  users.value = await fetchUsers()
}

const handleChange = async (row, value) => {
  await updateUserEnabled(row.id, value)
  ElMessage.success('状态已更新')
  loadData()
}

onMounted(loadData)
</script>
