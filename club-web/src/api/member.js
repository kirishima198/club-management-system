import request from '../utils/request'

export const applyJoin = (clubId, data) =>
  request.post(`/clubs/${clubId}/apply`, data || {})
export const myJoined = () => request.get('/clubs/my/joined')
export const pageMembers = (clubId, params) =>
  request.get(`/clubs/${clubId}/members`, { params })
export const pagePending = (clubId, params) =>
  request.get(`/clubs/${clubId}/members/pending`, { params })
export const auditMember = (clubId, memberId, data) =>
  request.put(`/clubs/${clubId}/members/${memberId}/audit`, data)
export const removeMember = (clubId, memberId) =>
  request.delete(`/clubs/${clubId}/members/${memberId}`)
export const quitClub = (clubId) => request.delete(`/clubs/${clubId}/quit`)