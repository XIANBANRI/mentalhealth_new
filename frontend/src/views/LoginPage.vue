<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>心理健康管理系统</h2>

      <el-form :model="form" @keyup.enter="login">
        <el-form-item label="身份选择">
          <el-radio-group v-model="form.role" class="role-group">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">心理老师</el-radio>
            <el-radio label="counselor">辅导员</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>

        <div class="btn-group">
          <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="login"
          >
            登录
          </el-button>

          <el-button type="text" @click="goForget">
            忘记密码
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import request from "@/utils/request"

const router = useRouter()
const loading = ref(false)

const form = reactive({
  role: "",
  username: "",
  password: ""
})

const getAdminDisplayName = (username, data = {}) => {
  if (data.name && String(data.name).trim()) {
    return data.name
  }

  if (username === "admin") {
    return "管理员1"
  }

  if (username === "operator" || username === "admin2") {
    return "管理员2"
  }

  return "系统管理员"
}

const clearLoginCache = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("role")
  localStorage.removeItem("username")
  localStorage.removeItem("redirectPath")
  localStorage.removeItem("userInfo")

  localStorage.removeItem("studentId")
  localStorage.removeItem("studentName")
  localStorage.removeItem("className")
  localStorage.removeItem("college")
  localStorage.removeItem("grade")
  localStorage.removeItem("phone")

  localStorage.removeItem("teacherAccount")
  localStorage.removeItem("teacherName")
  localStorage.removeItem("teacherPhone")
  localStorage.removeItem("officeLocation")

  localStorage.removeItem("counselorId")
  localStorage.removeItem("counselorName")

  localStorage.removeItem("adminAccount")
  localStorage.removeItem("adminName")
}

const login = async () => {
  if (!form.role || !form.username || !form.password) {
    ElMessage.error("请填写完整信息")
    return
  }

  loading.value = true

  try {
    const result = await request.post("/api/auth/login", {
      role: form.role,
      username: form.username,
      password: form.password
    })

    const success = result?.code === 200 || result?.success === true
    const data = result?.data || {}

    if (!success) {
      ElMessage.error(result?.message || "登录失败")
      return
    }

    const role = data.role || form.role
    const username = data.username || data.account || form.username
    const redirectPath = data.redirectPath || `/${role}`
    const token = data.token || result?.token

    if (!token) {
      ElMessage.error("登录失败：后端未返回 token")
      return
    }

    clearLoginCache()

    localStorage.setItem("token", token)
    localStorage.setItem("role", role)
    localStorage.setItem("username", username)
    localStorage.setItem("redirectPath", redirectPath)

    const userInfo = {
      role,
      username
    }

    if (role === "student") {
      const studentName = data.name || data.studentName || ""

      localStorage.setItem("studentId", data.studentId || username)

      if (studentName) {
        localStorage.setItem("studentName", studentName)
        userInfo.name = studentName
      }
      if (data.className) {
        localStorage.setItem("className", data.className)
        userInfo.className = data.className
      }
      if (data.college) {
        localStorage.setItem("college", data.college)
        userInfo.college = data.college
      }
      if (data.grade) {
        localStorage.setItem("grade", data.grade)
        userInfo.grade = data.grade
      }
      if (data.phone) {
        localStorage.setItem("phone", data.phone)
        userInfo.phone = data.phone
      }
    }

    if (role === "teacher") {
      const teacherName = data.teacherName || data.name || ""

      localStorage.setItem(
          "teacherAccount",
          data.teacherAccount || data.account || username
      )

      if (teacherName) {
        localStorage.setItem("teacherName", teacherName)
        userInfo.name = teacherName
      }
      if (data.phone) {
        localStorage.setItem("teacherPhone", data.phone)
        userInfo.phone = data.phone
      }
      if (data.officeLocation) {
        localStorage.setItem("officeLocation", data.officeLocation)
        userInfo.officeLocation = data.officeLocation
      }
    }

    if (role === "counselor") {
      const counselorName = data.name || data.counselorName || ""

      localStorage.setItem(
          "counselorId",
          data.counselorId || data.account || username
      )

      if (counselorName) {
        localStorage.setItem("counselorName", counselorName)
        userInfo.name = counselorName
      }
      if (data.college) {
        localStorage.setItem("college", data.college)
        userInfo.college = data.college
      }
      if (data.grade) {
        localStorage.setItem("grade", data.grade)
        userInfo.grade = data.grade
      }
      if (data.phone) {
        localStorage.setItem("phone", data.phone)
        userInfo.phone = data.phone
      }
    }

    if (role === "admin") {
      const adminName = getAdminDisplayName(username, data)

      localStorage.setItem("adminAccount", username)
      localStorage.setItem("adminName", adminName)

      userInfo.name = adminName
    }

    localStorage.setItem("userInfo", JSON.stringify(userInfo))

    ElMessage.success(result?.message || "登录成功")
    router.push(redirectPath)
  } catch (error) {
    console.error(error)
    ElMessage.error(
        error?.response?.data?.message ||
        error?.response?.data?.msg ||
        error?.message ||
        "无法连接后端，请检查后端是否启动"
    )
  } finally {
    loading.value = false
  }
}

const goForget = () => {
  router.push("/forget")
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 原来的渐变背景已替换为图片背景 */
  background: url("@/assets/bg.jpg") no-repeat center center;
  background-size: cover;
}

.login-card {
  width: 450px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.role-group {
  display: flex !important;
  flex-wrap: nowrap !important;
  gap: 15px !important;
  align-items: center;
  width: 100%;
}

.role-group .el-radio {
  display: inline-flex !important;
  margin: 0 !important;
}

.btn-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-btn {
  width: 70%;
}
</style>