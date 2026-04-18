<template>
  <div class="welcome-panel">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">工作时间管理</div>
          <div class="card-desc">维护每周可预约时间段，并查看每日工作安排</div>
          <el-button type="primary" link @click="goPage('/teacher/schedule/manage')">
            立即进入
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">预约管理</div>
          <div class="card-desc">查看当前预约信息并处理学生咨询安排</div>
          <el-button type="primary" link @click="goPage('/teacher/appointment/query')">
            前往查看
          </el-button>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="home-card">
          <div class="card-title">个人信息查询</div>
          <div class="card-desc">查看老师账号、姓名、手机号和办公地点等资料</div>
          <el-button type="primary" link @click="goPage('/teacher/profile')">
            查看信息
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="notice-card">
      <template #header>
        <span>欢迎使用心理老师端</span>
      </template>
      <p>
        您好，{{ teacherName || "心理老师" }}。你可以通过左侧菜单返回主页、进行工作时间管理、查看预约信息和预约记录，以及查询个人信息。
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()
const teacherName = ref("")

const goPage = (path) => {
  router.push(path)
}

onMounted(() => {
  teacherName.value = localStorage.getItem("teacherName") || ""
})
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