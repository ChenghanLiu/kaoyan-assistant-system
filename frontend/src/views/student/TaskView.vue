<template>
  <div class="task-page">
    <section class="page-card task-toolbar">
      <div class="page-header task-toolbar__header">
        <div>
          <div class="page-title">学习任务</div>
          <div class="page-subtitle">学习任务从属于学习计划，完成任务会持续推进该计划的完成率</div>
        </div>
        <div class="task-toolbar__actions">
          <el-select
            v-model="selectedPlanId"
            class="task-toolbar__select"
            placeholder="请选择学习计划"
            :disabled="!plans.length"
            @change="handlePlanChange"
          >
            <el-option v-for="item in plans" :key="item.id" :value="item.id" :label="item.planName" />
          </el-select>
          <el-button type="primary" size="large" :disabled="!selectedPlanId" @click="openCreateDialog">新增任务</el-button>
        </div>
      </div>
    </section>

    <section v-if="activePlan" class="page-card task-context">
      <div class="task-context__head">
        <div>
          <div class="task-context__eyebrow">当前所属学习计划</div>
          <div class="task-context__title">{{ activePlan.planName }}</div>
          <p class="task-context__desc">{{ activePlan.planDescription || '暂未填写计划说明，可补充本阶段的重点与节奏安排。' }}</p>
        </div>
        <el-progress
          type="dashboard"
          :percentage="Number(activePlan.completionRate || 0)"
          :width="136"
          :stroke-width="10"
        >
          <template #default="{ percentage }">
            <div class="task-context__progress-text">
              <strong>{{ percentage }}%</strong>
              <span>完成率</span>
            </div>
          </template>
        </el-progress>
      </div>

      <el-descriptions :column="isMobile ? 1 : 4" border class="task-context__descriptions">
        <el-descriptions-item label="计划周期">
          {{ formatDate(activePlan.startDate) }} 至 {{ formatDate(activePlan.endDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="总任务数">{{ activePlan.totalTaskCount || 0 }} 项</el-descriptions-item>
        <el-descriptions-item label="已完成任务数">{{ activePlan.completedTaskCount || 0 }} 项</el-descriptions-item>
        <el-descriptions-item label="计划状态">
          <el-tag :type="planStatusMeta.type" effect="light" round>{{ planStatusMeta.label }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </section>

    <section v-if="activePlan && taskCards.length" class="task-grid">
      <el-card
        v-for="task in taskCards"
        :key="task.id"
        shadow="hover"
        class="task-card"
        :class="{ 'task-card--done': task.statusLabel === '已完成' }"
      >
        <div class="task-card__head">
          <div>
            <div class="task-card__title">{{ task.taskName }}</div>
            <p class="task-card__desc">{{ task.taskDescription || '暂未填写任务说明，可补充今日学习重点。' }}</p>
          </div>
          <el-tag :type="task.statusMeta.type" effect="light" round>{{ task.statusLabel }}</el-tag>
        </div>

        <div class="task-card__info">
          <span>截止日期：{{ formatDate(task.dueDate) }}</span>
          <span>学习时长：{{ task.latestStudyMinutesText }}</span>
          <span v-if="task.completedAt">完成时间：{{ formatDateTime(task.completedAt) }}</span>
        </div>

        <div class="task-card__progress">
          <div class="task-card__progress-head">
            <span>当前进度</span>
            <strong>{{ task.latestProgressPercent }}%</strong>
          </div>
          <el-progress :percentage="task.latestProgressPercent" :stroke-width="12" />
        </div>

        <div class="task-card__record">
          <div class="task-card__record-title">最近一次进度记录</div>
          <div v-if="task.latestRecord" class="task-card__record-body">
            <p>{{ task.latestRecord.progressNote || '本次未填写进度备注' }}</p>
            <span>{{ formatDateTime(task.latestRecord.recordedAt) }} · {{ task.latestRecord.progressPercent }}% · {{ formatMinutes(task.latestRecord.studyMinutes) }}</span>
          </div>
          <div v-else class="task-card__record-empty">还没有进度记录，先记录一次本次学习内容吧</div>
        </div>

        <div class="task-card__actions">
          <el-button
            :type="task.statusLabel === '已完成' ? 'info' : 'success'"
            plain
            @click="handleToggleStatus(task)"
          >
            {{ task.statusLabel === '已完成' ? '恢复为进行中' : '标记完成' }}
          </el-button>
          <el-button type="primary" @click="openProgressDialog(task)">记录进度</el-button>
        </div>
      </el-card>
    </section>

    <section v-else-if="activePlan" class="page-card">
      <el-empty description="这个学习计划还没有任务，先添加第一项任务吧">
        <el-button type="primary" @click="openCreateDialog">新增任务</el-button>
      </el-empty>
    </section>

    <section v-else class="page-card">
      <el-empty description="请先选择一个学习计划，再查看或创建学习任务" />
    </section>

    <el-dialog v-model="createDialogVisible" title="新增学习任务" width="560px">
      <el-form :model="form" label-position="top" class="task-form">
        <el-form-item label="所属学习计划">
          <el-input :model-value="activePlan?.planName || ''" disabled />
        </el-form-item>
        <el-form-item label="任务名称">
          <el-input v-model="form.taskName" maxlength="128" placeholder="例如：完成数学强化阶段第一章习题" show-word-limit />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            v-model="form.taskDescription"
            type="textarea"
            :rows="4"
            maxlength="2000"
            placeholder="补充任务目标、完成标准或学习安排"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" placeholder="选择截止日期" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存任务</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="progressDialogVisible" title="记录进度" size="460px">
      <div class="progress-drawer">
        <div class="progress-drawer__summary">
          <strong>{{ activeTask?.taskName || '当前任务' }}</strong>
          <span>{{ activeTask?.taskDescription || '补充本次学习情况与阶段推进程度。' }}</span>
        </div>
        <el-form :model="progressForm" label-position="top" class="task-form">
          <el-form-item label="完成百分比">
            <el-input-number v-model="progressForm.progressPercent" :min="0" :max="100" :step="5" class="task-form__number" />
          </el-form-item>
          <el-form-item label="学习时长（分钟）">
            <el-input-number v-model="progressForm.studyMinutes" :min="0" :step="10" class="task-form__number" />
          </el-form-item>
          <el-form-item label="进度备注">
            <el-input
              v-model="progressForm.progressNote"
              type="textarea"
              :rows="6"
              maxlength="2000"
              placeholder="例如：完成了真题精练第 2 章，错题已经整理到笔记中"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        <div class="progress-drawer__footer">
          <el-button @click="progressDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRecordProgress">保存记录</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createTask, fetchPlanDetail, fetchPlans, recordTaskProgress, updateTaskStatus } from '../../api/student'

const route = useRoute()
const router = useRouter()

const plans = ref([])
const selectedPlanId = ref(null)
const activePlan = ref(null)
const createDialogVisible = ref(false)
const progressDialogVisible = ref(false)
const activeTask = ref(null)
const isMobile = ref(typeof window !== 'undefined' ? window.innerWidth < 768 : false)

const form = reactive({ taskName: '', taskDescription: '', dueDate: '' })
const progressForm = reactive({ progressPercent: 0, studyMinutes: 0, progressNote: '' })

const statusMap = {
  TODO: { label: '未开始', type: 'info' },
  IN_PROGRESS: { label: '进行中', type: 'warning' },
  DONE: { label: '已完成', type: 'success' }
}

const formatDate = (value) => value || '未设置'
const getTodayString = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
const formatDateTime = (value) => {
  if (!value) {
    return '未记录'
  }
  return String(value).replace('T', ' ').slice(0, 16)
}
const formatMinutes = (value) => (value || value === 0 ? `${value} 分钟` : '未记录')

const getPlanStatusMeta = (plan) => {
  const completionRate = Number(plan?.completionRate || 0)
  const today = getTodayString()

  if (completionRate >= 100) {
    return { label: '已完成', type: 'success' }
  }

  if (plan?.endDate && plan.endDate < today) {
    return { label: '已过期', type: 'danger' }
  }

  return { label: '进行中', type: 'warning' }
}

const planStatusMeta = computed(() => getPlanStatusMeta(activePlan.value))

const taskCards = computed(() =>
  (activePlan.value?.tasks || []).map((task) => {
    const latestRecord = task.progressRecords?.[0] || null
    const statusMeta = statusMap[task.status] || statusMap.TODO

    return {
      ...task,
      latestRecord,
      latestProgressPercent: Number(task.latestProgressPercent || 0),
      statusLabel: statusMeta.label,
      statusMeta,
      latestStudyMinutesText: formatMinutes(latestRecord?.studyMinutes)
    }
  })
)

const updateViewportState = () => {
  isMobile.value = window.innerWidth < 768
}

const syncRouteQuery = (planId) => {
  router.replace({
    path: '/student/tasks',
    query: {
      ...route.query,
      planId: planId ?? undefined
    }
  })
}

const resetCreateForm = () => {
  Object.assign(form, { taskName: '', taskDescription: '', dueDate: '' })
}

const resetProgressForm = () => {
  Object.assign(progressForm, { progressPercent: 0, studyMinutes: 0, progressNote: '' })
}

const loadPlanDetail = async () => {
  if (!selectedPlanId.value) {
    activePlan.value = null
    return
  }

  activePlan.value = await fetchPlanDetail(selectedPlanId.value)
}

const loadData = async () => {
  plans.value = await fetchPlans()

  if (!plans.value.length) {
    selectedPlanId.value = null
    activePlan.value = null
    return
  }

  const queryPlanId = Number(route.query.planId)
  const matchedPlan = plans.value.find((item) => item.id === queryPlanId) || plans.value[0]
  selectedPlanId.value = matchedPlan?.id ?? null
  await loadPlanDetail()

  if (route.query.action === 'create') {
    createDialogVisible.value = true
    router.replace({ path: '/student/tasks', query: { planId: selectedPlanId.value } })
  }
}

const handlePlanChange = async () => {
  syncRouteQuery(selectedPlanId.value)
  await loadPlanDetail()
}

const openCreateDialog = () => {
  if (!selectedPlanId.value) {
    ElMessage.warning('请先选择学习计划')
    return
  }
  createDialogVisible.value = true
}

const handleCreate = async () => {
  if (!selectedPlanId.value) {
    ElMessage.warning('请先选择学习计划')
    return
  }

  if (!form.taskName.trim()) {
    ElMessage.warning('请先填写任务名称')
    return
  }

  await createTask(selectedPlanId.value, form)
  ElMessage.success('学习任务已创建')
  createDialogVisible.value = false
  resetCreateForm()
  await loadPlanDetail()
}

const handleToggleStatus = async (task) => {
  const nextStatus = task.status === 'DONE' ? 'IN_PROGRESS' : 'DONE'
  await updateTaskStatus(task.id, nextStatus)
  ElMessage.success(nextStatus === 'DONE' ? '任务已标记为已完成' : '任务已恢复为进行中')
  await loadPlanDetail()
}

const openProgressDialog = (task) => {
  activeTask.value = task
  Object.assign(progressForm, {
    progressPercent: Number(task.latestProgressPercent || 0),
    studyMinutes: task.latestRecord?.studyMinutes ?? 0,
    progressNote: ''
  })
  progressDialogVisible.value = true
}

const handleRecordProgress = async () => {
  if (!activeTask.value) {
    return
  }

  await recordTaskProgress(activeTask.value.id, {
    progressPercent: Number(progressForm.progressPercent),
    studyMinutes: Number(progressForm.studyMinutes),
    progressNote: progressForm.progressNote
  })
  ElMessage.success('进度记录已保存')
  progressDialogVisible.value = false
  resetProgressForm()
  await loadPlanDetail()
}

watch(
  () => route.query.planId,
  async (value) => {
    const planId = Number(value)
    if (!planId || planId === selectedPlanId.value) {
      return
    }

    const matchedPlan = plans.value.find((item) => item.id === planId)
    if (!matchedPlan) {
      return
    }

    selectedPlanId.value = matchedPlan.id
    await loadPlanDetail()
  }
)

onMounted(() => {
  loadData()
  window.addEventListener('resize', updateViewportState)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateViewportState)
})
</script>

<style scoped>
.task-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.task-toolbar__header {
  gap: 20px;
}

.task-toolbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.task-toolbar__select {
  width: 320px;
}

.task-context__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 20px;
}

.task-context__eyebrow {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: #68809f;
}

.task-context__title {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
}

.task-context__desc {
  margin: 10px 0 0;
  color: var(--text-sub);
  line-height: 1.8;
}

.task-context__progress-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.task-context__progress-text strong {
  font-size: 24px;
}

.task-context__progress-text span {
  font-size: 12px;
  color: var(--text-sub);
}

.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.task-card {
  border: none;
}

.task-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 24px;
}

