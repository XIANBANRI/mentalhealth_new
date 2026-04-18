<template>
  <div class="welcome-panel">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="home-card">
          <div class="card-title">辅导员管理</div>
          <div class="card-desc">查看和维护辅导员账号与基础信息</div>
          <el-button type="primary" link @click="goPage('/admin/management/counselor')">
            立即进入
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="home-card">
          <div class="card-title">心理老师管理</div>
          <div class="card-desc">管理心理老师基础信息与状态</div>
          <el-button type="primary" link @click="goPage('/admin/management/teacher')">
            立即进入
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="home-card">
          <div class="card-title">测试输入</div>
          <div class="card-desc">录入心理测评量表或测试基础内容</div>
          <el-button type="primary" link @click="goPage('/admin/test/input')">
            立即进入
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="home-card">
          <div class="card-title">个人信息</div>
          <div class="card-desc">查看当前管理员个人信息</div>
          <el-button type="primary" link @click="goPage('/admin/profile')">
            查看信息
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="notice-card">
      <template #header>
        <span>欢迎使用管理员端</span>
      </template>
      <p>
        当前管理员端包含主页、人员管理、测试输入、个人信息四个模块。
        目前为纯前端静态页面演示版本，暂未连接后端接口。
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router"

const router = useRouter()

const goPage = (path) => {
  router.push(path)
}
</script>

<style scoped>
.welcome-panel {
  min-height: 100%;
}

.home-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.card-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.6;
}

.notice-card {
  border-radius: 12px;
}
</style>