<template>
  <div style="display:grid;gap:20px">
    <div class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">题目管理</div>
          <div class="page-subtitle">最小版本支持题目、选项、答案与解析维护</div>
        </div>
      </div>
      <el-form :model="questionForm" label-width="90px" @submit.prevent>
        <el-form-item label="所属试卷">
          <el-select v-model="questionForm.paperId" placeholder="选择试卷" style="width: 320px">
            <el-option v-for="paper in papers" :key="paper.id" :label="paper.title" :value="paper.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="questionForm.questionType" style="width: 180px">
            <el-option label="单选题" value="SINGLE" />
            <el-option label="多选题" value="MULTIPLE" />
            <el-option label="判断题" value="JUDGE" />
          </el-select>
        </el-form-item>
        <el-form-item label="题干">
          <el-input v-model="questionForm.questionStem" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="正确答案">
          <el-input v-model="questionForm.correctAnswer" placeholder="如 A 或 A,C 或 TRUE" />
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="questionForm.sortOrder" :min="1" :max="999" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="questionForm.analysisText" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreateQuestion">新增题目</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="page-card">
      <div style="display:flex;gap:12px;align-items:center;margin-bottom:16px">
        <span>按试卷筛选</span>
        <el-select v-model="selectedPaperId" clearable placeholder="全部试卷" style="width: 320px" @change="loadQuestions">
          <el-option v-for="paper in papers" :key="paper.id" :label="paper.title" :value="paper.id" />
        </el-select>
      </div>
      <el-table :data="questions">
        <el-table-column prop="paperTitle" label="试卷" width="180" />
        <el-table-column prop="questionType" label="题型" width="100">
          <template #default="{ row }">{{ questionTypeText(row.questionType) }}</template>
        </el-table-column>
        <el-table-column prop="questionStem" label="题干" min-width="320" />
        <el-table-column prop="correctAnswer" label="答案" width="120" />
        <el-table-column prop="score" label="分值" width="90" />
        <el-table-column prop="sortOrder" label="排序" width="90" />
        <el-table-column label="选项管理" width="140">
          <template #default="{ row }">
            <el-button type="primary" link @click="openOptionPanel(row)">查看/新增</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDeleteQuestion(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="activeQuestion" class="page-card">
      <div class="page-title">选项管理</div>
      <div class="page-subtitle" style="margin-bottom: 16px">{{ activeQuestion.questionStem }}</div>
      <el-form :model="optionForm" inline @submit.prevent>
        <el-form-item label="标签">
          <el-input v-model="optionForm.optionLabel" placeholder="如 A / B / TRUE" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="optionForm.optionContent" placeholder="输入选项内容" style="width: 360px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreateOption">新增选项</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="options" style="margin-top: 12px">
        <el-table-column prop="optionLabel" label="标签" width="120" />
        <el-table-column prop="optionContent" label="内容" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDeleteOption(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createQuestion,
  createQuestionOption,
  deleteQuestion,
  deleteQuestionOption,
  fetchPapers,
  fetchQuestionOptions,
  fetchQuestions
} from '../../api/admin'

const papers = ref([])
const questions = ref([])
const options = ref([])
const selectedPaperId = ref()
const activeQuestion = ref(null)
const questionForm = ref({
  paperId: undefined,
  questionType: 'SINGLE',
  questionStem: '',
  correctAnswer: '',
  score: 2,
  sortOrder: null,
  analysisText: ''
})
const optionForm = ref({
  optionLabel: '',
  optionContent: ''
})

const questionTypeText = (type) => {
  if (type === 'SINGLE') return '单选题'
  if (type === 'MULTIPLE') return '多选题'
  if (type === 'JUDGE') return '判断题'
  return type
}

const loadPapers = async () => {
  papers.value = await fetchPapers()
  if (!questionForm.value.paperId && papers.value.length > 0) {
    questionForm.value.paperId = papers.value[0].id
  }
}

const loadQuestions = async () => {
  questions.value = await fetchQuestions(selectedPaperId.value)
}

const openOptionPanel = async (question) => {
  activeQuestion.value = question
  options.value = await fetchQuestionOptions(question.id)
}

const handleCreateQuestion = async () => {
  await createQuestion(questionForm.value)
  ElMessage.success('题目已创建')
  questionForm.value = {
    ...questionForm.value,
    questionStem: '',
    correctAnswer: '',
    score: 2,
    sortOrder: null,
    analysisText: ''
  }
  await loadPapers()
  await loadQuestions()
}

const handleCreateOption = async () => {
  if (!activeQuestion.value) return
  await createQuestionOption(activeQuestion.value.id, optionForm.value)
  ElMessage.success('选项已创建')
  optionForm.value = { optionLabel: '', optionContent: '' }
  await openOptionPanel(activeQuestion.value)
}

const handleDeleteQuestion = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该题目吗？若存在答题记录或错题记录将禁止删除。', '提示', { type: 'warning' })
    await deleteQuestion(id)
    ElMessage.success('删除成功')
    if (activeQuestion.value?.id === id) {
      activeQuestion.value = null
      options.value = []
    }
    await loadPapers()
    await loadQuestions()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleDeleteOption = async (optionId) => {
  if (!activeQuestion.value) return
  try {
    await ElMessageBox.confirm('确认删除该选项吗？若已产生答题记录或该选项为正确答案将禁止删除。', '提示', { type: 'warning' })
    await deleteQuestionOption(activeQuestion.value.id, optionId)
    ElMessage.success('删除成功')
    await openOptionPanel(activeQuestion.value)
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(async () => {
  await loadPapers()
  await loadQuestions()
})
</script>
