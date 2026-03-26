import { defineStore } from 'pinia'
import { loginApi, meApi, registerApi } from '../api/auth'

const parseStoredUser = () => {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null')
  } catch (error) {
    localStorage.removeItem('user')
    return null
  }
}

const normalizeUser = (user) => {
  if (!user) return null
  const displayName = user.displayName || user.realName || user.username || '未登录'
  return {
    ...user,
    displayName,
    realName: user.realName || displayName,
    roles: Array.isArray(user.roles) ? user.roles : []
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: normalizeUser(parseStoredUser())
  }),
  getters: {
    displayName: (state) => state.user?.displayName || '未登录',
    isAdmin: (state) => state.user?.roles?.includes('ADMIN'),
    isStudent: (state) => state.user?.roles?.includes('STUDENT')
  },
  actions: {
    persist(token, user) {
      const normalizedUser = normalizeUser(user)
      this.token = token
      this.user = normalizedUser
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(normalizedUser))
    },
    async login(form) {
      const data = await loginApi(form)
      this.persist(data.token, data.user)
      return this.user
    },
    async register(form) {
      const data = await registerApi(form)
      this.persist(data.token, data.user)
      return this.user
    },
    async fetchMe() {
      if (!this.token) return null
      const user = await meApi()
      this.persist(this.token, user)
      return this.user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
