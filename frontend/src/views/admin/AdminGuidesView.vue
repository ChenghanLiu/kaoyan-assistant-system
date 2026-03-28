<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">招生简章管理</div>
          <div class="page-subtitle">维护招生简章与报录比数据</div>
        </div>
        <div style="display:flex;gap:12px">
          <el-button @click="ratioDialogVisible = true">新增报录比</el-button>
          <el-button type="primary" @click="openGuideDialog()">新增简章</el-button>
        </div>
      </div>
      <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px;margin-top:20px">
        <el-select v-model="filters.schoolId" clearable placeholder="筛选院校" @change="handleSchoolChange">
          <el-option v-for="item in schools" :key="item.id" :label="item.schoolName" :value="item.id" />
        </el-select>
        <el-select v-model="filters.majorId" clearable placeholder="筛选专业" @change="loadData">
          <el-option v-for="item in filteredMajors" :key="item.id" :label="item.majorName" :value="item.id" />
        </el-select>
      </div>
      <el-table :data="guides" style="margin-top:20px">
        <el-table-column prop="title" label="简章标题" min-width="220" />
        <el-table-column prop="schoolName" label="院校" width="180" />
        <el-table-column prop="majorName" label="专业" width="180" />
        <el-table-column prop="guideYear" label="年份" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openGuideDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDeleteGuide(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="page-card">
      <div class="page-title">报录比管理</div>
      <el-table :data="ratios" style="margin-top:20px">
        <el-table-column prop="schoolName" label="院校" width="180" />
        <el-table-column prop="majorName" label="专业" width="180" />
        <el-table-column prop="ratioYear" label="年份" width="100" />
        <el-table-column prop="applyCount" label="报考人数" width="120" />
        <el-table-column prop="admitCount" label="录取人数" width="120" />
        <el-table-column prop="ratioValue" label="报录比" width="120" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openRatioDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDeleteRatio(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="guideDialogVisible" :title="editingGuideId ? '编辑招生简章' : '新增招生简章'" width="520px">
      <el-form :model="guideForm" label-position="top">
        <el-form-item label="院校">
          <el-select v-model="guideForm.schoolId" placeholder="选择院校" @change="handleGuideSchoolChange">
            <el-option v-for="item in schools" :key="item.id" :label="item.schoolName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="guideForm.majorId" clearable placeholder="选择专业">
            <el-option v-for="item in guideMajors" :key="item.id" :label="item.majorName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="guideForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="guideForm.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="年份"><el-input-number v-model="guideForm.guideYear" :min="2020" :max="2100" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="guideDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveGuide">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="ratioDialogVisible" :title="editingRatioId ? '编辑报录比' : '新增报录比'" width="520px">
      <el-form :model="ratioForm" label-position="top">
        <el-form-item label="院校">
          <el-select v-model="ratioForm.schoolId" placeholder="选择院校" @change="handleRatioSchoolChange">
            <el-option v-for="item in schools" :key="item.id" :label="item.schoolName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="ratioForm.majorId" placeholder="选择专业">
            <el-option v-for="item in ratioMajors" :key="item.id" :label="item.majorName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份"><el-input-number v-model="ratioForm.ratioYear" :min="2020" :max="2100" style="width:100%" /></el-form-item>
        <el-form-item label="报考人数"><el-input-number v-model="ratioForm.applyCount" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="录取人数"><el-input-number v-model="ratioForm.admitCount" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="报录比"><el-input v-model="ratioForm.ratioValue" placeholder="如 5:1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ratioDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRatio">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchMajors, fetchSchools } from '../../api/student'
import {
  createGuide,
  createRatio,
  deleteGuide,
  deleteRatio,
  fetchGuides,
  fetchRatios,
  updateGuide,
  updateRatio
} from '../../api/admin'

const guides = ref([])
const ratios = ref([])
const schools = ref([])
const majors = ref([])
const guideDialogVisible = ref(false)
const ratioDialogVisible = ref(false)
const editingGuideId = ref(null)
const editingRatioId = ref(null)
const filters = reactive({ schoolId: undefined, majorId: undefined })
const guideForm = reactive({ schoolId: undefined, majorId: undefined, title: '', content: '', guideYear: 2026 })
const ratioForm = reactive({ schoolId: undefined, majorId: undefined, ratioYear: 2026, applyCount: 100, admitCount: 20, ratioValue: '' })

const filteredMajors = computed(() => {
  if (!filters.schoolId) {
    return majors.value
  }
  return majors.value.filter((item) => item.schoolId === filters.schoolId)
})

const guideMajors = computed(() => majors.value.filter((item) => item.schoolId === guideForm.schoolId))
const ratioMajors = computed(() => majors.value.filter((item) => item.schoolId === ratioForm.schoolId))

const loadData = async () => {
  const params = { schoolId: filters.schoolId, majorId: filters.majorId }
  const [guideData, ratioData] = await Promise.all([fetchGuides(params), fetchRatios(params)])
  guides.value = guideData
  ratios.value = ratioData
}

const resetGuideForm = () => {
  editingGuideId.value = null
  Object.assign(guideForm, { schoolId: undefined, majorId: undefined, title: '', content: '', guideYear: 2026 })
}

const resetRatioForm = () => {
  editingRatioId.value = null
  Object.assign(ratioForm, { schoolId: undefined, majorId: undefined, ratioYear: 2026, applyCount: 100, admitCount: 20, ratioValue: '' })
}

const openGuideDialog = (row = null) => {
  if (!row) {
    resetGuideForm()
  } else {
    editingGuideId.value = row.id
    Object.assign(guideForm, {
      schoolId: row.schoolId,
      majorId: row.majorId,
      title: row.title,
      content: row.content,
      guideYear: row.guideYear
    })
  }
  guideDialogVisible.value = true
}

const openRatioDialog = (row = null) => {
  if (!row) {
    resetRatioForm()
  } else {
    editingRatioId.value = row.id
    Object.assign(ratioForm, {
      schoolId: row.schoolId,
      majorId: row.majorId,
      ratioYear: row.ratioYear,
      applyCount: row.applyCount,
      admitCount: row.admitCount,
      ratioValue: row.ratioValue
    })
  }
  ratioDialogVisible.value = true
}

const handleSaveGuide = async () => {
  if (editingGuideId.value) {
    await updateGuide(editingGuideId.value, guideForm)
    ElMessage.success('更新成功')
  } else {
    await createGuide(guideForm)
    ElMessage.success('新增成功')
  }
  guideDialogVisible.value = false
  await loadData()
}

const handleSaveRatio = async () => {
  if (editingRatioId.value) {
    await updateRatio(editingRatioId.value, ratioForm)
    ElMessage.success('更新成功')
  } else {
    await createRatio(ratioForm)
    ElMessage.success('新增成功')
  }
  ratioDialogVisible.value = false
  await loadData()
}

const handleDeleteGuide = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该招生简章吗？', '提示', { type: 'warning' })
    await deleteGuide(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleDeleteRatio = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该报录比吗？', '提示', { type: 'warning' })
    await deleteRatio(id)
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

const handleGuideSchoolChange = () => {
  if (guideForm.majorId && !guideMajors.value.some((item) => item.id === guideForm.majorId)) {
    guideForm.majorId = undefined
  }
}

const handleRatioSchoolChange = () => {
  if (ratioForm.majorId && !ratioMajors.value.some((item) => item.id === ratioForm.majorId)) {
    ratioForm.majorId = undefined
  }
}

onMounted(async () => {
  const [schoolData, majorData] = await Promise.all([fetchSchools(''), fetchMajors()])
  schools.value = schoolData
  majors.value = majorData
  await loadData()
})
</script>
