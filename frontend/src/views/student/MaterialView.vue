<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">资料列表</div>
        <div class="page-subtitle">浏览已审核通过的资料</div>
      </div>
    </div>
    <el-table :data="materials">
      <el-table-column prop="title" label="资料标题" />
      <el-table-column prop="categoryName" label="分类" width="140" />
      <el-table-column prop="description" label="说明" />
      <el-table-column prop="downloadCount" label="下载次数" width="120" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleDownload(row)">下载资料</el-button>
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

onMounted(async () => {
  materials.value = await fetchMaterials()
})

const handleDownload = async (material) => {
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
}
</script>
