import axios from 'axios'
import request from '../utils/request'

export const fetchSchools = (keyword) => request.get('/schools', { params: { keyword } })
export const fetchSchoolDetail = (id) => request.get(`/schools/${id}`)
export const fetchMajors = () => request.get('/majors')
export const fetchAdmissions = (params) => request.get('/admissions', { params })
export const fetchAdmissionDetail = (id) => request.get(`/admissions/${id}`)
export const fetchRatios = (params) => request.get('/ratios', { params })
export const fetchPolicies = (params) => request.get('/policies', { params })
export const fetchPlans = () => request.get('/student/plans')
export const createPlan = (data) => request.post('/student/plans', data)
export const fetchPlanDetail = (id) => request.get(`/student/plans/${id}`)
export const fetchTasks = (planId) => request.get(`/student/plans/${planId}/tasks`)
export const createTask = (planId, data) => request.post(`/student/plans/${planId}/tasks`, data)
export const updateTaskStatus = (id, status) => request.patch(`/student/tasks/${id}/status`, { status })
export const recordTaskProgress = (id, data) => request.post(`/student/tasks/${id}/progress`, data)
export const fetchReminders = () => request.get('/student/reminders')
export const fetchPosts = () => request.get('/posts')
export const createPost = (data) => request.post('/posts', data)
export const fetchPostDetail = (id) => request.get(`/posts/${id}`)
export const deletePost = (id) => request.delete(`/posts/${id}`)
export const createComment = (id, data) => request.post(`/posts/${id}/comments`, data)
export const fetchComments = (id) => request.get(`/posts/${id}/comments`)
export const deleteComment = (id) => request.delete(`/posts/comments/${id}`)
export const fetchPapers = () => request.get('/student/exams/papers')
export const fetchPaperDetail = (id) => request.get(`/student/exams/papers/${id}`)
export const fetchPaperQuestions = (id) => request.get(`/student/exams/papers/${id}/questions`)
export const submitPaper = (data) => request.post('/student/exams/submit', data)
export const fetchExamRecords = () => request.get('/student/exams/records')
export const fetchExamRecordDetail = (id) => request.get(`/student/exams/records/${id}`)
export const fetchWrongQuestions = () => request.get('/student/exams/wrong-questions')
export const fetchMaterials = () => request.get('/materials')
export const fetchMaterialDetail = (id) => request.get(`/student/materials/${id}`)
export const fetchMyMaterials = () => request.get('/student/materials/mine')
export const fetchMaterialCategories = () => request.get('/student/materials/categories')
export const uploadMaterial = (data) => request.post('/student/materials', data, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const downloadMaterial = (id) => {
  const token = localStorage.getItem('token')
  return axios.get(`/api/student/materials/${id}/download`, {
    responseType: 'blob',
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  })
}
export const fetchProfile = () => request.get('/student/profile')
export const fetchNotices = () => request.get('/notices')
