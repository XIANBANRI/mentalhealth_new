<template>
  <div class="forget-container">
    <el-card class="forget-card">
      <h2>找回密码</h2>

      <el-form :model="form" class="reset-form">
        <el-form-item label="身份选择">
          <el-radio-group v-model="form.role" class="role-group">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">心理老师</el-radio>
            <el-radio label="counselor">辅导员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="账号">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>

        <el-form-item class="btn-form-item">
          <div class="btn-group">
            <el-button
                type="primary"
                class="full-btn"
                :loading="loading"
                @click="reset"
            >
              重置密码
            </el-button>
            <el-button
                class="full-btn"
                :disabled="loading"
                @click="backLogin"
            >
              返回登录
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import axios from "axios"

const router = useRouter()
const loading = ref(false)

const form = reactive({
  role: "",
  username: "",
  phone: "",
  password: ""
})

const reset = async () => {
  if (!form.role || !form.username || !form.phone || !form.password) {
    ElMessage.error("请填写完整信息")
    return
  }

  loading.value = true

  try {
    const res = await axios.post("http://localhost:8080/api/password/reset", {
      role: form.role,
      username: form.username,
      phone: form.phone,
      password: form.password
    })

    if (res.data && res.data.success) {
      ElMessage.success(res.data.message || "密码重置成功")
      form.role = ""
      form.username = ""
      form.phone = ""
      form.password = ""
      router.push("/")
    } else {
      ElMessage.error((res.data && res.data.message) || "密码重置失败")
    }
  } catch (error) {
    ElMessage.error(
        error?.response?.data?.message ||
        error?.message ||
        "密码重置失败"
    )
  } finally {
    loading.value = false
  }
}

const backLogin = () => {
  router.push("/")
}
</script>

<style scoped>
.forget-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url("@/assets/bg.jpg") no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
}

.forget-card {
  width: 450px;
  padding: 20px !important;
  background-color: rgba(255, 255, 255, 0.9) !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border-radius: 10px !important;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.reset-form {
  width: 100%;
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


.reset-form :deep(.el-form-item__label) {
  width: 80px !important;
  flex: 0 0 80px !important;
  text-align: right !important;
  padding-right: 10px !important;
}

.reset-form :deep(.el-form-item):not(.btn-form-item):not(:first-child) .el-form-item__content {
  width: calc(100% - 90px) !important;
  flex: 0 0 calc(100% - 90px) !important;
}
.reset-form :deep(.el-input) {
  width: 100% !important;
  box-sizing: border-box !important;
}

.btn-form-item {
  margin: 0 !important;
  padding: 0 !important;
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  margin-top: 10px;
}

.full-btn {
  width: 100% !important;
  box-sizing: border-box !important;
  margin: 0 !important;
  padding: 12px 0 !important;
}
</style>