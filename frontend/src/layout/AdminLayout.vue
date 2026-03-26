<template>
  <AppLayout :menus="menus" title="运营管理台" subtitle="内容维护、审核与系统配置" />
</template>

<script setup>
import AppLayout from './AppLayout.vue'

const menus = [
  { path: '/admin/dashboard', label: '后台首页' },
  { path: '/admin/users', label: '用户管理' },
  { path: '/admin/schools', label: '院校管理' },
  { path: '/admin/majors', label: '专业管理' },
  { path: '/admin/guides', label: '招生简章管理' },
  { path: '/admin/material-reviews', label: '资料审核' },
  { path: '/admin/policy-news', label: '政策资讯管理' },
  { path: '/admin/papers', label: '试卷管理' },
  { path: '/admin/questions', label: '题目管理' },
  { path: '/admin/notices', label: '系统公告管理' },
  { path: '/admin/configs', label: '系统配置管理' },
  { path: '/admin/logs', label: '操作日志查询' }
]
</script>
