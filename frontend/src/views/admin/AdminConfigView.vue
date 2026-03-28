<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">系统配置页</div>
        <div class="page-subtitle">维护简单系统参数</div>
      </div>
    </div>
    <el-table :data="list">
      <el-table-column prop="configKey" label="配置键" />
      <el-table-column prop="configValue" label="配置值">
        <template #default="{ row }">
          <el-input v-model="row.configValue" />
        </template>
      </el-table-column>
      <el-table-column prop="configDescription" label="说明">
        <template #default="{ row }">
          <el-input v-model="row.configDescription" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleSave(row)">保存</el-button>
          <el-button type="danger" link @click="handleDelete(row.configKey)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteSystemConfig, fetchSystemConfigs, updateSystemConfig } from '../../api/admin'

const list = ref([])

const loadData = async () => {
  list.value = await fetchSystemConfigs()
}

const handleSave = async (row) => {
  await updateSystemConfig(row.configKey, { configValue: row.configValue, configDescription: row.configDescription })
  ElMessage.success('保存成功')
  loadData()
}

const handleDelete = async (configKey) => {
  try {
    await ElMessageBox.confirm('确认删除该配置项吗？系统关键配置不允许删除。', '提示', { type: 'warning' })
    await deleteSystemConfig(configKey)
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
