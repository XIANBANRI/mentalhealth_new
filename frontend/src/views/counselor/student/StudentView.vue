<template>
  <el-card class="page-card">
    <template #header>
      <div class="header-bar">
        <span class="title">学生查看</span>

        <div class="header-actions">
          <el-select
              v-model="selectedClass"
              placeholder="请选择班级"
              class="class-select"
              @change="handleSearch"
          >
            <el-option
                v-for="item in classOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>

          <el-input
              v-model="keyword"
              placeholder="请输入学号/姓名"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
          />

          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </template>

    <el-table
        v-loading="tableLoading"
        :data="studentList"
        border
        style="width: 100%"
        empty-text="暂无学生数据"
    >
      <el-table-column prop="studentId" label="学号" min-width="140" />
      <el-table-column prop="name" label="姓名" min-width="100" />
      <el-table-column prop="className" label="班级" min-width="160" />
      <el-table-column prop="college" label="学院" min-width="180" />
      <el-table-column prop="grade" label="年级" min-width="100" />
      <el-table-column prop="phone" label="联系电话" min-width="140" />

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
      title="学生详情"
      width="900px"
      align-center
      destroy-on-close
      :close-on-click-modal="false"
      class="student-detail-dialog"
  >
    <div v-loading="detailLoading" class="dialog-content">
      <template v-if="studentDetail">
        <el-card shadow="never" class="detail-card">
          <template #header>
            <span>基本信息</span>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="学号">
              {{ studentDetail.studentId || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="姓名">
              {{ studentDetail.name || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="学院">
              {{ studentDetail.college || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="班级">
              {{ studentDetail.className || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ studentDetail.grade || "-" }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ studentDetail.phone || "-" }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="detail-card">
          <template #header>
            <span>心理测试汇总记录</span>
          </template>

          <el-table
              :data="pagedAssessmentSummaries"
              border
              style="width: 100%"
              empty-text="暂无心理测试汇总记录"
          >
            <el-table-column prop="semester" label="学期" min-width="120" />
            <el-table-column prop="testedCount" label="测评次数" width="100" />
            <el-table-column prop="scoreSummary" label="分数汇总" min-width="180" />
            <el-table-column prop="semesterLevel" label="学期等级" width="120">
              <template #default="scope">
                <el-tag
                    v-if="scope.row.semesterLevel"
                    :type="getLevelTagType(scope.row.semesterLevel)"
                >
                  {{ scope.row.semesterLevel }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="最近测试时间" min-width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.lastTestedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="创建时间" min-width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>

          <div class="inner-pagination" v-if="assessmentTotal > 0">
            <el-pagination
                v-model:current-page="assessmentPageNum"
                :page-size="assessmentPageSize"
                :total="assessmentTotal"
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

const keyword = ref("")
const selectedClass = ref("")
const defaultClassValue = ref("")
const classOptions = ref([{ label: "全部", value: "" }])

const studentList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const tableLoading = ref(false)

const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const studentDetail = ref(null)

const assessmentPageNum = ref(1)
const assessmentPageSize = ref(4)

const assessmentTotal = computed(() => {
  return studentDetail.value?.assessmentSummaries?.length || 0
})

const pagedAssessmentSummaries = computed(() => {
  const list = studentDetail.value?.assessmentSummaries || []
  const start = (assessmentPageNum.value - 1) * assessmentPageSize.value
  const end = start + assessmentPageSize.value
  return list.slice(start, end)
})

async function loadClassOptions() {
  try {
    const res = await request.get("/api/counselor/student/classes")

    if (res.code === 200) {
      const actualClassList = res.data || []
      classOptions.value = [
        { label: "全部", value: "" },
        ...actualClassList.map(item => ({
          label: item,
          value: item
        }))
      ]

      defaultClassValue.value = ""
      selectedClass.value = ""
      return true
    } else {
      ElMessage.error(res.message || "查询班级列表失败")
      return false
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "查询班级列表失败")
    return false
  }
}

async function loadStudentList() {
  tableLoading.value = true
  try {
    const res = await request.post("/api/counselor/student/list", {
      className: selectedClass.value,
      keyword: keyword.value.trim(),
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })

    if (res.code === 200) {
      studentList.value = res.data?.list || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.message || "查询学生列表失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "查询学生列表失败")
  } finally {
    tableLoading.value = false
  }
}

async function handleViewDetail(row) {
  detailDialogVisible.value = true
  detailLoading.value = true
  studentDetail.value = null
  assessmentPageNum.value = 1

  try {
    const res = await request.get("/api/counselor/student/detail", {
      params: {
        studentId: row.studentId
      }
    })

    if (res.code === 200) {
      studentDetail.value = res.data || {}
    } else {
      ElMessage.error(res.message || "查询学生详情失败")
      detailDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "查询学生详情失败")
    detailDialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadStudentList()
}

function handleReset() {
  keyword.value = ""
  selectedClass.value = defaultClassValue.value
  pageNum.value = 1
  pageSize.value = 10
  loadStudentList()
}

function handleCurrentChange(val) {
  pageNum.value = val
  loadStudentList()
}

function handleSizeChange(val) {
  pageSize.value = val
  pageNum.value = 1
  loadStudentList()
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
  if (level.includes("高")) return "danger"
  if (level.includes("中")) return "warning"
  if (level.includes("低")) return "success"
  return "info"
}

onMounted(async () => {
  const ok = await loadClassOptions()
  if (ok) {
    loadStudentList()
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

.class-select {
  width: 220px;
}

.search-input {
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
.student-detail-dialog .el-dialog {
  margin: 0 !important;
}

.student-detail-dialog .el-dialog__body {
  padding-top: 10px;
  padding-bottom: 10px;
}
</style>