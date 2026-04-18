<template>
  <el-card class="page-card">
    <template #header>
      <div class="header-bar">
        <span class="title">学生预警</span>

        <div class="header-actions">
          <el-select
              v-model="selectedSemester"
              class="semester-select"
              placeholder="请选择学期"
              @change="handleSearch"
          >
            <el-option
                v-for="item in semesterOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>

          <el-select
              v-model="selectedClass"
              class="class-select"
              placeholder="请选择班级"
              @change="handleSearch"
          >
            <el-option
                v-for="item in classOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>

          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button
              type="success"
              :loading="exportLoading"
              @click="handleExport"
          >
            导出Excel
          </el-button>
        </div>
      </div>
    </template>

    <el-table
        v-loading="tableLoading"
        :data="warningList"
        border
        style="width: 100%"
        empty-text="当前条件下暂无危险预警学生"
    >
      <el-table-column prop="studentId" label="学号" min-width="140" />
      <el-table-column prop="name" label="姓名" min-width="100" />
      <el-table-column prop="className" label="班级" min-width="180" />
      <el-table-column prop="college" label="学院" min-width="180" />
      <el-table-column prop="phone" label="联系电话" min-width="140" />

      <el-table-column label="预警状态" width="100">
        <template #default>
          <el-tag type="danger">危险</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="handleViewDetail(scope.row)">
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </el-card>

  <el-dialog
      v-model="detailDialogVisible"
      title="预警详情"
      width="1000px"
      align-center
      destroy-on-close
      :close-on-click-modal="false"
      class="warning-detail-dialog"
  >
    <div v-loading="detailLoading" class="dialog-content">
      <template v-if="detailData">
        <el-card shadow="never" class="detail-card">
          <template #header>
            <span>学生信息</span>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="学号">
              {{ detailData.studentId || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="姓名">
              {{ detailData.name || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="班级">
              {{ detailData.className || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="学院">
              {{ detailData.college || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ detailData.phone || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="当前学期">
              {{ detailData.semester || "-" }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="detail-card">
          <template #header>
            <span>当前学期测评记录</span>
          </template>

          <el-table
              :data="pagedRecords"
              border
              style="width: 100%"
              empty-text="当前学期暂无测评记录"
          >
            <el-table-column prop="scaleName" label="量表名称" min-width="180" />
            <el-table-column prop="scaleCode" label="量表编码" width="120" />
            <el-table-column prop="rawScore" label="得分" width="90" />

            <el-table-column prop="resultLevel" label="结果等级" width="120">
              <template #default="scope">
                <el-tag
                    v-if="scope.row.resultLevel"
                    :type="getLevelTagType(scope.row.resultLevel)"
                >
                  {{ scope.row.resultLevel }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column
                prop="resultSummary"
                label="结果说明"
                min-width="180"
                show-overflow-tooltip
            />
            <el-table-column
                prop="suggestion"
                label="建议"
                min-width="220"
                show-overflow-tooltip
            />

            <el-table-column label="提交时间" min-width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.submittedAt) }}
              </template>
            </el-table-column>
          </el-table>

          <div class="inner-pagination" v-if="recordTotal > 0">
            <el-pagination
                v-model:current-page="recordPageNum"
                :page-size="recordPageSize"
                :total="recordTotal"
                layout="total, prev, pager, next"
                background
            />
          </div>
        </el-card>
      </template>
    </div>

    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, ref } from "vue"
import { ElMessage } from "element-plus"
import request from "@/utils/request"

const semesterOptions = Array.from({ length: 8 }, (_, index) => ({
  label: `第${index + 1}学期`,
  value: `第${index + 1}学期`
}))

const selectedSemester = ref("第1学期")
const selectedClass = ref("")
const classOptions = ref([{ label: "全部", value: "" }])

const warningList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const tableLoading = ref(false)
const exportLoading = ref(false)

const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref(null)

const recordPageNum = ref(1)
const recordPageSize = ref(4)

const recordTotal = computed(() => {
  return detailData.value?.records?.length || 0
})

const pagedRecords = computed(() => {
  const list = detailData.value?.records || []
  const start = (recordPageNum.value - 1) * recordPageSize.value
  const end = start + recordPageSize.value
  return list.slice(start, end)
})

function getCounselorAccount() {
  const storageKeys = [
    "userInfo",
    "loginUser",
    "counselorInfo",
    "user",
    "account"
  ]

  for (const key of storageKeys) {
    const localVal = localStorage.getItem(key)
    const sessionVal = sessionStorage.getItem(key)
    const raw = localVal || sessionVal

    if (!raw) continue

    if (key === "account") {
      return raw
    }

    try {
      const parsed = JSON.parse(raw)
      if (parsed?.counselorAccount) return parsed.counselorAccount
      if (parsed?.account) return parsed.account
      if (parsed?.username) return parsed.username
      if (parsed?.userAccount) return parsed.userAccount
    } catch {
      if (raw) return raw
    }
  }

  return ""
}

function buildDefaultFileName() {
  const className = selectedClass.value ? selectedClass.value : "全部班级"
  return `学生预警名单-${selectedSemester.value}-${className}.xlsx`
}

function parseFileNameFromDisposition(disposition) {
  if (!disposition) return ""

  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) {
    try {
      return decodeURIComponent(utf8Match[1])
    } catch {
      return utf8Match[1]
    }
  }

  const normalMatch = disposition.match(/filename="?([^"]+)"?/i)
  if (normalMatch?.[1]) {
    return normalMatch[1]
  }

  return ""
}

async function parseBlobErrorMessage(blob) {
  try {
    const text = await blob.text()
    if (!text) return "导出失败"

    try {
      const json = JSON.parse(text)
      return json.message || json.msg || "导出失败"
    } catch {
      return text || "导出失败"
    }
  } catch {
    return "导出失败"
  }
}

function downloadBlob(blob, fileName) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement("a")
  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

async function loadClassOptions() {
  const counselorAccount = getCounselorAccount()
  if (!counselorAccount) {
    ElMessage.error("未获取到辅导员账号，请重新登录")
    return false
  }

  try {
    const res = await request.get("/counselor/warning/classes", {
      params: { counselorAccount }
    })

    if (res.code === 200) {
      const actualClassList = res.data || []
      classOptions.value = [
        { label: "全部", value: "" },
        ...actualClassList.map(item => ({
          label: item,
          value: item
        }))
      ]
      selectedClass.value = ""
      return true
    } else {
      ElMessage.error(res.message || "查询班级列表失败")
      return false
    }
  } catch (error) {
    ElMessage.error(error?.message || "查询班级列表失败")
    return false
  }
}

async function loadWarningList() {
  const counselorAccount = getCounselorAccount()
  if (!counselorAccount) {
    ElMessage.error("未获取到辅导员账号，请重新登录")
    return
  }

  tableLoading.value = true
  try {
    const res = await request.post("/counselor/warning/list", {
      counselorAccount,
      semester: selectedSemester.value,
      className: selectedClass.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })

    if (res.code === 200) {
      warningList.value = res.data?.list || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.message || "查询预警名单失败")
    }
  } catch (error) {
    ElMessage.error(error?.message || "查询预警名单失败")
  } finally {
    tableLoading.value = false
  }
}

async function handleExport() {
  const counselorAccount = getCounselorAccount()
  if (!counselorAccount) {
    ElMessage.error("未获取到辅导员账号，请重新登录")
    return
  }

  exportLoading.value = true
  try {
    const response = await request.get("/counselor/warning/export", {
      params: {
        counselorAccount,
        semester: selectedSemester.value,
        className: selectedClass.value
      },
      responseType: "blob"
    })

    const blob = response?.data
    const headers = response?.headers || {}

    if (!(blob instanceof Blob)) {
      throw new Error("导出失败，未获取到文件")
    }

    if (blob.type && blob.type.includes("application/json")) {
      const message = await parseBlobErrorMessage(blob)
      throw new Error(message)
    }

    const disposition =
        headers["content-disposition"] || headers["Content-Disposition"] || ""
    const fileName =
        parseFileNameFromDisposition(disposition) || buildDefaultFileName()

    downloadBlob(blob, fileName)
    ElMessage.success("导出成功")
  } catch (error) {
    if (error?.response?.data instanceof Blob) {
      const message = await parseBlobErrorMessage(error.response.data)
      ElMessage.error(message)
    } else {
      ElMessage.error(error?.message || "导出失败")
    }
  } finally {
    exportLoading.value = false
  }
}

async function handleViewDetail(row) {
  const counselorAccount = getCounselorAccount()
  if (!counselorAccount) {
    ElMessage.error("未获取到辅导员账号，请重新登录")
    return
  }

  detailDialogVisible.value = true
  detailLoading.value = true
  detailData.value = null
  recordPageNum.value = 1

  try {
    const res = await request.get("/counselor/warning/detail", {
      params: {
        counselorAccount,
        studentId: row.studentId,
        semester: selectedSemester.value
      }
    })

    if (res.code === 200) {
      detailData.value = res.data || {}
    } else {
      ElMessage.error(res.message || "查询预警详情失败")
      detailDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error?.message || "查询预警详情失败")
    detailDialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadWarningList()
}

function handleReset() {
  selectedSemester.value = "第1学期"
  selectedClass.value = ""
  pageNum.value = 1
  pageSize.value = 10
  loadWarningList()
}

function handleCurrentChange(val) {
  pageNum.value = val
  loadWarningList()
}

function handleSizeChange(val) {
  pageSize.value = val
  pageNum.value = 1
  loadWarningList()
}

function formatDateTime(value) {
  if (!value) return "-"
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, "0")
  const day = String(date.getDate()).padStart(2, "0")
  const hour = String(date.getHours()).padStart(2, "0")
  const minute = String(date.getMinutes()).padStart(2, "0")
  const second = String(date.getSeconds()).padStart(2, "0")

  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

function getLevelTagType(level) {
  if (!level) return "info"
  if (level.includes("危险") || level.includes("高") || level.includes("重")) return "danger"
  if (level.includes("中")) return "warning"
  if (level.includes("低") || level.includes("轻") || level.includes("正常")) return "success"
  return "info"
}

onMounted(async () => {
  const ok = await loadClassOptions()
  if (ok) {
    loadWarningList()
  }
})
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.semester-select {
  width: 160px;
}

.class-select {
  width: 220px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-card {
  margin-bottom: 16px;
}

.dialog-content {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 4px;
}

.inner-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

<style>
.warning-detail-dialog .el-dialog {
  margin: 0 !important;
}

.warning-detail-dialog .el-dialog__body {
  padding-top: 10px;
  padding-bottom: 10px;
}
</style>