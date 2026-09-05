import request from '../utils/request'

export const pageClubs = (params) => request.get('/clubs', { params })
export const clubDetail = (id) => request.get(`/clubs/${id}`)
export const applyClub = (data) => request.post('/clubs', data)
export const updateClub = (id, data) => request.put(`/clubs/${id}`, data)
export const deleteClub = (id) => request.delete(`/clubs/${id}`)
export const auditClub = (id, data) => request.put(`/clubs/${id}/audit`, data)
export const myCreated = () => request.get('/clubs/my/created')