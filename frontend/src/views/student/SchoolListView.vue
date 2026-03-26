<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">院校查询</div>
        <div class="page-subtitle">查看院校简介、招生简章与报录比</div>
      </div>
      <el-input v-model="keyword" placeholder="请输入院校名称" style="max-width: 280px" @input="loadData" />
    </div>
    <el-table :data="schools">
      <el-table-column prop="schoolName" label="院校名称" />
      <el-table-column prop="province" label="省份" width="120" />
      <el-table-column prop="city" label="城市" width="120" />
      <el-table-column prop="schoolType" label="类型" width="120" />
      <el-table-column prop="schoolLevel" label="层次" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/student/schools/${row.id}`)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchSchools } from '../../api/student'

const keyword = ref('')
const schools = ref([])

const loadData = async () => {
  schools.value = await fetchSchools(keyword.value)
}

onMounted(loadData)
</script>
