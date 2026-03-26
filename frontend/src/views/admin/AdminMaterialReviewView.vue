<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">资料审核页</div>
        <div class="page-subtitle">对学生上传资料进行审核</div>
      </div>
    </div>
    <el-table :data="list">
      <el-table-column prop="title" label="资料标题" />
      <el-table-column prop="categoryName" label="分类" width="140" />
      <el-table-column prop="fileName" label="文件名" />
      <el-table-column prop="reviewStatus" label="当前状态" />
      <el-table-column prop="username" label="上传者" width="140" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="handleReview(row.id, 'APPROVED')">通过</el-button>
          <el-button size="small" type="danger" @click="handleReview(row.id, 'REJECTED')">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchMaterialReviews, reviewMaterial } from '../../api/admin'

const list = ref([])

const loadData = async () => {
  list.value = await fetchMaterialReviews()
}

const handleReview = async (id, status) => {
  await reviewMaterial(id, status)
  ElMessage.success('审核状态已更新')
  loadData()
}

onMounted(loadData)
</script>
