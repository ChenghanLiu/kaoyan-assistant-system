<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">模拟测试列表</div>
        <div class="page-subtitle">支持在线答题、自动判分与成绩记录</div>
      </div>
    </div>
    <el-table :data="papers">
      <el-table-column prop="title" label="试卷名称" />
      <el-table-column prop="description" label="说明" />
      <el-table-column prop="paperYear" label="年份" width="100" />
      <el-table-column prop="questionCount" label="题数" width="100" />
      <el-table-column prop="totalScore" label="总分" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/student/exams/${row.id}`)">开始答题</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchPapers } from '../../api/student'

const papers = ref([])
onMounted(async () => {
  papers.value = await fetchPapers()
})
</script>
