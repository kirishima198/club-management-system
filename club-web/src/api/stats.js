import request from '../utils/request'

export const overview = () => request.get('/stats/overview')
export const clubMemberRanking = () => request.get('/stats/club-member-ranking')
export const activityByClub = () => request.get('/stats/activity-by-club')