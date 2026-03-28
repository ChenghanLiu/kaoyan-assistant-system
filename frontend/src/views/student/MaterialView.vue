<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">资料列表</div>
        <div class="page-subtitle">浏览已审核通过的资料</div>
      </div>
    </div>
    <el-table v-loading="listLoading" :data="materials">
      <el-table-column prop="title" label="资料标题" />
      <el-table-column prop="categoryName" label="分类" width="140" />
      <el-table-column prop="description" label="说明" />
      <el-table-column prop="downloadCount" label="下载次数" width="120" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button link type="primary" :loading="downloadingId === row.id" @click="handleDownload(row)">下载资料</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { downloadMaterial, fetchMaterials } from '../../api/student'

const materials = ref([])
const listLoading = ref(false)
const downloadingId = ref(null)

onMounted(async () => {
  listLoading.value = true
  try {
    materials.value = await fetchMaterials()
  } catch (error) {
    console.error('加载资料列表失败', error)
    ElMessage.error(error.message || '加载资料失败')
  } finally {
    listLoading.value = false
  }
})

const handleDownload = async (material) => {
  downloadingId.value = material.id
  try {
    const response = await downloadMaterial(material.id)
    const blobUrl = window.URL.createObjectURL(response.data)
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = material.fileName
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(blobUrl)
    ElMessage.success('下载开始')
  } catch (error) {
    console.error(`下载资料失败: materialId=${material.id}`, error)
    ElMessage.error(error.message || '下载失败')
  } finally {
    downloadingId.value = null
  }
}
</script>
