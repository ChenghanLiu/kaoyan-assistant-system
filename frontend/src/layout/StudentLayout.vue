<template>
  <AppLayout :menus="menus" title="学习中心" subtitle="考研信息、资料与练习的一站式入口" />
</template>

<script setup>
import AppLayout from './AppLayout.vue'

const menus = [
  { path: '/student/home', label: '首页' },
  { path: '/student/schools', label: '院校查询' },
  { path: '/student/guides', label: '招生简章' },
  { path: '/student/plans', label: '学习计划' },
  { path: '/student/tasks', label: '学习任务' },
  { path: '/student/materials', label: '资料列表' },
  { path: '/student/my-uploads', label: '我的上传' },
  { path: '/student/posts', label: '讨论区' },
  { path: '/student/exams', label: '模拟测试' },
  { path: '/student/results', label: '成绩结果' },
  { path: '/student/wrong-questions', label: '错题本' },
  { path: '/student/profile', label: '个人中心' }
]
</script>
