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
  const targetSchool = user.targetSchool ?? user.school ?? ''
  const targetMajor = user.targetMajor ?? user.major ?? ''
  return {
    ...user,
    displayName,
    realName: user.realName || displayName,
    targetSchool,
    targetMajor,
    school: targetSchool,
    major: targetMajor,
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
    isStudent: (state) => state.user?.roles?.includes('STUDENT'),
    shouldHydrateProfile: (state) => {
      if (!state.user) return true
      return !state.user.displayName || state.user.targetSchool === undefined || state.user.targetMajor === undefined
    }
  },
  actions: {
    persist(token, user) {
      const normalizedUser = normalizeUser(user)
      this.token = token
      this.user = normalizedUser
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(normalizedUser))
    },
    setUser(user) {
      if (!this.token) return
      this.persist(this.token, user)
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
