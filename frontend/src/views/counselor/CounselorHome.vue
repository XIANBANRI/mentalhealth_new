<template>
  <div class="counselor-page">
    <el-container class="layout-container">
      <el-aside width="220px" class="sidebar">
        <div class="logo-area">
          <h2>辅导员端</h2>
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
          <el-menu-item index="/counselor">
            <el-icon><House /></el-icon>
            <span>主页</span>
          </el-menu-item>

          <el-sub-menu index="student">
            <template #title>
              <el-icon><User /></el-icon>
              <span>学生管理</span>
            </template>
            <el-menu-item index="/counselor/student/view">
              学生查看
            </el-menu-item>
            <el-menu-item index="/counselor/student/warning">
              学生预警
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/counselor/report">
            <el-icon><TrendCharts /></el-icon>
            <span>趋势报告</span>
          </el-menu-item>

          <el-menu-item index="/counselor/profile">
            <el-icon><Document /></el-icon>
            <span>个人信息查询</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <span class="page-title">辅导员主页</span>
          </div>

          <div class="header-right">
            <span class="info-item">账号：{{ counselor.counselorId || "暂无" }}</span>
            <span class="info-item">学院：{{ counselor.college || "暂无" }}</span>
            <span class="info-item">年级：{{ counselor.grade || "暂无" }}</span>

            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                姓名：{{ counselor.name || "暂无" }}
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
  User,
  TrendCharts,
  Document,
  ArrowDown
} from "@element-plus/icons-vue"

const router = useRouter()
const route = useRoute()

const counselor = reactive({
  counselorId: localStorage.getItem("counselorId") || "",
  name: localStorage.getItem("counselorName") || "",
  college: localStorage.getItem("college") || "",
  grade: localStorage.getItem("grade") || "",
  phone: localStorage.getItem("phone") || ""
})

const activeMenu = computed(() => route.path)

const clearLoginCache = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("role")
  localStorage.removeItem("username")
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
  localStorage.removeItem("counselorAccount")
}

const loadCounselorProfile = async () => {
  const role = localStorage.getItem("role")
  const account =
      localStorage.getItem("counselorAccount") ||
      localStorage.getItem("username") ||
      localStorage.getItem("counselorId")

  if (role !== "counselor") {
    ElMessage.error("当前登录身份不是辅导员")
    router.push("/")
    return
  }

  if (!account) {
    ElMessage.error("未获取到辅导员账号，请重新登录")
    router.push("/")
    return
  }

  try {
    const result = await request.post("/api/counselor/profile", {
      account
    })

    const success = result?.code === 200 || result?.success === true

    if (success) {
      const data = result?.data || {}

      counselor.counselorId = data.counselorId || account
      counselor.name = data.name || ""
      counselor.college = data.college || ""
      counselor.grade = data.grade || ""
      counselor.phone = data.phone || ""

      localStorage.setItem("counselorId", data.counselorId || account)
      localStorage.setItem("counselorAccount", data.counselorId || account)
      localStorage.setItem("counselorName", data.name || "")
      localStorage.setItem("college", data.college || "")
      localStorage.setItem("grade", data.grade || "")
      localStorage.setItem("phone", data.phone || "")
    } else {
      ElMessage.error(result?.message || "辅导员信息加载失败")
    }
  } catch (error) {
    ElMessage.error(
        error?.response?.data?.message ||
        error?.response?.data?.msg ||
        error?.message ||
        "辅导员信息加载失败"
    )
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
  loadCounselorProfile()
})
</script>

<style scoped>
.counselor-page {
  height: 100vh;
  background: #f5f7fa;
}

.layout-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  color: #fff;
}

.logo-area {
  height: 70px;
  padding: 12px 16px;
  box-sizing: border-box;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-area h2 {
  margin: 0;
  font-size: 22px;
  color: #fff;
}

.logo-area p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #c0c4cc;
}

.menu {
  border-right: none;
  height: calc(100% - 70px);
}

.header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left .page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #606266;
}

.info-item {
  white-space: nowrap;
}

.user-dropdown {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  color: #409eff;
}

.dropdown-icon {
  margin-left: 6px;
}

.main-content {
  padding: 20px;
  box-sizing: border-box;
  background: #f5f7fa;
}
</style>