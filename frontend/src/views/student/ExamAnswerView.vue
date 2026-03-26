<template>
  <div style="display:grid;gap:20px" v-if="paper">
    <div class="page-card">
      <div class="page-title">{{ paper.title }}</div>
      <div class="page-subtitle">{{ paper.description }}</div>
      <div style="margin-top: 8px; color: var(--color-text-secondary)">
        年份：{{ paper.paperYear }} ｜ 题数：{{ paper.questionCount }} ｜ 总分：{{ paper.totalScore }}
      </div>
    </div>
    <div v-for="(question, index) in questions" :key="question.id" class="page-card">
      <div style="font-weight:700">{{ index + 1 }}. {{ question.questionStem }}</div>
      <div style="margin-top: 8px; color: var(--color-text-secondary)">
        题型：{{ questionTypeText(question.questionType) }} ｜ 分值：{{ question.score }}
      </div>
      <div style="margin-top: 14px">
        <el-checkbox-group
          v-if="question.questionType === 'MULTIPLE'"
          v-model="answers[question.id]"
        >
          <div v-for="option in question.options" :key="option.id" style="margin-bottom: 10px">
            <el-checkbox :label="option.optionLabel">
              {{ option.optionLabel }}. {{ option.optionContent }}
            </el-checkbox>
          </div>
        </el-checkbox-group>
        <el-radio-group
          v-else
          :model-value="singleAnswer(question.id)"
          @update:model-value="updateSingleAnswer(question.id, $event)"
        >
          <div v-for="option in question.options" :key="option.id" style="margin-bottom: 10px">
            <el-radio :label="option.optionLabel">
              {{ option.optionLabel }}. {{ option.optionContent }}
            </el-radio>
          </div>
        </el-radio-group>
      </div>
    </div>
    <div class="page-card">
      <el-button type="primary" @click="handleSubmit">提交试卷</el-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { fetchPaperDetail, fetchPaperQuestions, submitPaper } from '../../api/student'

const route = useRoute()
const router = useRouter()
const paper = ref(null)
const questions = ref([])
const answers = reactive({})

onMounted(async () => {
  paper.value = await fetchPaperDetail(route.params.id)
  questions.value = await fetchPaperQuestions(route.params.id)
  questions.value.forEach((question) => {
    answers[question.id] = question.questionType === 'MULTIPLE' ? [] : []
  })
})

const questionTypeText = (type) => {
  if (type === 'SINGLE') return '单选题'
  if (type === 'MULTIPLE') return '多选题'
  if (type === 'JUDGE') return '判断题'
  return type
}

const singleAnswer = (questionId) => answers[questionId]?.[0] || ''

const updateSingleAnswer = (questionId, value) => {
  answers[questionId] = value ? [value] : []
}

const handleSubmit = async () => {
  const payload = {
    paperId: Number(route.params.id),
    answers: questions.value.map((item) => ({
      questionId: item.id,
      selectedOptions: answers[item.id] || []
    }))
  }
  const result = await submitPaper(payload)
  ElMessage.success(`提交成功，得分 ${result.score}`)
  router.push(`/student/results?recordId=${result.recordId}`)
}
</script>
