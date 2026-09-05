import request from '../utils/request'

export const pageActivities = (params) =>
  request.get('/activities', { params })
export const activityDetail = (id) => request.get(`/activities/${id}`)
export const publishActivity = (data) => request.post('/activities', data)
export const updateActivity = (id, data) =>
  request.put(`/activities/${id}`, data)
export const deleteActivity = (id) => request.delete(`/activities/${id}`)
export const myPublished = () => request.get('/activities/my/published')
export const mySignups = (params) =>
  request.get('/activities/my/signups', { params })
export const signup = (id) => request.put(`/activities/${id}/signup`)
export const cancelSignup = (id) => request.delete(`/activities/${id}/signup`)
export const pageSignups = (id, params) =>
  request.get(`/activities/${id}/signups`, { params })
export const checkin = (id, userId) =>
  request.put(`/activities/${id}/signups/${userId}/checkin`)