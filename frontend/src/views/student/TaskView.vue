<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">学习任务</div>
        <div class="page-subtitle">按计划拆分待办，跟踪执行状态</div>
      </div>
      <el-button type="primary" @click="dialogVisible = true">新增任务</el-button>
    </div>
    <el-select v-model="selectedPlanId" style="width:280px;margin-bottom:16px" placeholder="请选择学习计划" @change="loadTasks">
      <el-option v-for="item in plans" :key="item.id" :value="item.id" :label="item.planName" />
    </el-select>
    <el-table :data="tasks">
      <el-table-column prop="taskName" label="任务名称" />
      <el-table-column prop="taskDescription" label="任务说明" />
      <el-table-column prop="dueDate" label="截止日期" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="latestProgressPercent" label="最新进度">
        <template #default="{ row }">{{ row.latestProgressPercent }}%</template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="handleToggleStatus(row, row.status === 'DONE' ? 'TODO' : 'DONE')">
            {{ row.status === 'DONE' ? '标记未完成' : '标记完成' }}
          </el-button>
          <el-button size="small" type="primary" @click="openProgressDialog(row)">记录进度</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="新增学习任务" width="480px">
      <el-form :model="form" label-position="top">
        <el-form-item label="所属计划">
          <el-select v-model="selectedPlanId" style="width:100%">
            <el-option v-for="item in plans" :key="item.id" :value="item.id" :label="item.planName" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务名称"><el-input v-model="form.taskName" /></el-form-item>
        <el-form-item label="任务说明"><el-input v-model="form.taskDescription" type="textarea" /></el-form-item>
        <el-form-item label="截止日期"><el-input v-model="form.dueDate" placeholder="2026-04-20" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="progressDialogVisible" title="记录学习进度" width="480px">
      <el-form :model="progressForm" label-position="top">
        <el-form-item label="进度百分比"><el-input v-model="progressForm.progressPercent" placeholder="例如 60" /></el-form-item>
        <el-form-item label="学习时长（分钟）"><el-input v-model="progressForm.studyMinutes" placeholder="例如 90" /></el-form-item>
        <el-form-item label="学习记录"><el-input v-model="progressForm.progressNote" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecordProgress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createTask, fetchPlans, fetchTasks, recordTaskProgress, updateTaskStatus } from '../../api/student'

const tasks = ref([])
const plans = ref([])
const dialogVisible = ref(false)
const progressDialogVisible = ref(false)
const selectedTaskId = ref(null)
const selectedPlanId = ref(null)
const form = reactive({ taskName: '', taskDescription: '', dueDate: '' })
const progressForm = reactive({ progressPercent: '', studyMinutes: '', progressNote: '' })

const loadTasks = async () => {
  tasks.value = selectedPlanId.value ? await fetchTasks(selectedPlanId.value) : []
}

const loadData = async () => {
  plans.value = await fetchPlans()
  if (plans.value.length > 0) {
    selectedPlanId.value = plans.value[0].id
    await loadTasks()
  }
}

const handleCreate = async () => {
  if (!selectedPlanId.value) {
    ElMessage.warning('请先选择学习计划')
    return
  }
  await createTask(selectedPlanId.value, form)
  ElMessage.success('新增成功')
  dialogVisible.value = false
  Object.assign(form, { taskName: '', taskDescription: '', dueDate: '' })
  loadTasks()
}

const handleToggleStatus = async (row, status) => {
  await updateTaskStatus(row.id, status)
  ElMessage.success('状态已更新')
  loadTasks()
}

const openProgressDialog = (row) => {
  selectedTaskId.value = row.id
  progressDialogVisible.value = true
  Object.assign(progressForm, { progressPercent: row.latestProgressPercent ?? 0, studyMinutes: '', progressNote: '' })
}

const handleRecordProgress = async () => {
  await recordTaskProgress(selectedTaskId.value, {
    progressPercent: Number(progressForm.progressPercent),
    studyMinutes: Number(progressForm.studyMinutes),
    progressNote: progressForm.progressNote
  })
  ElMessage.success('进度已记录')
  progressDialogVisible.value = false
  loadTasks()
}

onMounted(loadData)
</script>
