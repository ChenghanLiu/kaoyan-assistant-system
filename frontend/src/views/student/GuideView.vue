<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">院校信息服务</div>
          <div class="page-subtitle">查看招生简章、报录比和政策资讯，支持按院校/专业筛选</div>
        </div>
      </div>
      <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px;margin-top:20px">
        <el-select v-model="filters.schoolId" clearable placeholder="选择院校" @change="handleSchoolChange">
          <el-option v-for="item in schools" :key="item.id" :label="item.schoolName" :value="item.id" />
        </el-select>
        <el-select v-model="filters.majorId" clearable placeholder="选择专业" @change="loadData">
          <el-option v-for="item in filteredMajors" :key="item.id" :label="item.majorName" :value="item.id" />
        </el-select>
        <el-button @click="resetFilters">重置筛选</el-button>
      </div>
    </div>

    <div class="page-card">
      <div class="page-title">招生简章</div>
      <el-collapse style="margin-top:16px">
        <el-collapse-item v-for="item in admissions" :key="item.id" :title="`${item.guideYear}年 · ${item.schoolName} · ${item.majorName}`" :name="item.id">
          <div style="font-weight:600;margin-bottom:8px">{{ item.title }}</div>
          <div style="line-height:1.8">{{ item.content }}</div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <div class="page-card">
      <div class="page-title">报录比</div>
      <el-table :data="ratios" style="margin-top:16px">
        <el-table-column prop="schoolName" label="院校" min-width="180" />
        <el-table-column prop="majorName" label="专业" min-width="180" />
        <el-table-column prop="ratioYear" label="年份" width="100" />
        <el-table-column prop="applyCount" label="报考人数" width="120" />
        <el-table-column prop="admitCount" label="录取人数" width="120" />
        <el-table-column prop="ratioValue" label="报录比" width="120" />
      </el-table>
    </div>

    <div class="page-card">
      <div class="page-title">政策资讯</div>
      <el-timeline style="margin-top:20px">
        <el-timeline-item v-for="item in policies" :key="item.id" :timestamp="item.publishedDate">
          <strong>{{ item.title }}</strong>
          <div style="margin-top:6px;color:#64748b">{{ item.schoolName }} / {{ item.majorName }} / {{ item.sourceName || '官方渠道' }}</div>
          <div style="margin-top:6px;line-height:1.8">{{ item.content }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { fetchAdmissions, fetchMajors, fetchPolicies, fetchRatios, fetchSchools } from '../../api/student'

const schools = ref([])
const majors = ref([])
const admissions = ref([])
const ratios = ref([])
const policies = ref([])
const filters = reactive({
  schoolId: undefined,
  majorId: undefined
})

const filteredMajors = computed(() => {
  if (!filters.schoolId) {
    return majors.value
  }
  return majors.value.filter((item) => item.schoolId === filters.schoolId)
})

const buildParams = () => ({
  schoolId: filters.schoolId,
  majorId: filters.majorId
})

const loadData = async () => {
  const params = buildParams()
  const [admissionData, ratioData, policyData] = await Promise.all([
    fetchAdmissions(params),
    fetchRatios(params),
    fetchPolicies(params)
  ])
  admissions.value = admissionData
  ratios.value = ratioData
  policies.value = policyData
}

const handleSchoolChange = () => {
  if (filters.majorId && !filteredMajors.value.some((item) => item.id === filters.majorId)) {
    filters.majorId = undefined
  }
  loadData()
}

const resetFilters = () => {
  filters.schoolId = undefined
  filters.majorId = undefined
  loadData()
}

onMounted(async () => {
  const [schoolData, majorData] = await Promise.all([fetchSchools(''), fetchMajors()])
  schools.value = schoolData
  majors.value = majorData
  await loadData()
})
</script>
