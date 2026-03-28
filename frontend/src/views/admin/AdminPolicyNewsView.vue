<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">政策资讯管理</div>
        <div class="page-subtitle">维护政策提醒与资讯内容</div>
      </div>
      <el-button type="primary" @click="openDialog()">新增资讯</el-button>
    </div>
    <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px;margin-top:20px">
      <el-select v-model="filters.schoolId" clearable placeholder="筛选院校" @change="handleSchoolChange">
        <el-option v-for="item in schools" :key="item.id" :label="item.schoolName" :value="item.id" />
      </el-select>
      <el-select v-model="filters.majorId" clearable placeholder="筛选专业" @change="loadData">
        <el-option v-for="item in filteredMajors" :key="item.id" :label="item.majorName" :value="item.id" />
      </el-select>
    </div>
    <el-table :data="news" style="margin-top:20px">
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="schoolName" label="院校" width="160" />
      <el-table-column prop="majorName" label="专业" width="160" />
      <el-table-column prop="sourceName" label="来源" width="140" />
      <el-table-column prop="publishedDate" label="发布日期" width="140" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑政策资讯' : '新增政策资讯'" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="院校">
          <el-select v-model="form.schoolId" clearable placeholder="选择院校" @change="handleFormSchoolChange">
            <el-option v-for="item in schools" :key="item.id" :label="item.schoolName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="form.majorId" clearable placeholder="选择专业">
            <el-option v-for="item in formMajors" :key="item.id" :label="item.majorName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="来源"><el-input v-model="form.sourceName" /></el-form-item>
        <el-form-item label="发布日期"><el-date-picker v-model="form.publishedDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchMajors, fetchSchools } from '../../api/student'
import { createPolicyNews, deletePolicyNews, fetchPolicyNews, updatePolicyNews } from '../../api/admin'

const news = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const schools = ref([])
const majors = ref([])
const filters = reactive({ schoolId: undefined, majorId: undefined })
const form = reactive({ schoolId: undefined, majorId: undefined, title: '', content: '', sourceName: '', publishedDate: '' })

const filteredMajors = computed(() => {
  if (!filters.schoolId) {
    return majors.value
  }
  return majors.value.filter((item) => item.schoolId === filters.schoolId)
})

const formMajors = computed(() => {
  if (!form.schoolId) {
    return majors.value
  }
  return majors.value.filter((item) => item.schoolId === form.schoolId)
})

const loadData = async () => {
  news.value = await fetchPolicyNews({ schoolId: filters.schoolId, majorId: filters.majorId })
}

const resetForm = () => {
  editingId.value = null
  Object.assign(form, { schoolId: undefined, majorId: undefined, title: '', content: '', sourceName: '', publishedDate: '' })
}

const openDialog = (row = null) => {
  if (!row) {
    resetForm()
  } else {
    editingId.value = row.id
    Object.assign(form, {
      schoolId: row.schoolId,
      majorId: row.majorId,
      title: row.title,
      content: row.content,
      sourceName: row.sourceName,
      publishedDate: row.publishedDate
    })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (editingId.value) {
    await updatePolicyNews(editingId.value, form)
    ElMessage.success('更新成功')
  } else {
    await createPolicyNews(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadData()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该资讯吗？', '提示', { type: 'warning' })
    await deletePolicyNews(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleSchoolChange = () => {
  if (filters.majorId && !filteredMajors.value.some((item) => item.id === filters.majorId)) {
    filters.majorId = undefined
  }
  loadData()
}

const handleFormSchoolChange = () => {
  if (form.majorId && !formMajors.value.some((item) => item.id === form.majorId)) {
    form.majorId = undefined
  }
}

onMounted(async () => {
  const [schoolData, majorData] = await Promise.all([fetchSchools(''), fetchMajors()])
  schools.value = schoolData
  majors.value = majorData
  await loadData()
})
</script>
