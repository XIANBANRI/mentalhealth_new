<template>
  <div class="teacher-page">
    <el-container class="layout-container">
      <el-aside width="220px" class="sidebar">
        <div class="logo-area">
          <h2>心理老师端</h2>
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
          <el-menu-item index="/teacher">
            <el-icon><House /></el-icon>
            <span>主页</span>
          </el-menu-item>

          <el-sub-menu index="schedule">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>工作时间管理</span>
            </template>
            <el-menu-item index="/teacher/schedule/query">
              时间查询
            </el-menu-item>
            <el-menu-item index="/teacher/schedule/manage">
              工作时间管理
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="appointment">
            <template #title>
              <el-icon><Tickets /></el-icon>
              <span>预约管理</span>
            </template>
            <el-menu-item index="/teacher/appointment/query">
              预约查询
            </el-menu-item>
            <el-menu-item index="/teacher/appointment/record">
              预约记录
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/teacher/profile">
            <el-icon><User /></el-icon>
            <span>个人信息查询</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <span class="page-title">心理老师主页</span>
          </div>

          <div class="header-right">
            <span class="info-item">账号：{{ teacher.teacherAccount || "暂无" }}</span>

            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                姓名：{{ teacher.teacherName || "暂无" }}
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
  Calendar,
  Tickets,
  User,
  ArrowDown
} from "@element-plus/icons-vue"

const router = useRouter()
const route = useRoute()

const teacher = reactive({
  teacherAccount: localStorage.getItem("teacherAccount") || localStorage.getItem("username") || "",
  teacherName: localStorage.getItem("teacherName") || "",
  phone: localStorage.getItem("teacherPhone") || "",
  officeLocation: localStorage.getItem("officeLocation") || ""
})

const activeMenu = computed(() => route.path)

const clearLoginCache = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("role")
  localStorage.removeItem("username")
  localStorage.removeItem("redirectPath")
  localStorage.removeItem("userInfo")

  localStorage.removeItem("teacherAccount")
  localStorage.removeItem("teacherName")
  localStorage.removeItem("teacherPhone")
  localStorage.removeItem("officeLocation")
}

const loadTeacherProfile = async () => {
  const role = localStorage.getItem("role")
  const teacherAccount =
      localStorage.getItem("teacherAccount") || localStorage.getItem("username")

  if (role !== "teacher") {
    ElMessage.error("当前登录身份不是心理老师")
    router.push("/")
    return
  }

  if (!teacherAccount) {
    ElMessage.error("未获取到老师账号，请重新登录")
    router.push("/")
    return
  }

  try {
    const result = await request.get("/api/teacher/profile")

    const success = result?.code === 200 || result?.success === true

    if (success) {
      const data = result?.data || {}

      teacher.teacherAccount = data.teacherAccount || teacherAccount
      teacher.teacherName = data.teacherName || data.name || ""
      teacher.phone = data.phone || ""
      teacher.officeLocation = data.officeLocation || ""

      localStorage.setItem("teacherAccount", data.teacherAccount || teacherAccount)
      localStorage.setItem("teacherName", data.teacherName || data.name || "")
      localStorage.setItem("teacherPhone", data.phone || "")
      localStorage.setItem("officeLocation", data.officeLocation || "")
    } else {
      ElMessage.error(result?.message || "老师信息加载失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "老师信息加载失败")
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
  loadTeacherProfile()
})
</script>

<style scoped>
.teacher-page {
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

.info-item {
  white-space: nowrap;
}

.user-dropdown {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  color: #409eff;
  font-weight: 500;
}

.dropdown-icon {
  margin-left: 6px;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  box-sizing: border-box;
}
</style>