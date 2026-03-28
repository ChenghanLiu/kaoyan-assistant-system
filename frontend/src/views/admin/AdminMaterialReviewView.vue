<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">资料审核页</div>
          <div class="page-subtitle">对学生上传资料进行审核，并支持管理员删除资料</div>
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

    <div class="page-card">
      <div class="page-title">资料管理</div>
      <div class="page-subtitle">展示全部资料并支持删除，删除时同步清理本地文件</div>
      <el-table :data="allMaterials" style="margin-top:20px">
        <el-table-column prop="title" label="资料标题" />
        <el-table-column prop="categoryName" label="分类" width="140" />
        <el-table-column prop="reviewStatus" label="审核状态" width="140" />
        <el-table-column prop="username" label="上传者" width="140" />
        <el-table-column prop="fileName" label="文件名" />
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
import { deleteMaterial, fetchAdminMaterials, fetchMaterialReviews, reviewMaterial } from '../../api/admin'

const list = ref([])
const allMaterials = ref([])

const loadData = async () => {
  const [reviewData, materialData] = await Promise.all([fetchMaterialReviews(), fetchAdminMaterials()])
  list.value = reviewData
  allMaterials.value = materialData
}

const handleReview = async (id, status) => {
  await reviewMaterial(id, status)
  ElMessage.success('审核状态已更新')
  await loadData()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该资料吗？删除后数据库记录和本地文件都会被移除。', '提示', { type: 'warning' })
    await deleteMaterial(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(loadData)
</script>
