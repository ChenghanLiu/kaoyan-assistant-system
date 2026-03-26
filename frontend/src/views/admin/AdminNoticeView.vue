<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">系统公告管理</div>
        <div class="page-subtitle">维护站内公告</div>
      </div>
      <el-button type="primary" @click="openCreate">新增公告</el-button>
    </div>
    <el-table :data="list">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="targetRole" label="目标角色" width="120">
        <template #default="{ row }">{{ roleLabel[row.targetRole] || row.targetRole }}</template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="320" />
      <el-table-column prop="updatedAt" label="更新时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改公告' : '新增公告'" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" /></el-form-item>
        <el-form-item label="目标角色">
          <el-select v-model="form.targetRole" style="width:100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="管理员" value="ADMIN" />
            <el-option label="全部" value="ALL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createNotice, fetchNotices, updateNotice } from '../../api/admin'

const list = ref([])
const dialogVisible = ref(false)
const currentId = ref(null)
const isEdit = ref(false)
const form = reactive({ title: '', content: '', targetRole: 'STUDENT' })
const roleLabel = {
  ALL: '全部',
  STUDENT: '学生',
  ADMIN: '管理员'
}

const loadData = async () => {
  list.value = await fetchNotices()
}

const resetForm = () => {
  currentId.value = null
  isEdit.value = false
  Object.assign(form, { title: '', content: '', targetRole: 'STUDENT' })
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  currentId.value = row.id
  isEdit.value = true
  Object.assign(form, { title: row.title, content: row.content, targetRole: row.targetRole })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updateNotice(currentId.value, form)
    ElMessage.success('修改成功')
  } else {
    await createNotice(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  resetForm()
  await loadData()
}

onMounted(async () => {
  await loadData()
  resetForm()
})
</script>
