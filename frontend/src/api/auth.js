import request from '../utils/request'

export const loginApi = (data) => request.post('/auth/login', data)
export const registerApi = (data) => request.post('/auth/register', data)
export const meApi = () => request.get('/auth/me')
