<template>
  <el-card class="page-card">
    <template #header>
      <div class="card-header">
        <span>测试记录</span>
        <el-button type="primary" plain size="small" @click="loadRecords">
          刷新
        </el-button>
      </div>
    </template>

    <el-table
        :data="recordList"
        style="width: 100%"
        border
        v-loading="loading"
        row-key="summaryId"
    >
      <el-table-column type="expand">
        <template #default="scope">
          <div class="detail-wrapper">
            <div class="detail-title">本学期测试详情</div>

            <el-table
                :data="scope.row.details || []"
                border
                size="small"
                style="width: 100%"
            >
              <el-table-column prop="scaleCode" label="量表编码" width="120" />
              <el-table-column prop="scaleName" label="量表名称" min-width="160" />
              <el-table-column prop="rawScore" label="得分" width="80" />

              <el-table-column label="等级" width="100">
                <template #default="detailScope">
                  <el-tag
                      :type="getLevelTagType(detailScope.row.resultLevel)"
                      size="small"
                  >
                    {{ detailScope.row.resultLevel || "-" }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column
                  prop="resultSummary"
                  label="结果说明"
                  min-width="220"
                  show-overflow-tooltip
              />
              <el-table-column
                  prop="suggestion"
                  label="建议"
                  min-width="240"
                  show-overflow-tooltip
              />
              <el-table-column prop="submittedAt" label="提交时间" width="180" />
            </el-table>

            <el-empty
                v-if="!scope.row.details || scope.row.details.length === 0"
                description="暂无详情记录"
            />
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column prop="testedCount" label="已测数量" width="100" />

      <el-table-column label="分数汇总" min-width="260">
        <template #default="scope">
          <div class="score-summary">
            {{ scope.row.scoreSummary || "-" }}
          </div>
        </template>
      </el-table-column>

      <el-table-column label="学期总等级" width="120">
        <template #default="scope">
          <el-tag :type="getSemesterTagType(scope.row.semesterLevel)">
            {{ scope.row.semesterLevel || "-" }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && recordList.length === 0" description="暂无测试记录" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { ElMessage } from "element-plus"
import request from "@/utils/request"

const loading = ref(false)
const recordList = ref([])

const getLevelTagType = (level) => {
  if (!level) return "info"
  if (level.includes("重度") || level.includes("严重")) return "danger"
  if (level.includes("中度")) return "warning"
  if (level.includes("轻度") || level.includes("轻微")) return "warning"
  if (level.includes("正常") || level.includes("无")) return "success"
  return "info"
}

const getSemesterTagType = (level) => {
  if (level === "危险") return "danger"
  if (level === "轻危") return "warning"
  if (level === "无") return "success"
  if (level === "未完成") return "info"
  return "info"
}

const getSemesterOrder = (semester) => {
  if (!semester) return 999

  const text = String(semester).trim()

  const orderMap = {
    "第1学期": 1,
    "第2学期": 2,
    "第3学期": 3,
    "第4学期": 4,
    "第5学期": 5,
    "第6学期": 6,
    "第7学期": 7,
    "第8学期": 8
  }

  if (orderMap[text]) {
    return orderMap[text]
  }

  const match = text.match(/第\s*(\d+)\s*学期/)
  if (match) {
    return Number(match[1])
  }

  return 999
}

const sortRecordList = (list) => {
  return [...list].sort((a, b) => {
    const orderA = getSemesterOrder(a.semester)
    const orderB = getSemesterOrder(b.semester)
    return orderA - orderB
  })
}

const loadRecords = async () => {
  const studentId = localStorage.getItem("studentId")
  if (!studentId) {
    ElMessage.error("未获取到学生学号，请重新登录")
    return
  }

  loading.value = true
  try {
    const res = await request.get(`/api/student/assessment/records/${studentId}`)
    if (res?.success) {
      const rawList = res.data || []
      recordList.value = sortRecordList(rawList)
    } else {
      recordList.value = []
      ElMessage.error(res?.message || "测试记录加载失败")
    }
  } catch (error) {
    recordList.value = []
    ElMessage.error(error?.response?.data?.message || error?.message || "测试记录加载失败")
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.page-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.score-summary {
  white-space: normal;
  line-height: 1.8;
  word-break: break-word;
}

.detail-wrapper {
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 8px;
}

.detail-title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
</style>