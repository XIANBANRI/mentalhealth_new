import axios from "axios"
import router from "@/router"

const request = axios.create({
  baseURL: "/",
  timeout: 15000,
  headers: {
    "Content-Type": "application/json"
  }
})

// 请求拦截：自动带 token
request.interceptors.request.use(
    config => {
      const token = localStorage.getItem("token")
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    error => Promise.reject(error)
)

// 响应拦截：统一处理返回
request.interceptors.response.use(
    response => {
      if (response.config?.responseType === "blob") {
        return response
      }
      return response.data
    },
    error => {
      const status = error?.response?.status
      const message = error?.response?.data?.message || "请求失败"

      if (status === 401) {
        localStorage.removeItem("token")
        localStorage.removeItem("role")
        localStorage.removeItem("username")
        localStorage.removeItem("redirectPath")

        alert("登录已失效，请重新登录")
        router.push("/")
      } else if (status === 403) {
        alert("无权限访问该功能")
      } else {
        console.error("请求错误：", message)
      }

      return Promise.reject(error)
    }
)

export default request