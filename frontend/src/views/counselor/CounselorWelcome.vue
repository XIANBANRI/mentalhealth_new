<template>
  <div class="welcome-panel">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">学生查看</div>
          <div class="card-desc">查看所负责学生的基础信息与心理相关情况</div>
          <el-button type="primary" link @click="goPage('/counselor/student/view')">
            立即进入
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">学生预警</div>
          <div class="card-desc">查看需要重点关注的学生预警信息</div>
          <el-button type="primary" link @click="goPage('/counselor/student/warning')">
            前往查看
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">个人信息</div>
          <div class="card-desc">查看辅导员个人资料与负责信息</div>
          <el-button type="primary" link @click="goPage('/counselor/profile')">
            查看信息
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="home-card">
          <div class="card-title">趋势报告</div>
          <div class="card-desc">查看学生心理状态变化趋势与统计情况</div>
          <el-button type="primary" link @click="goPage('/counselor/report')">
            查看报告
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="home-card">
          <div class="card-title">预约情况</div>
          <div class="card-desc">查看学生预约咨询的整体情况</div>
          <el-button type="primary" link @click="goPage('/counselor/appointment')">
            前往查看
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="notice-card">
      <template #header>
        <span>欢迎使用辅导员端</span>
      </template>
      <p>
        你可以通过左侧菜单返回主页、查看学生信息、关注学生预警、浏览趋势报告、查看预约情况以及查询个人信息。
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