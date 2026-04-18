<template>
  <div class="appointment-record-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span>预约记录</span>
        </div>
      </template>

      <el-form :model="queryForm" inline class="search-form">
        <el-form-item label="学生学号">
          <el-input
              v-model="queryForm.studentId"
              placeholder="请输入学生学号"
              clearable
              style="width: 180px"
          />
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker
              v-model="queryForm.appointmentDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择预约日期"
              clearable
              style="width: 180px"
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadRecordList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
          :data="pagedRecordList"
          border
          style="width: 100%"
          v-loading="loading"
          empty-text="暂无预约记录"
      >
        <el-table-column label="序号" width="80" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>

        <el-table-column prop="appointmentNo" label="预约编号" min-width="160" />
        <el-table-column prop="studentId" label="学生学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="90" />
        <el-table-column prop="endTime" label="结束时间" width="90" />
        <el-table-column prop="purpose" label="预约原因" min-width="140" show-overflow-tooltip />
        <el-table-column prop="remark" label="学生备注" min-width="140" show-overflow-tooltip />

        <el-table-column label="会诊记录" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.offlineRecord || "暂无" }}
          </template>
        </el-table-column>

        <el-table-column label="拒绝原因" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.rejectReason || "暂无" }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column prop="approvedAt" label="通过时间" min-width="170" />
        <el-table-column prop="completedAt" label="完成时间" min-width="170" />
      </el-table>

      <div class="pagination-box" v-if="recordList.length > 0">
        <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :total="recordList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue"
import { ElMessage } from "element-plus"
import request from "@/utils/request"

const loading = ref(false)
const recordList = ref([])

const currentPage = ref(1)
const pageSize = ref(7)

const queryForm = reactive({
  studentId: "",
  appointmentDate: "",
  status: ""
})

const teacherAccount = computed(() => {
  return localStorage.getItem("teacherAccount") || localStorage.getItem("username") || ""
})

const pagedRecordList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return recordList.value.slice(start, end)
})

const getStatusText = (status) => {
  const map = {
    PENDING: "待处理",
    APPROVED: "已通过",
    REJECTED: "已拒绝",
    COMPLETED: "已完成",
    CANCELLED: "已取消"
  }
  return map[status] || status || "未知"
}

const getStatusTag = (status) => {
  const map = {
    PENDING: "warning",
    APPROVED: "success",
    REJECTED: "danger",
    COMPLETED: "primary",
    CANCELLED: "info"
  }
  return map[status] || "info"
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const loadRecordList = async () => {
  if (!teacherAccount.value) {
    ElMessage.error("未获取到老师账号")
    return
  }

  loading.value = true
  try {
    const result = await request.post("/api/teacher/appointment/record", {
      teacherAccount: teacherAccount.value,
      studentId: queryForm.studentId,
      appointmentDate: queryForm.appointmentDate,
      status: queryForm.status
    })

    if (result?.code === 200 || result?.success === true) {
      recordList.value = result?.data || []
      currentPage.value = 1
    } else {
      recordList.value = []
      currentPage.value = 1
      ElMessage.error(result?.message || "查询失败")
    }
  } catch (error) {
    recordList.value = []
    currentPage.value = 1
    ElMessage.error(error?.response?.data?.message || error?.message || "查询失败")
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.studentId = ""
  queryForm.appointmentDate = ""
  queryForm.status = ""
  currentPage.value = 1
  loadRecordList()
}

onMounted(() => {
  loadRecordList()
})
</script>

<style scoped>
.appointment-record-page {
  min-height: 100%;
}

.page-card {
  border-radius: 10px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-form {
  margin-bottom: 18px;
}

.pagination-box {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}
</style>