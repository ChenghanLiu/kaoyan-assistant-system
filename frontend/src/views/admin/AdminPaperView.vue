<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">试卷管理</div>
          <div class="page-subtitle">维护模拟测试试卷基础信息</div>
        </div>
      </div>
      <el-form :model="form" inline @submit.prevent="handleCreate">
        <el-form-item label="试卷名称" required>
          <el-input v-model="form.title" placeholder="输入试卷名称" clearable @keyup.enter="handleCreate" />
        </el-form-item>
        <el-form-item label="年份">
          <el-input-number v-model="form.paperYear" :min="2024" :max="2035" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" placeholder="输入试卷说明" clearable style="width: 320px" @keyup.enter="handleCreate" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="creating" @click="handleCreate">新增试卷</el-button>
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
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createPaper, deletePaper, fetchPapers } from '../../api/admin'

const papers = ref([])
const creating = ref(false)
const form = ref({
  title: '',
  description: '',
  paperYear: new Date().getFullYear()
})

const loadPapers = async () => {
  papers.value = await fetchPapers()
}

const handleCreate = async () => {
  const payload = {
    title: form.value.title?.trim() || '',
    description: form.value.description?.trim() || '',
    paperYear: form.value.paperYear
  }

  if (!payload.title) {
    ElMessage.warning('请输入试卷名称')
    return
  }

  creating.value = true
  try {
    await createPaper(payload)
    ElMessage.success('试卷已创建')
    form.value = { title: '', description: '', paperYear: new Date().getFullYear() }
    await loadPapers()
  } catch (error) {
    ElMessage.error(error.message || '新增试卷失败')
  } finally {
    creating.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该试卷吗？若存在考试记录或错题记录将禁止删除。', '提示', { type: 'warning' })
    await deletePaper(id)
    ElMessage.success('删除成功')
    await loadPapers()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(async () => {
  await loadPapers()
})
</script>
