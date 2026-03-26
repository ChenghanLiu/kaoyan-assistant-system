<template>
  <div style="display:grid;gap:20px" v-if="detail.id">
    <div class="page-card">
      <div class="page-title">{{ detail.schoolName }}</div>
      <div class="page-subtitle">{{ detail.province }} · {{ detail.city }} · {{ detail.schoolLevel }}</div>
      <p style="line-height:1.8;margin-top:16px">{{ detail.description }}</p>
    </div>
    <div class="page-card">
      <div class="page-title">招生简章</div>
      <el-timeline style="margin-top: 20px">
        <el-timeline-item v-for="guide in detail.admissionGuides" :key="guide.id" :timestamp="`${guide.guideYear} 年`">
          <strong>{{ guide.title }}</strong>
          <div style="margin-top:6px;color:#64748b">{{ guide.majorName }}</div>
          <div style="margin-top:6px">{{ guide.content }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
    <div class="page-card">
      <div class="page-title">报录比</div>
      <el-table :data="detail.applicationRatios" style="margin-top: 16px">
        <el-table-column prop="majorName" label="专业" />
        <el-table-column prop="ratioYear" label="年份" width="100" />
        <el-table-column prop="applyCount" label="报考人数" />
        <el-table-column prop="admitCount" label="录取人数" />
        <el-table-column prop="ratioValue" label="报录比" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { fetchSchoolDetail } from '../../api/student'

const route = useRoute()
const detail = ref({ admissionGuides: [], applicationRatios: [] })

onMounted(async () => {
  detail.value = await fetchSchoolDetail(route.params.id)
})
</script>