.task-card--done {
  background: linear-gradient(180deg, #fbfffc, #f4fbf6);
}

.task-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.task-card__title {
  font-size: 20px;
  font-weight: 700;
}

.task-card__desc {
  margin: 10px 0 0;
  color: var(--text-sub);
  line-height: 1.7;
  min-height: 48px;
}

.task-card__info {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  color: #516174;
  font-size: 13px;
}

.task-card__progress {
  padding: 16px;
  border-radius: 16px;
  background: #f6f9ff;
}

.task-card__progress-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 8px;
}

.task-card__progress-head span {
  color: var(--text-sub);
}

.task-card__progress-head strong {
  font-size: 24px;
  color: var(--primary-color);
}

.task-card__record {
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #e3ebf5;
  background: #fbfdff;
}

.task-card__record-title {
  margin-bottom: 10px;
  font-size: 13px;
  font-weight: 600;
  color: #68809f;
}

.task-card__record-body p,
.task-card__record-empty {
  margin: 0;
  line-height: 1.7;
  color: var(--text-main);
}

.task-card__record-body span {
  display: block;
  margin-top: 10px;
  font-size: 12px;
  color: var(--text-sub);
}

.task-card__record-empty {
  color: var(--text-sub);
}

.task-card__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.task-form :deep(.el-date-editor),
.task-form__number {
  width: 100%;
}

.progress-drawer {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.progress-drawer__summary {
  margin-bottom: 20px;
  padding: 16px;
  border-radius: 16px;
  background: #f6f9ff;
}

.progress-drawer__summary strong,
.progress-drawer__summary span {
  display: block;
}

.progress-drawer__summary span {
  margin-top: 8px;
  color: var(--text-sub);
  line-height: 1.6;
}

.progress-drawer__footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: auto;
  padding-top: 16px;
}

@media (max-width: 768px) {
  .task-toolbar__header,
  .task-context__head,
  .task-card__head {
    flex-direction: column;
    align-items: stretch;
  }

  .task-toolbar__select {
    width: 100%;
  }
}
</style>
