<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">专业管理</div>
        <div class="page-subtitle">维护专业编码与类别</div>
      </div>
      <el-button type="primary" @click="dialogVisible = true">新增专业</el-button>
    </div>
    <el-table :data="majors">
      <el-table-column prop="majorName" label="专业名称" />
      <el-table-column prop="majorCode" label="专业代码" />
      <el-table-column prop="categoryName" label="学科门类" />
    </el-table>
    <el-dialog v-model="dialogVisible" title="新增专业" width="480px">
      <el-form :model="form" label-position="top">
        <el-form-item label="专业名称"><el-input v-model="form.majorName" /></el-form-item>
        <el-form-item label="专业代码"><el-input v-model="form.majorCode" /></el-form-item>
        <el-form-item label="学科门类"><el-input v-model="form.categoryName" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createAdminMajor, fetchAdminMajors } from '../../api/admin'

const majors = ref([])
const dialogVisible = ref(false)
const form = reactive({ majorName: '', majorCode: '', categoryName: '', description: '' })

const loadData = async () => {
  majors.value = await fetchAdminMajors()
}

const handleCreate = async () => {
  await createAdminMajor(form)
  ElMessage.success('新增成功')
  dialogVisible.value = false
  Object.assign(form, { majorName: '', majorCode: '', categoryName: '', description: '' })
  loadData()
}

onMounted(loadData)
</script>
