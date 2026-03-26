<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">试卷管理</div>
          <div class="page-subtitle">维护模拟测试试卷基础信息</div>
        </div>
      </div>
      <el-form :model="form" inline @submit.prevent>
        <el-form-item label="试卷名称">
          <el-input v-model="form.title" placeholder="输入试卷名称" />
        </el-form-item>
        <el-form-item label="年份">
          <el-input-number v-model="form.paperYear" :min="2024" :max="2035" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" placeholder="输入试卷说明" style="width: 320px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate">新增试卷</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="page-card">
      <el-table :data="papers">
        <el-table-column prop="title" label="试卷名称" />
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="paperYear" label="年份" width="100" />
        <el-table-column prop="questionCount" label="题数" width="100" />
        <el-table-column prop="totalScore" label="总分" width="100" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createPaper, fetchPapers } from '../../api/admin'

const papers = ref([])
const form = ref({
  title: '',
  description: '',
  paperYear: new Date().getFullYear()
})

const loadPapers = async () => {
  papers.value = await fetchPapers()
}

const handleCreate = async () => {
  await createPaper(form.value)
  ElMessage.success('试卷已创建')
  form.value = { title: '', description: '', paperYear: new Date().getFullYear() }
  await loadPapers()
}

onMounted(async () => {
  await loadPapers()
})
</script>
