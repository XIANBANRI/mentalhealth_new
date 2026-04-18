<template>
  <div class="appointment-record-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span>预约记录</span>
        </div>
      </template>

      <el-form :model="queryForm" inline class="search-form">
        <el-form-item label="预约状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="待处理" value="PENDING" />
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
        <el-table-column prop="teacherName" label="心理老师" width="110" />
        <el-table-column prop="teacherPhone" label="联系电话" width="130" />
        <el-table-column prop="officeLocation" label="办公地点" min-width="140" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="90" />
        <el-table-column prop="endTime" label="结束时间" width="90" />
        <el-table-column prop="purpose" label="预约原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />

        <el-table-column label="反馈内容" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.status === 'REJECTED'">
              {{ row.rejectReason || "暂无拒绝原因" }}
            </span>
            <span v-else>
              {{ row.teacherReply || "暂无" }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="提交时间" min-width="170" />
        <el-table-column prop="approvedAt" label="通过时间" min-width="170" />
        <el-table-column prop="completedAt" label="完成时间" min-width="170" />
        <el-table-column prop="cancelledAt" label="取消时间" min-width="170" />

        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
                v-if="canCancel(row.status)"
                type="danger"
                size="small"
                @click="cancelAppointment(row)"
            >
              取消
            </el-button>
            <span v-else class="disabled-text">不可操作</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-box" v-if="filteredRecordList.length > 0">
        <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :total="filteredRecordList.length"
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
import { ElMessage, ElMessageBox } from "element-plus"
import request from "@/utils/request"

const loading = ref(false)
const recordList = ref([])

const currentPage = ref(1)
const pageSize = ref(7)

const queryForm = reactive({
  status: ""
})

const filteredRecordList = computed(() => {
  if (!queryForm.status) {
    return recordList.value
  }
  return recordList.value.filter(item => item.status === queryForm.status)
})

const pagedRecordList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRecordList.value.slice(start, end)
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

const canCancel = (status) => {
  return status === "PENDING" || status === "APPROVED"
}

const normalizeResult = (res) => {
  if (res && typeof res === "object" && Object.prototype.hasOwnProperty.call(res, "code")) {
    return res
  }
  if (
      res &&
      typeof res === "object" &&
      Object.prototype.hasOwnProperty.call(res, "data") &&
      res.data &&
      typeof res.data === "object" &&
      Object.prototype.hasOwnProperty.call(res.data, "code")
  ) {
    return res.data
  }
  return res
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const loadRecordList = async () => {
  loading.value = true
  try {
    const res = await request.get("/api/student/appointment/my")
    const result = normalizeResult(res)

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
  queryForm.status = ""
  currentPage.value = 1
  loadRecordList()
}

const cancelAppointment = async (row) => {
  try {
    await ElMessageBox.confirm("确认取消该预约吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    })

    const res = await request.post("/api/student/appointment/cancel", {
      appointmentId: row.id
    })

    const result = normalizeResult(res)

    if (result?.code === 200 || result?.success === true) {
      ElMessage.success(result?.message || "取消成功")
      await loadRecordList()

      const maxPage = Math.ceil(filteredRecordList.value.length / pageSize.value) || 1
      if (currentPage.value > maxPage) {
        currentPage.value = maxPage
      }
    } else {
      ElMessage.error(result?.message || "取消失败")
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error?.response?.data?.message || error?.message || "取消失败")
    }
  }
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

.disabled-text {
  color: #909399;
  font-size: 13px;
}

.pagination-box {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}
</style>