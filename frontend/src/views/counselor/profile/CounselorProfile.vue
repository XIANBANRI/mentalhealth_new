<template>
  <div class="counselor-profile-page">
    <el-card class="page-card">
      <template #header>
        <div class="header-row">
          <span>辅导员个人信息</span>
        </div>
      </template>

      <div class="avatar-section">
        <el-avatar :size="110" :src="displayAvatarUrl" class="avatar-preview">
          {{ avatarFallback }}
        </el-avatar>

        <div class="avatar-actions">
          <!-- 这里已删除：辅导员头像 -->
          <div class="avatar-tip">支持 jpg / jpeg / png / webp，大小不超过 5MB</div>

          <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
              accept=".jpg,.jpeg,.png,.webp"
          >
            <el-button type="primary" :loading="uploading">上传头像</el-button>
          </el-upload>
        </div>
      </div>

      <el-descriptions :column="2" border v-loading="loading">
        <el-descriptions-item label="辅导员账号">
          {{ profile.counselorId || "暂无" }}
        </el-descriptions-item>

        <el-descriptions-item label="姓名">
          {{ profile.name || "暂无" }}
        </el-descriptions-item>

        <el-descriptions-item label="学院">
          {{ profile.college || "暂无" }}
        </el-descriptions-item>

        <el-descriptions-item label="负责年级">
          {{ profile.grade || "暂无" }}
        </el-descriptions-item>

        <el-descriptions-item label="手机号">
          {{ profile.phone || "暂无" }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue"
import { ElMessage } from "element-plus"
import request from "@/utils/request"

const loading = ref(false)
const uploading = ref(false)

const BACKEND_BASE_URL = (
    import.meta.env.VITE_FILE_BASE_URL ||
    import.meta.env.VITE_API_BASE_URL ||
    "http://localhost:8080"
).replace(/\/$/, "")

const profile = reactive({
  counselorId: "",
  name: "",
  college: "",
  grade: "",
  phone: "",
  avatarUrl: ""
})

const assignProfile = (data = {}, fallbackAccount = "") => {
  profile.counselorId = data.counselorId || fallbackAccount || ""
  profile.name = data.name || ""
  profile.college = data.college || ""
  profile.grade = data.grade || ""
  profile.phone = data.phone || ""
  profile.avatarUrl = data.avatarUrl || ""

  localStorage.setItem("counselorAccount", profile.counselorId)
}

const getCounselorAccount = () => {
  const account =
      localStorage.getItem("counselorAccount") || localStorage.getItem("username")

  if (!account) {
    ElMessage.error("未获取到辅导员账号，请重新登录")
    return ""
  }

  return account
}

const buildAvatarUrl = (path) => {
  if (!path) return ""
  if (/^https?:\/\//i.test(path)) return path
  return `${BACKEND_BASE_URL}${path.startsWith("/") ? "" : "/"}${path}`
}

const displayAvatarUrl = computed(() => buildAvatarUrl(profile.avatarUrl))

const avatarFallback = computed(() => {
  if (profile.name) {
    return profile.name.substring(profile.name.length - 1)
  }
  return "导"
})

const loadProfile = async () => {
  const account = getCounselorAccount()
  if (!account) return

  loading.value = true
  try {
    const result = await request.post("/api/counselor/profile", { account })
    const success = result?.code === 200 || result?.success === true

    if (success) {
      assignProfile(result?.data || {}, account)
    } else {
      ElMessage.error(result?.message || "查询失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "查询失败")
  } finally {
    loading.value = false
  }
}

const beforeAvatarUpload = (rawFile) => {
  const isImage =
      ["image/jpeg", "image/png", "image/webp"].includes(rawFile.type) ||
      /\.(jpg|jpeg|png|webp)$/i.test(rawFile.name)

  const isLt5M = rawFile.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error("只支持 jpg、jpeg、png、webp 格式图片")
    return false
  }

  if (!isLt5M) {
    ElMessage.error("头像大小不能超过 5MB")
    return false
  }

  return true
}

const handleAvatarUpload = async (options) => {
  const account = getCounselorAccount()
  if (!account) {
    options.onError(new Error("未获取到辅导员账号"))
    return
  }

  const formData = new FormData()
  formData.append("account", account)
  formData.append("file", options.file)

  uploading.value = true

  try {
    const result = await request.post("/api/counselor/profile/avatar", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })

    const success = result?.code === 200 || result?.success === true

    if (success) {
      assignProfile(result?.data || {}, account)
      ElMessage.success(result?.message || "头像上传成功")
      options.onSuccess(result)
    } else {
      const error = new Error(result?.message || "头像上传失败")
      ElMessage.error(error.message)
      options.onError(error)
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "头像上传失败")
    options.onError(error)
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.counselor-profile-page {
  min-height: 100%;
}

.page-card {
  border-radius: 10px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: #fafafa;
}

.avatar-preview {
  flex-shrink: 0;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.avatar-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.avatar-tip {
  font-size: 13px;
  color: #909399;
}
</style>