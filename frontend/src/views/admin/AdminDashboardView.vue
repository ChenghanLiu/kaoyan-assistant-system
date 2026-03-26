<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">后台首页 / 仪表盘</div>
          <div class="page-subtitle">展示系统统计分析结果与讨论区管理信息</div>
        </div>
      </div>
      <div class="stat-grid">
        <div class="stat-card"><div>用户总数</div><div style="font-size:32px;margin-top:12px">{{ data.userStats?.totalUsers || 0 }}</div></div>
        <div class="stat-card" style="background:linear-gradient(135deg,#0f766e,#14b8a6)"><div>学生数量</div><div style="font-size:32px;margin-top:12px">{{ data.userStats?.studentCount || 0 }}</div></div>
        <div class="stat-card" style="background:linear-gradient(135deg,#9a3412,#f97316)"><div>管理员数量</div><div style="font-size:32px;margin-top:12px">{{ data.userStats?.adminCount || 0 }}</div></div>
        <div class="stat-card" style="background:linear-gradient(135deg,#6d28d9,#8b5cf6)"><div>平均完成率</div><div style="font-size:32px;margin-top:12px">{{ formatPercent(data.studyPlanStats?.averageCompletionRate) }}</div></div>
      </div>
    </div>

    <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));gap:20px">
      <div class="page-card">
        <div class="page-title">学习计划统计</div>
        <div class="page-subtitle">基于现有计划与任务数据实时汇总</div>
        <div class="metric-list">
          <div><span>学习计划总数</span><strong>{{ data.studyPlanStats?.totalPlans || 0 }}</strong></div>
          <div><span>已完成任务数</span><strong>{{ data.studyPlanStats?.completedTaskCount || 0 }}</strong></div>
          <div><span>未完成任务数</span><strong>{{ data.studyPlanStats?.pendingTaskCount || 0 }}</strong></div>
        </div>
      </div>

      <div class="page-card">
        <div class="page-title">模拟考试统计</div>
        <div class="page-subtitle">展示考试参与情况与成绩区间</div>
        <div class="metric-list">
          <div><span>参与次数</span><strong>{{ data.examStats?.participationCount || 0 }}</strong></div>
          <div><span>平均成绩</span><strong>{{ formatNumber(data.examStats?.averageScore) }}</strong></div>
          <div><span>最高分</span><strong>{{ data.examStats?.highestScore || 0 }}</strong></div>
          <div><span>最低分</span><strong>{{ data.examStats?.lowestScore || 0 }}</strong></div>
        </div>
      </div>

      <div class="page-card">
        <div class="page-title">资料与社区统计</div>
        <div class="page-subtitle">审核数据与讨论区数据同步展示</div>
        <div class="metric-list">
          <div><span>资料总数</span><strong>{{ data.materialStats?.totalMaterials || 0 }}</strong></div>
          <div><span>审核通过数量</span><strong>{{ data.materialStats?.approvedCount || 0 }}</strong></div>
          <div><span>待审核数量</span><strong>{{ data.materialStats?.pendingCount || 0 }}</strong></div>
          <div><span>帖子数量</span><strong>{{ data.contentStats?.postCount || 0 }}</strong></div>
          <div><span>评论数量</span><strong>{{ data.contentStats?.commentCount || 0 }}</strong></div>
        </div>
      </div>
    </div>

    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">讨论区审核</div>
          <div class="page-subtitle">管理员可删除违规帖子与评论</div>
        </div>
      </div>
      <el-table :data="posts" style="margin-top:20px">
        <el-table-column prop="title" label="帖子标题" min-width="220" />
        <el-table-column prop="displayName" label="发布人" width="140" />
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary" @click="loadComments(row.id)">查看评论</el-button>
            <el-button link type="danger" @click="handleDeletePost(row.id)">删除帖子</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="commentDialogVisible" title="帖子评论审核" width="720px">
      <el-table :data="comments">
        <el-table-column prop="displayName" label="评论人" width="140" />
        <el-table-column prop="content" label="评论内容" min-width="280" />
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDeleteComment(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAdminComment, deleteAdminPost, fetchAdminPostComments, fetchAdminPosts, fetchDashboard } from '../../api/admin'

const data = ref({})
const posts = ref([])
const comments = ref([])
const selectedPostId = ref(null)
const commentDialogVisible = ref(false)

const formatPercent = (value) => `${Number(value || 0).toFixed(2)}%`
const formatNumber = (value) => Number(value || 0).toFixed(2)

const loadDashboard = async () => {
  const [dashboardData, postData] = await Promise.all([fetchDashboard(), fetchAdminPosts()])
  data.value = dashboardData
  posts.value = postData
}

const loadComments = async (postId) => {
  selectedPostId.value = postId
  comments.value = await fetchAdminPostComments(postId)
  commentDialogVisible.value = true
}

const handleDeletePost = async (id) => {
  await ElMessageBox.confirm('确认删除该帖子吗？', '提示', { type: 'warning' })
  await deleteAdminPost(id)
  ElMessage.success('删除成功')
  if (selectedPostId.value === id) {
    commentDialogVisible.value = false
  }
  loadDashboard()
}

const handleDeleteComment = async (id) => {
  await ElMessageBox.confirm('确认删除该评论吗？', '提示', { type: 'warning' })
  await deleteAdminComment(id)
  ElMessage.success('删除成功')
  if (selectedPostId.value) {
    comments.value = await fetchAdminPostComments(selectedPostId.value)
  }
}

onMounted(loadDashboard)
</script>

<style scoped>
.metric-list {
  display: grid;
  gap: 12px;
  margin-top: 18px;
}

.metric-list div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f8fafc;
  color: #334155;
}

.metric-list strong {
  font-size: 20px;
  color: #0f172a;
}
</style>
