<template>
  <div class="plan-page">
    <section class="page-card plan-hero">
      <div class="page-header plan-hero__header">
        <div>
          <div class="page-title">学习计划</div>
          <div class="page-subtitle">制定阶段目标，拆分学习任务，持续推进备考进度</div>
        </div>
        <el-button type="primary" size="large" @click="dialogVisible = true">新建学习计划</el-button>
      </div>
      <div class="stat-grid">
        <div class="plan-summary-card">
          <span>计划总数</span>
          <strong>{{ plans.length }}</strong>
          <small>先设阶段目标，再逐步拆解任务</small>
        </div>
        <div class="plan-summary-card">
          <span>任务总数</span>
          <strong>{{ totalTaskCount }}</strong>
          <small>所有任务都从某个学习计划展开</small>
        </div>
        <div class="plan-summary-card">
          <span>已完成任务</span>
          <strong>{{ completedTaskCount }}</strong>
          <small>任务完成会直接推动计划完成率</small>
        </div>
        <div class="plan-summary-card">
          <span>平均完成率</span>
          <strong>{{ averageCompletionRate }}%</strong>
          <small>用来快速判断整体备考推进情况</small>
        </div>
      </div>
    </section>

    <section class="plan-grid" v-if="plans.length">
      <el-card v-for="plan in planCards" :key="plan.id" shadow="hover" class="plan-card">
        <div class="plan-card__top">
          <div>
            <div class="plan-card__title">{{ plan.planName }}</div>
            <p class="plan-card__desc">{{ plan.planDescription || '暂未填写计划说明，可补充本阶段的备考目标。' }}</p>
          </div>
          <el-tag :type="plan.statusMeta.type" effect="light" round>{{ plan.statusMeta.label }}</el-tag>
        </div>

        <div class="plan-card__meta">
          <span>时间：{{ formatDate(plan.startDate) }} 至 {{ formatDate(plan.endDate) }}</span>
          <span>任务：{{ plan.totalTaskCount }} 项</span>
          <span>已完成：{{ plan.completedTaskCount }} 项</span>
        </div>

        <div class="plan-card__progress">
          <div class="plan-card__progress-head">
            <span>完成率</span>
            <strong>{{ plan.completionRate }}%</strong>
          </div>
          <el-progress :percentage="plan.completionRate" :stroke-width="12" :show-text="false" />
        </div>

        <div class="plan-card__footer">
          <div class="plan-card__tip">任务属于学习计划，完成任务后这里的完成率会同步提升。</div>
          <div class="plan-card__actions">
            <el-button @click="goToTasks(plan.id)">查看任务</el-button>
            <el-button type="primary" plain @click="goToTasks(plan.id, true)">新增任务</el-button>
          </div>
        </div>
      </el-card>
    </section>

    <section v-else class="page-card">
      <el-empty description="暂无学习计划，先创建一个阶段目标吧">
        <el-button type="primary" @click="dialogVisible = true">新建学习计划</el-button>
      </el-empty>
    </section>

    <el-dialog v-model="dialogVisible" title="新建学习计划" width="560px">
      <el-form :model="form" label-position="top" class="plan-form">
        <el-form-item label="计划名称">
          <el-input v-model="form.planName" maxlength="128" placeholder="例如：暑期强化阶段复习计划" show-word-limit />
        </el-form-item>
        <el-form-item label="计划描述">
          <el-input
            v-model="form.planDescription"
            type="textarea"
            :rows="4"
            maxlength="2000"
            placeholder="写清这个阶段想完成什么，后续任务会围绕这个目标展开"
            show-word-limit
          />
        </el-form-item>
        <div class="plan-form__row">
          <el-form-item label="开始日期" class="plan-form__item">
            <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期" />
          </el-form-item>
          <el-form-item label="结束日期" class="plan-form__item">
            <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存计划</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createPlan, fetchPlans } from '../../api/student'

const router = useRouter()

