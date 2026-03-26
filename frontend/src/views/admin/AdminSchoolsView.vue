<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">院校管理</div>
        <div class="page-subtitle">新增和查看院校信息</div>
      </div>
      <el-button type="primary" @click="dialogVisible = true">新增院校</el-button>
    </div>
    <el-table :data="schools">
      <el-table-column prop="schoolName" label="院校名称" />
      <el-table-column prop="province" label="省份" />
      <el-table-column prop="city" label="城市" />
      <el-table-column prop="schoolType" label="类型" />
      <el-table-column prop="schoolLevel" label="层次" />
    </el-table>
    <el-dialog v-model="dialogVisible" title="新增院校" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="院校名称"><el-input v-model="form.schoolName" /></el-form-item>
        <el-form-item label="省份"><el-input v-model="form.province" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="form.schoolType" /></el-form-item>
        <el-form-item label="层次"><el-input v-model="form.schoolLevel" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" /></el-form-item>
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
import { createAdminSchool, fetchAdminSchools } from '../../api/admin'

const schools = ref([])
const dialogVisible = ref(false)
const form = reactive({ schoolName: '', province: '', city: '', schoolType: '', schoolLevel: '', description: '' })

const loadData = async () => {
  schools.value = await fetchAdminSchools()
}

const handleCreate = async () => {
  await createAdminSchool(form)
  ElMessage.success('新增成功')
  dialogVisible.value = false
  Object.assign(form, { schoolName: '', province: '', city: '', schoolType: '', schoolLevel: '', description: '' })
  loadData()
}

onMounted(loadData)
</script>
