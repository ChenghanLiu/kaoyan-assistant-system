<template>
  <div class="layout-shell">
    <el-container style="min-height: 100vh">
      <el-aside width="220px" style="background: #0f172a; color: #fff">
        <div style="padding: 24px 20px; font-size: 22px; font-weight: 700">考研备考助手</div>
        <el-menu
          :default-active="$route.path"
          router
          background-color="#0f172a"
          text-color="#cbd5e1"
          active-text-color="#ffffff"
        >
          <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
            {{ item.label }}
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="background: rgba(255,255,255,0.85); backdrop-filter: blur(12px)">
          <div style="display:flex;justify-content:space-between;align-items:center;height:100%">
            <div>
              <div style="font-size: 20px; font-weight: 700">{{ title }}</div>
              <div style="font-size: 12px; color: #64748b">{{ subtitle }}</div>
            </div>
            <div style="display:flex;align-items:center;gap:12px">
              <el-tag type="primary">{{ authStore.displayName }}</el-tag>
              <el-tag>{{ authStore.user?.roles?.join(' / ') || '游客' }}</el-tag>
              <el-button @click="handleLogout">退出登录</el-button>
            </div>
          </div>
        </el-header>
        <el-main class="layout-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const props = defineProps({
  menus: { type: Array, default: () => [] },
  title: { type: String, default: '考研备考助手' },
  subtitle: { type: String, default: '一站式考研信息与学习平台' }
})

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const menus = computed(() => props.menus)

const handleLogout = () => {
  authStore.logout()
  router.replace('/login')
}
</script>
