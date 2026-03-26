<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">学习计划</div>
        <div class="page-subtitle">设置阶段计划，拆解复习路径</div>
      </div>
      <el-button type="primary" @click="dialogVisible = true">新增计划</el-button>
    </div>
    <el-table :data="plans">
      <el-table-column prop="planName" label="计划名称" />
      <el-table-column prop="planDescription" label="计划说明" />
      <el-table-column prop="startDate" label="开始日期" />
      <el-table-column prop="endDate" label="结束日期" />
      <el-table-column prop="completionRate" label="完成率">
        <template #default="{ row }">{{ row.completionRate }}%</template>
      </el-table-column>
    </el-table>
    <div class="page-title" style="margin-top:24px">提醒列表</div>
    <el-table :data="reminders">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" />
      <el-table-column prop="reminderType" label="类型" width="120" />
      <el-table-column prop="remindAt" label="提醒时间" width="180" />
    </el-table>
    <el-dialog v-model="dialogVisible" title="新增学习计划" width="480px">
      <el-form :model="form" label-position="top">
        <el-form-item label="计划名称"><el-input v-model="form.planName" /></el-form-item>
        <el-form-item label="计划说明"><el-input v-model="form.planDescription" type="textarea" /></el-form-item>
        <el-form-item label="开始日期"><el-input v-model="form.startDate" placeholder="2026-04-01" /></el-form-item>
        <el-form-item label="结束日期"><el-input v-model="form.endDate" placeholder="2026-06-30" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createPlan, fetchPlans, fetchReminders } from '../../api/student'

const plans = ref([])
const reminders = ref([])
const dialogVisible = ref(false)
const form = reactive({ planName: '', planDescription: '', startDate: '', endDate: '' })

const loadData = async () => {
  ;[plans.value, reminders.value] = await Promise.all([fetchPlans(), fetchReminders()])
}

const handleCreate = async () => {
  await createPlan(form)
  ElMessage.success('新增成功')
  dialogVisible.value = false
  Object.assign(form, { planName: '', planDescription: '', startDate: '', endDate: '' })
  loadData()
}

onMounted(loadData)
</script>
