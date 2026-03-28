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
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteUser, fetchUsers, updateUserEnabled } from '../../api/admin'

const users = ref([])

const loadData = async () => {
  users.value = await fetchUsers()
}

const handleChange = async (row, value) => {
  await updateUserEnabled(row.id, value)
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？存在学习、考试、资料或日志关联时将禁止删除。', '提示', { type: 'warning' })
    await deleteUser(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(loadData)
</script>
