<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">我的上传</div>
          <div class="page-subtitle">上传本地资料并查看审核状态</div>
        </div>
      </div>
      <el-form label-position="top">
        <el-form-item label="资料标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="资料说明"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="资料分类">
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option v-for="item in categories" :key="item.id" :value="item.id" :label="item.categoryName" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件">
          <input type="file" @change="handleFileChange" />
        </el-form-item>
        <el-button type="primary" @click="handleUpload">上传资料</el-button>
      </el-form>
    </div>
    <div class="page-card">
      <div class="page-title">上传记录</div>
      <el-table :data="list" style="margin-top: 16px">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="categoryName" label="分类" width="140" />
        <el-table-column prop="reviewStatus" label="审核状态" width="120" />
        <el-table-column prop="reviewComment" label="审核说明" />
        <el-table-column prop="fileName" label="文件名" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { downloadMaterial, fetchMaterialCategories, fetchMyMaterials, uploadMaterial } from '../../api/student'

const list = ref([])
const categories = ref([])
const fileRef = ref(null)
const form = reactive({ categoryId: null, title: '', description: '' })

const loadData = async () => {
  ;[list.value, categories.value] = await Promise.all([fetchMyMaterials(), fetchMaterialCategories()])
  if (!form.categoryId && categories.value.length > 0) {
    form.categoryId = categories.value[0].id
  }
}

const handleFileChange = (event) => {
  fileRef.value = event.target.files[0]
}

const handleUpload = async () => {
  if (!fileRef.value) {
    ElMessage.warning('请选择文件')
    return
  }
  const data = new FormData()
  data.append('categoryId', form.categoryId)
  data.append('title', form.title)
  data.append('description', form.description)
  data.append('file', fileRef.value)
  await uploadMaterial(data)
  ElMessage.success('上传成功')
  Object.assign(form, { categoryId: categories.value[0]?.id ?? null, title: '', description: '' })
  fileRef.value = null
  loadData()
}

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

onMounted(loadData)
</script>
