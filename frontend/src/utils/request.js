import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const readBlobMessage = async (blob) => {
  try {
    const text = await blob.text()
    if (!text) {
      return ''
    }
    const payload = JSON.parse(text)
    return payload?.message || text
  } catch {
    return ''
  }
}

export const resolveRequestErrorMessage = async (error) => {
  const responseData = error?.response?.data
  if (responseData instanceof Blob) {
    const blobMessage = await readBlobMessage(responseData)
    if (blobMessage) {
      return blobMessage
    }
  }
  return responseData?.message || error.message || '网络异常'
}

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
  async (error) => Promise.reject(new Error(await resolveRequestErrorMessage(error)))
)

export default request
