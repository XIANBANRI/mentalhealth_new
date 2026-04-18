<template>
  <div class="schedule-manage-page">
    <el-card class="page-card">
      <template #header>
        <span>工作时间管理</span>
      </template>

      <div class="toolbar">
        <el-button type="primary" @click="openAddDialog">新增工作时间</el-button>
      </div>

      <el-table :data="scheduleList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="weekDay" label="星期" width="120">
          <template #default="scope">
            {{ formatWeekDay(scope.row.weekDay) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="150">
          <template #default="scope">
            {{ formatTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="150">
          <template #default="scope">
            {{ formatTime(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="maxAppointments" label="最大预约人数" width="140" />
        <el-table-column prop="remark" label="备注" min-width="180" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEditDialog(scope.row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteSchedule(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增工作时间' : '编辑工作时间'"
        width="520px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="星期" prop="weekDay">
          <el-select v-model="form.weekDay" placeholder="请选择星期" style="width: 100%">
            <el-option label="星期一" :value="1" />
            <el-option label="星期二" :value="2" />
            <el-option label="星期三" :value="3" />
            <el-option label="星期四" :value="4" />
            <el-option label="星期五" :value="5" />
            <el-option label="星期六" :value="6" />
            <el-option label="星期日" :value="7" />
          </el-select>
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-time-select
              v-model="form.startTime"
              start="08:00"
              step="00:30"
              end="20:00"
              placeholder="选择开始时间"
              format="HH:mm"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-time-select
              v-model="form.endTime"
              start="08:30"
              step="00:30"
              end="20:30"
              placeholder="选择结束时间"
              format="HH:mm"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="最大预约人数" prop="maxAppointments">
          <el-input-number v-model="form.maxAppointments" :min="1" :max="20" style="width: 100%" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
              v-model="form.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import request from "@/utils/request"

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref("add")
const scheduleList = ref([])
const formRef = ref()

const form = reactive({
  id: null,
  weekDay: "",
  startTime: "",
  endTime: "",
  maxAppointments: 1,
  remark: ""
})

const rules = {
  weekDay: [{ required: true, message: "请选择星期", trigger: "change" }],
  startTime: [{ required: true, message: "请选择开始时间", trigger: "change" }],
  endTime: [{ required: true, message: "请选择结束时间", trigger: "change" }],
  maxAppointments: [{ required: true, message: "请输入最大预约人数", trigger: "change" }]
}

const formatWeekDay = (value) => {
  const map = {
    1: "星期一",
    2: "星期二",
    3: "星期三",
    4: "星期四",
    5: "星期五",
    6: "星期六",
    7: "星期日"
  }
  return map[value] || "未知"
}

const formatTime = (time) => {
  if (!time) return ""
  return String(time).slice(0, 5)
}

const loadScheduleList = async () => {
  const teacherAccount =
      localStorage.getItem("teacherAccount") || localStorage.getItem("username")

  if (!teacherAccount) {
    ElMessage.error("未获取到老师账号")
    return
  }

  loading.value = true
  try {
    const result = await request.post("/api/teacher/schedule/query", {
      teacherAccount
    })

    const success = result?.code === 200 || result?.success === true

    if (success) {
      scheduleList.value = result?.data || []
    } else {
      ElMessage.error(result?.message || "加载失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "加载失败")
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.weekDay = ""
  form.startTime = ""
  form.endTime = ""
  form.maxAppointments = 1
  form.remark = ""
}

const openAddDialog = () => {
  dialogType.value = "add"
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogType.value = "edit"
  form.id = row.id
  form.weekDay = row.weekDay
  form.startTime = formatTime(row.startTime)
  form.endTime = formatTime(row.endTime)
  form.maxAppointments = row.maxAppointments
  form.remark = row.remark || ""
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    const teacherAccount =
        localStorage.getItem("teacherAccount") || localStorage.getItem("username")

    try {
      const url =
          dialogType.value === "add"
              ? "/api/teacher/schedule/add"
              : "/api/teacher/schedule/update"

      const payload = {
        id: form.id,
        teacherAccount,
        weekDay: form.weekDay,
        startTime: form.startTime,
        endTime: form.endTime,
        maxAppointments: form.maxAppointments,
        remark: form.remark
      }

      const result = await request.post(url, payload)
      const success = result?.code === 200 || result?.success === true

      if (success) {
        ElMessage.success(result?.message || (dialogType.value === "add" ? "新增成功" : "修改成功"))
        dialogVisible.value = false
        loadScheduleList()
      } else {
        ElMessage.error(result?.message || "保存失败")
      }
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || error?.message || "保存失败")
    }
  })
}

const deleteSchedule = async (row) => {
  const teacherAccount =
      localStorage.getItem("teacherAccount") || localStorage.getItem("username")

  try {
    await ElMessageBox.confirm("确认删除该工作时间吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    })

    const result = await request.post("/api/teacher/schedule/delete", {
      id: row.id,
      teacherAccount
    })

    const success = result?.code === 200 || result?.success === true

    if (success) {
      ElMessage.success(result?.message || "删除成功")
      loadScheduleList()
    } else {
      ElMessage.error(result?.message || "删除失败")
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error?.response?.data?.message || error?.message || "删除失败")
    }
  }
}

onMounted(() => {
  loadScheduleList()
})
</script>

<style scoped>
.schedule-manage-page {
  min-height: 100%;
}

.page-card {
  border-radius: 10px;
}

.toolbar {
  margin-bottom: 18px;
}
</style>