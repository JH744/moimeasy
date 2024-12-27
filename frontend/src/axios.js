// src/axios.js
import axios from 'axios'
import { useAuthStore } from './stores/auth'
import router from './router'

// 중앙집중식 Axios 인스턴스 생성
const api = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL, // .env 파일의 VITE_BACKEND_URL 사용
  withCredentials: true, // 쿠키를 포함하도록 설정 (Refresh Token 사용을 위해 필요)
})

// 요청 인터셉터: 모든 요청에 Access Token을 헤더에 추가
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.accessToken) {
      config.headers['Authorization'] = `Bearer ${authStore.accessToken}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 응답 인터셉터: 401 에러 발생 시 Access Token 갱신 시도
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const authStore = useAuthStore()
    const originalRequest = error.config

    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true
      try {
        await authStore.refreshAccessToken()
        originalRequest.headers['Authorization'] = `Bearer ${authStore.accessToken}`
        return api(originalRequest)
      } catch (err) {
        await authStore.logout()
        router.push('/login')
        return Promise.reject(err)
      }
    }

    return Promise.reject(error)
  }
)

export default api
