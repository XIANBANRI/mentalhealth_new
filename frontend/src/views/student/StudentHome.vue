<template>
  <div class="student-page">
    <el-container class="layout-container">
      <el-aside width="220px" class="sidebar">
        <div class="logo-area">
          <h2>学生端</h2>
          <p>心理健康管理系统</p>
        </div>

        <el-menu
            class="menu"
            :default-active="activeMenu"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
            router
        >
          <el-menu-item index="/student">
            <el-icon><House /></el-icon>
            <span>主页</span>
          </el-menu-item>

          <el-sub-menu index="assessment">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>心理测评</span>
            </template>
            <el-menu-item index="/student/assessment/test">
              心理测试
            </el-menu-item>
            <el-menu-item index="/student/assessment/record">
              测试记录
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="appointment">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>预约管理</span>
            </template>
            <el-menu-item index="/student/appointment/apply">
              心理预约
            </el-menu-item>
            <el-menu-item index="/student/appointment/record">
              预约记录
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/student/profile">
            <el-icon><Document /></el-icon>
            <span>个人信息查询</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <span class="page-title">学生主页</span>
          </div>

          <div class="header-right">
            <span class="info-item">学号：{{ student.studentId || "暂无" }}</span>
            <span class="info-item">班级：{{ student.className || "暂无" }}</span>

            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                姓名：{{ student.name || "暂无" }}
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, reactive, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { ElMessage, ElMessageBox } from "element-plus"
import request from "@/utils/request"
import {
  House,
  Reading,
  Calendar,
  Document,
  ArrowDown
} from "@element-plus/icons-vue"

const router = useRouter()
const route = useRoute()

const student = reactive({
  studentId: localStorage.getItem("studentId") || localStorage.getItem("username") || "",
  name: localStorage.getItem("studentName") || "",
  className: localStorage.getItem("className") || "",
  college: localStorage.getItem("college") || "",
  phone: localStorage.getItem("phone") || ""
})

const activeMenu = computed(() => route.path)

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
}

const loadStudentProfile = async () => {
  const role = localStorage.getItem("role")
  const studentId = localStorage.getItem("studentId") || localStorage.getItem("username")

  if (role !== "student") {
    ElMessage.error("当前登录身份不是学生")
    router.push("/")
    return
  }

  if (!studentId) {
    ElMessage.error("未获取到学生学号，请重新登录")
    router.push("/")
    return
  }

  try {
    const result = await request.get("/api/student/profile")

    if (result?.success) {
      const data = result.data || {}

      student.studentId = data.studentId || studentId
      student.name = data.name || ""
      student.className = data.className || ""
      student.college = data.college || ""
      student.phone = data.phone || ""

      localStorage.setItem("studentId", student.studentId || "")
      localStorage.setItem("studentName", data.name || "")
      localStorage.setItem("className", data.className || "")
      localStorage.setItem("college", data.college || "")
      localStorage.setItem("phone", data.phone || "")
    } else {
      ElMessage.error(result?.message || "学生信息加载失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "学生信息加载失败")
  }
}

const handleCommand = async (command) => {
  if (command === "logout") {
    try {
      await ElMessageBox.confirm("确认退出登录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })

      clearLoginCache()
      ElMessage.success("已退出登录")
      router.push("/")
    } catch (e) {
      // 用户取消
    }
  }
}

onMounted(() => {
  loadStudentProfile()
})
</script>

<style scoped>
.student-page {
  height: 100vh;
  background: #f5f7fa;
}

.layout-container {
  height: 100%;
}

.sidebar {
  background: #304156;
  color: #fff;
  overflow: hidden;
}

.logo-area {
  padding: 24px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-area h2 {
  margin: 0;
  font-size: 22px;
  color: #fff;
}

.logo-area p {
  margin: 8px 0 0;
  font-size: 12px;
  color: #bfcbd9;
}

.menu {
  border-right: none;
  height: calc(100% - 88px);
}

.header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-sizing: border-box;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 14px;
  color: #606266;
}

.user-dropdown {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  color: #303133;
}

.dropdown-icon {
  margin-left: 6px;
}

.main-content {
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}

.info-item {
  white-space: nowrap;
}
</style>