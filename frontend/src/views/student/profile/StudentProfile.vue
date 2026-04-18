<template>
  <el-card class="page-card">
    <template #header>
      <div class="header-row">
        <span>档案查询</span>
      </div>
    </template>

    <div class="avatar-section">
      <el-avatar :size="110" :src="displayAvatarUrl" class="avatar-preview">
        {{ avatarFallback }}
      </el-avatar>

      <div class="avatar-actions">
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

    <el-descriptions title="学生档案" :column="2" border v-loading="loading">
      <el-descriptions-item label="学号">
        {{ profile.studentId || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="姓名">
        {{ profile.name || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="班级">
        {{ profile.className || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="学院">
        {{ profile.college || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="年级">
        {{ profile.grade || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="联系电话">
        {{ profile.phone || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="辅导员">
        {{ profile.counselorName || "暂无" }}
      </el-descriptions-item>

      <el-descriptions-item label="辅导员电话">
        {{ profile.counselorPhone || "暂无" }}
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
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
  studentId: "",
  name: "",
  className: "",
  college: "",
  grade: "",
  phone: "",
  avatarUrl: "",
  counselorName: "",
  counselorPhone: ""
})

const assignProfile = (data = {}) => {
  profile.studentId = data.studentId || ""
  profile.name = data.name || ""
  profile.className = data.className || ""
  profile.college = data.college || ""
  profile.grade = data.grade || ""
  profile.phone = data.phone || ""
  profile.avatarUrl = data.avatarUrl || ""
  profile.counselorName = data.counselorName || ""
  profile.counselorPhone = data.counselorPhone || ""
}

const getStudentId = () => {
  const role = localStorage.getItem("role")
  const studentId = localStorage.getItem("studentId")

  if (role !== "student") {
    ElMessage.error("当前登录身份不是学生")
    return ""
  }

  if (!studentId) {
    ElMessage.error("未获取到学生学号，请重新登录")
    return ""
  }

  return studentId
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
  return "学"
})

const loadProfile = async () => {
  const studentId = getStudentId()
  if (!studentId) return

  loading.value = true

  try {
    const result = await request.post("/api/student/profile", { studentId })

    if (result?.success) {
      assignProfile(result.data || {})
    } else {
      ElMessage.error(result?.message || "档案查询失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "档案查询失败")
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
  const studentId = getStudentId()
  if (!studentId) {
    options.onError(new Error("未获取到学生学号"))
    return
  }

  const formData = new FormData()
  formData.append("studentId", studentId)
  formData.append("file", options.file)

  uploading.value = true

  try {
    const result = await request.post("/api/student/profile/avatar", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })

    if (result?.success) {
      assignProfile(result.data || {})
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
.page-card {
  border-radius: 12px;
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