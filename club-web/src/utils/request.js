import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        ElMessage.error(res.msg || '登录已过期，请重新登录')
        router.push('/login')
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  (error) => {
    const res = error.response && error.response.data
    if (res && res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.error(res.msg || '登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error((res && res.msg) || '网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request