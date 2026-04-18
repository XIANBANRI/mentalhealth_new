<template>
  <div class="welcome-panel">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">心理测试</div>
          <div class="card-desc">进入量表测试，了解当前心理状态</div>
          <el-button type="primary" link @click="goPage('/student/assessment/test')">
            立即进入
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">心理预约</div>
          <div class="card-desc">在线预约心理咨询服务</div>
          <el-button type="primary" link @click="goPage('/student/appointment/apply')">
            前往预约
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">档案查询</div>
          <div class="card-desc">查看个人心理档案与相关信息</div>
          <el-button type="primary" link @click="goPage('/student/profile')">
            查看档案
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="notice-card">
      <template #header>
        <span>欢迎使用学生端</span>
      </template>
      <p>你可以通过左侧菜单返回主页、进行心理测试、查看测试记录、提交心理预约以及查询个人档案。</p>
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