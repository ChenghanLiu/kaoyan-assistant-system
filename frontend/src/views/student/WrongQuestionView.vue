<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <div class="page-title">错题本</div>
        <div class="page-subtitle">自动记录错题与解析</div>
      </div>
    </div>
    <el-collapse>
      <el-collapse-item v-for="item in list" :key="item.wrongQuestionId" :title="item.questionStem" :name="item.wrongQuestionId">
        <div>来源试卷：{{ item.paperTitle }}</div>
        <div>题型：{{ questionTypeText(item.questionType) }}</div>
        <div style="margin-top:8px">
          <div v-for="option in item.options" :key="option.id">{{ option.optionLabel }}. {{ option.optionContent }}</div>
        </div>
        <div style="margin-top:8px">你的答案：{{ formatAnswers(item.latestAnswer) }}</div>
        <div>正确答案：{{ formatAnswers(item.correctAnswer) }}</div>
        <div style="margin-top:8px">解析：{{ item.analysisText }}</div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchWrongQuestions } from '../../api/student'

const list = ref([])

const translateAnswer = (value) => {
  if (value === 'TRUE') return '正确'
  if (value === 'FALSE') return '错误'
  return value
}

const questionTypeText = (type) => {
  if (type === 'SINGLE') return '单选题'
  if (type === 'MULTIPLE') return '多选题'
  if (type === 'JUDGE') return '判断题'
  return type
}

const formatAnswers = (answers) => {
  if (!answers?.length) return '未作答'
  return answers.map(translateAnswer).join('、')
}

onMounted(async () => {
  list.value = await fetchWrongQuestions()
})
</script>
