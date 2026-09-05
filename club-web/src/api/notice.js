import request from '../utils/request'

export const pageNotices = (params) => request.get('/notices', { params })
export const publishNotice = (data) => request.post('/notices', data)
export const updateNotice = (id, data) => request.put(`/notices/${id}`, data)
export const deleteNotice = (id) => request.delete(`/notices/${id}`)