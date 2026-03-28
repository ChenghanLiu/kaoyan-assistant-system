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
      <el-table-column prop="degreeType" label="学位类型" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAdminMajor, deleteAdminMajor, fetchAdminMajors } from '../../api/admin'

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

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该专业吗？若有关联简章、报录比或政策资讯将禁止删除。', '提示', { type: 'warning' })
    await deleteAdminMajor(id)
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
