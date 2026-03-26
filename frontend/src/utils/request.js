import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code === 0 || payload?.success === true) {
      return payload.data
    }
    return Promise.reject(new Error(payload.message || '请求失败'))
  },
  (error) => Promise.reject(new Error(error.response?.data?.message || error.message || '网络异常'))
)

export default request
