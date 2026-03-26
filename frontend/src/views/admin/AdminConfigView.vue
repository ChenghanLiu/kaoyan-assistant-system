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
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleSave(row)">保存</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchSystemConfigs, updateSystemConfig } from '../../api/admin'

const list = ref([])

const loadData = async () => {
  list.value = await fetchSystemConfigs()
}

const handleSave = async (row) => {
  await updateSystemConfig(row.configKey, { configValue: row.configValue, configDescription: row.configDescription })
  ElMessage.success('保存成功')
  loadData()
}

onMounted(loadData)
</script>
