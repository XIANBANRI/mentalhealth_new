<template>
  <div class="schedule-query-page">
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="page-card">
          <template #header>
            <span>时间查询（日历）</span>
          </template>

          <el-calendar v-model="selectedDate">
            <template #date-cell="{ data }">
              <div
                  class="calendar-cell"
                  :class="{ 'is-selected': isSameDate(data.day, selectedDate) }"
                  @click="handleDateSelect(data.day)"
              >
                <div class="day-number">
                  {{ getDayNumber(data.day) }}
                </div>
                <div
                    v-if="hasSchedule(data.day)"
                    class="schedule-dot"
                    :class="{ past: isPastDate(data.day) }"
                ></div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="page-card" v-loading="loading">
          <template #header>
            <span>当日工作时间</span>
          </template>

          <div class="date-info">
            <div class="date-text">选择日期：{{ selectedDateText }}</div>
            <div class="week-text">星期：{{ selectedWeekText }}</div>
          </div>

          <el-empty
              v-if="currentScheduleList.length === 0"
              description="当天暂无工作时间安排"
          />

          <div v-else class="schedule-list">
            <div
                class="schedule-item"
                v-for="item in currentScheduleList"
                :key="item.id"
            >
              <div class="time-range">
                {{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}
              </div>
              <div class="item-meta">
                <span class="max-count">最大预约人数：{{ item.maxAppointments }}</span>
              </div>
              <div class="remark" v-if="item.remark">
                备注：{{ item.remark }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { ElMessage } from "element-plus"
import request from "@/utils/request"

const selectedDate = ref(new Date())
const loading = ref(false)
const scheduleList = ref([])

const weekMap = {
  1: "星期一",
  2: "星期二",
  3: "星期三",
  4: "星期四",
  5: "星期五",
  6: "星期六",
  7: "星期日"
}

const formatDate = (date) => {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = `${d.getMonth() + 1}`.padStart(2, "0")
  const day = `${d.getDate()}`.padStart(2, "0")
  return `${year}-${month}-${day}`
}

const getWeekDayByDate = (date) => {
  const day = new Date(date).getDay()
  return day === 0 ? 7 : day
}

const formatTime = (time) => {
  if (!time) return ""
  return String(time).slice(0, 5)
}

const selectedDateText = computed(() => formatDate(selectedDate.value))

const selectedWeekText = computed(() => {
  const weekDay = getWeekDayByDate(selectedDate.value)
  return weekMap[weekDay]
})

const currentScheduleList = computed(() => {
  const weekDay = getWeekDayByDate(selectedDate.value)
  return scheduleList.value.filter(item => Number(item.weekDay) === weekDay)
})

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
      scheduleList.value = []
      ElMessage.error(result?.message || "查询失败")
    }
  } catch (error) {
    scheduleList.value = []
    ElMessage.error(
        error?.response?.data?.message ||
        error?.message ||
        "查询失败"
    )
  } finally {
    loading.value = false
  }
}

const handleDateSelect = (day) => {
  selectedDate.value = new Date(day)
}

const getDayNumber = (day) => {
  return day.split("-")[2]
}

const hasSchedule = (day) => {
  const weekDay = getWeekDayByDate(day)
  return scheduleList.value.some(item => Number(item.weekDay) === weekDay)
}

const isSameDate = (day, dateObj) => {
  return formatDate(dateObj) === day
}

// 判断是否是今天之前的日期
const isPastDate = (day) => {
  const today = formatDate(new Date())
  return day < today
}

onMounted(() => {
  loadScheduleList()
})
</script>

<style scoped>
.schedule-query-page {
  min-height: 100%;
}

.page-card {
  border-radius: 10px;
}

.calendar-cell {
  height: 56px;
  padding: 6px 4px;
  box-sizing: border-box;
  cursor: pointer;
  position: relative;
  border-radius: 6px;
  transition: all 0.2s;
}

.calendar-cell:hover {
  background: #ecf5ff;
}

.calendar-cell.is-selected {
  background: #409eff;
  color: #fff;
}

.day-number {
  font-size: 14px;
  line-height: 20px;
}

.schedule-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #67c23a;
  margin-top: 6px;
}

/* 过去日期灰色点 */
.schedule-dot.past {
  background: #c0c4cc;
}

.date-info {
  margin-bottom: 16px;
  padding: 14px;
  background: #f5f7fa;
  border-radius: 8px;
}

.date-text,
.week-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.schedule-item {
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #ebeef5;
  border-radius: 10px;
}

.time-range {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.item-meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.max-count {
  color: #606266;
  font-size: 14px;
}

.remark {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}
</style>