const plans = ref([])
const dialogVisible = ref(false)
const form = reactive({ planName: '', planDescription: '', startDate: '', endDate: '' })

const formatDate = (value) => value || '未设置'
const getTodayString = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getPlanStatusMeta = (plan) => {
  const completionRate = Number(plan.completionRate || 0)
  const today = getTodayString()

  if (completionRate >= 100) {
    return { label: '已完成', type: 'success' }
  }

  if (plan.endDate && plan.endDate < today) {
    return { label: '已过期', type: 'danger' }
  }

  return { label: '进行中', type: 'warning' }
}

const planCards = computed(() =>
  plans.value.map((plan) => ({
    ...plan,
    totalTaskCount: Number(plan.totalTaskCount || 0),
    completedTaskCount: Number(plan.completedTaskCount || 0),
    completionRate: Number(plan.completionRate || 0),
    statusMeta: getPlanStatusMeta(plan)
  }))
)

const totalTaskCount = computed(() => planCards.value.reduce((sum, plan) => sum + plan.totalTaskCount, 0))
const completedTaskCount = computed(() => planCards.value.reduce((sum, plan) => sum + plan.completedTaskCount, 0))
const averageCompletionRate = computed(() => {
  if (!planCards.value.length) {
    return 0
  }
  const total = planCards.value.reduce((sum, plan) => sum + plan.completionRate, 0)
  return Math.round(total / planCards.value.length)
})

const resetForm = () => {
  Object.assign(form, { planName: '', planDescription: '', startDate: '', endDate: '' })
}

const loadData = async () => {
  plans.value = await fetchPlans()
}

const goToTasks = (planId, shouldOpenCreate = false) => {
  router.push({
    path: '/student/tasks',
    query: {
      planId,
      ...(shouldOpenCreate ? { action: 'create' } : {})
    }
  })
}

const handleCreate = async () => {
  if (!form.planName.trim()) {
    ElMessage.warning('请先填写计划名称')
    return
  }

  await createPlan(form)
  ElMessage.success('学习计划已创建')
  dialogVisible.value = false
  resetForm()
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.plan-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.plan-hero {
  overflow: hidden;
}

.plan-hero__header {
  margin-bottom: 20px;
}

.plan-summary-card {
  padding: 18px;
  border-radius: 18px;
  border: 1px solid rgba(36, 87, 245, 0.12);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 244, 255, 0.86));
}

.plan-summary-card span,
.plan-summary-card small {
  display: block;
}

.plan-summary-card span {
  font-size: 13px;
  color: var(--text-sub);
}

.plan-summary-card strong {
  display: block;
  margin: 10px 0 6px;
  font-size: 30px;
  line-height: 1;
}

.plan-summary-card small {
  color: #54708f;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.plan-card {
  border: none;
}

.plan-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 24px;
}

.plan-card__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.plan-card__title {
  font-size: 20px;
  font-weight: 700;
}

.plan-card__desc {
  margin: 10px 0 0;
  color: var(--text-sub);
  line-height: 1.7;
  min-height: 48px;
}

.plan-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  color: #516174;
  font-size: 13px;
}

.plan-card__progress {
  padding: 16px;
  border-radius: 16px;
  background: #f6f9ff;
}

.plan-card__progress-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
}

.plan-card__progress-head span {
  color: var(--text-sub);
}

.plan-card__progress-head strong {
  font-size: 24px;
  color: var(--primary-color);
}

.plan-card__footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.plan-card__tip {
  flex: 1;
  font-size: 13px;
  color: #54708f;
  line-height: 1.6;
}

.plan-card__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.plan-form__row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.plan-form__item {
  margin-bottom: 0;
}

.plan-form :deep(.el-date-editor) {
  width: 100%;
}

@media (max-width: 768px) {
  .plan-hero__header,
  .plan-card__footer,
  .plan-card__top {
    flex-direction: column;
    align-items: stretch;
  }

  .plan-form__row {
    grid-template-columns: 1fr;
  }
}
</style>
