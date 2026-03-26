<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">操作日志查询</div>
        <div class="page-subtitle">展示登录、审核、创建等关键操作记录</div>
      </div>
    </div>
    <el-table :data="list">
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="userRole" label="角色" width="100" />
      <el-table-column prop="operationModule" label="模块" width="120" />
      <el-table-column prop="operationType" label="操作类型" width="140" />
      <el-table-column prop="operationContent" label="操作内容" min-width="280" />
      <el-table-column prop="requestPath" label="请求路径" min-width="220" />
      <el-table-column prop="createdAt" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchOperationLogs } from '../../api/admin'

const list = ref([])

onMounted(async () => {
  list.value = await fetchOperationLogs()
})
</script>
