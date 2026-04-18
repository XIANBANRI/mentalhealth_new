<template>
  <div class="admin-page">
    <el-container class="layout-container">
      <el-aside width="220px" class="sidebar">
        <div class="logo-area">
          <h2>管理员端</h2>
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
          <el-menu-item index="/admin">
            <el-icon><House /></el-icon>
            <span>主页</span>
          </el-menu-item>

          <el-sub-menu index="management">
            <template #title>
              <el-icon><User /></el-icon>
              <span>人员管理</span>
            </template>
            <el-menu-item index="/admin/management/counselor">
              辅导员管理
            </el-menu-item>
            <el-menu-item index="/admin/management/teacher">
              心理老师管理
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/test/input">
            <el-icon><EditPen /></el-icon>
            <span>测试集输入</span>
          </el-menu-item>

          <el-menu-item index="/admin/profile">
            <el-icon><Document /></el-icon>
            <span>个人信息查询</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <span class="page-title">管理员主页</span>
          </div>

          <div class="header-right">
            <span class="info-item">账号：{{ admin.account || "-" }}</span>
            <span class="info-item">角色：系统管理员</span>

            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                操作
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
import { computed, onMounted, reactive } from "vue"
import { useRoute, useRouter } from "vue-router"
import { ElMessage, ElMessageBox } from "element-plus"
import {
  House,
  User,
  EditPen,
  Document,
  ArrowDown
} from "@element-plus/icons-vue"
import request from "@/utils/request"

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const admin = reactive({
  account: "",
  name: ""
})

const getCurrentAdminAccount = () => {
  return (
      localStorage.getItem("adminAccount") ||
      localStorage.getItem("username") ||
      ""
  )
}

const fetchAdminProfile = async () => {
  const account = getCurrentAdminAccount()

  if (!account) {
    admin.account = ""
    admin.name = ""
    return
  }

  try {
    const result = await request.get("/api/admin/profile", {
      params: { account }
    })

    const data = result?.data || {}

    admin.account = data.account || account
    admin.name = data.name || ""

    if (data.account) {
      localStorage.setItem("adminAccount", data.account)
    }
    if (data.name) {
      localStorage.setItem("adminName", data.name)
    }
  } catch (error) {
    admin.account = account
    admin.name = localStorage.getItem("adminName") || ""
    ElMessage.error(
        error?.response?.data?.message ||
        error?.message ||
        "获取管理员信息失败"
    )
  }
}

const clearLoginCache = () => {
  localStorage.removeItem("token")
  localStorage.removeItem("role")
  localStorage.removeItem("username")
  localStorage.removeItem("userInfo")
  localStorage.removeItem("adminAccount")
  localStorage.removeItem("adminName")
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
  fetchAdminProfile()
})
</script>

<style scoped>
.admin-page {
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
  color: #ffffff;
}

.logo-area p {
  margin: 8px 0 0;
  font-size: 13px;
  color: #bfcbd9;
}

.menu {
  border-right: none;
}

.header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
  color: #606266;
  font-size: 14px;
}

.info-item {
  white-space: nowrap;
}

.user-dropdown {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  color: #303133;
  font-size: 14px;
}

.dropdown-icon {
  margin-left: 6px;
}

.main-content {
  padding: 20px;
  background: #f5f7fa;
}
</style>