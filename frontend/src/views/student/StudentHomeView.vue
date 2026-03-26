<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">欢迎回来，{{ authStore.displayName }}</div>
          <div class="page-subtitle">目标院校：{{ authStore.user?.targetSchool || '暂未填写' }} / 目标专业：{{ authStore.user?.targetMajor || '暂未填写' }}</div>
        </div>
      </div>
      <div class="stat-grid">
        <div class="stat-card">
          <div>学习计划数</div>
          <div style="font-size:32px;font-weight:700;margin-top:12px">{{ plans.length }}</div>
        </div>
        <div class="stat-card" style="background:linear-gradient(135deg,#0f766e,#14b8a6)">
          <div>帖子总数</div>
          <div style="font-size:32px;font-weight:700;margin-top:12px">{{ posts.length }}</div>
        </div>
        <div class="stat-card" style="background:linear-gradient(135deg,#9a3412,#f97316)">
          <div>模拟试卷数</div>
          <div style="font-size:32px;font-weight:700;margin-top:12px">{{ papers.length }}</div>
        </div>
      </div>
    </div>
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">系统公告</div>
          <div class="page-subtitle">站内提醒与学习信息汇总</div>
        </div>
      </div>
      <el-timeline>
        <el-timeline-item v-for="notice in notices" :key="notice.id" :timestamp="notice.createdAt">
          <strong>{{ notice.title }}</strong>
          <div style="margin-top:6px">{{ notice.content }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { fetchExamRecords, fetchNotices, fetchPapers, fetchPlans, fetchPosts } from '../../api/student'

const authStore = useAuthStore()
const plans = ref([])
const posts = ref([])
const papers = ref([])
const notices = ref([])

onMounted(async () => {
  plans.value = await fetchPlans()
  posts.value = await fetchPosts()
  papers.value = await fetchPapers()
  notices.value = await fetchNotices()
  await fetchExamRecords()
})
</script>
