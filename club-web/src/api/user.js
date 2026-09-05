import request from '../utils/request'

export const pageUsers = (params) => request.get('/users', { params })
export const addUser = (data) => request.post('/users', data)
export const updateUser = (id, data) => request.put(`/users/${id}`, data)
export const deleteUser = (id) => request.delete(`/users/${id}`)
export const resetPassword = (id) => request.put(`/users/${id}/reset-password`)
export const updateStatus = (id, status) =>
  request.put(`/users/${id}/status`, null, { params: { status } })