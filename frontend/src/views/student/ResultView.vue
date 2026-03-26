<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">成绩结果</div>
          <div class="page-subtitle">记录每次模拟测试的成绩</div>
        </div>
      </div>
      <el-table :data="records">
        <el-table-column prop="paperTitle" label="试卷" />
        <el-table-column prop="score" label="得分" width="120" />
        <el-table-column prop="totalScore" label="总分" width="120" />
        <el-table-column prop="correctCount" label="答对题数" />
        <el-table-column prop="totalCount" label="总题数" />
        <el-table-column prop="submittedAt" label="提交时间" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="loadDetail(row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="detail" class="page-card">
      <div class="page-title">{{ detail.paperTitle }}</div>
      <div class="page-subtitle">
        得分 {{ detail.score }}/{{ detail.totalScore }}，答对 {{ detail.correctCount }}/{{ detail.totalCount }}
      </div>
      <div
        v-for="(answer, index) in detail.answers"
        :key="answer.questionId"
        style="padding:16px 0;border-top:1px solid rgba(15, 23, 42, 0.08)"
      >
        <div style="font-weight:700">{{ index + 1 }}. {{ answer.questionStem }}</div>
        <div style="margin-top:8px">你的答案：{{ formatAnswers(answer.selectedOptions) }}</div>
        <div>正确答案：{{ formatAnswers(answer.correctOptions) }}</div>
        <div>得分：{{ answer.awardedScore }}/{{ answer.score }}</div>
        <div style="margin-top:8px">解析：{{ answer.analysisText || '暂无解析' }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { fetchExamRecordDetail, fetchExamRecords } from '../../api/student'

const route = useRoute()
const records = ref([])
const detail = ref(null)

const translateAnswer = (value) => {
  if (value === 'TRUE') return '正确'
  if (value === 'FALSE') return '错误'
  return value
}

const formatAnswers = (answers) => {
  if (!answers?.length) return '未作答'
  return answers.map(translateAnswer).join('、')
}

const loadDetail = async (id) => {
  detail.value = await fetchExamRecordDetail(id)
}

onMounted(async () => {
  records.value = await fetchExamRecords()
  if (route.query.recordId) {
    await loadDetail(Number(route.query.recordId))
  } else if (records.value.length > 0) {
    await loadDetail(records.value[0].id)
  }
})
</script>
