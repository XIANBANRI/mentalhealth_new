<template>
  <el-card class="page-card">
    <template #header>
      <span>预约情况</span>
    </template>

    <el-table :data="appointmentList" border style="width: 100%">
      <el-table-column prop="studentId" label="学号" width="120" />
      <el-table-column prop="studentName" label="学生姓名" width="100" />
      <el-table-column prop="teacherName" label="心理老师" width="100" />
      <el-table-column prop="appointmentDate" label="预约日期" width="120" />
      <el-table-column prop="timeSlot" label="预约时段" width="150" />
      <el-table-column prop="status" label="预约状态" width="100">
        <template #default="scope">
          <el-tag
              :type="
              scope.row.status === '已预约'
                ? 'primary'
                : scope.row.status === '已完成'
                ? 'success'
                : 'info'
            "
          >
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from "vue"

const appointmentList = ref([
  {
    studentId: "20230001",
    studentName: "张三",
    teacherName: "刘老师",
    appointmentDate: "2026-03-22",
    timeSlot: "09:00-10:00",
    status: "已预约",
    remark: "首次咨询"
  },
  {
    studentId: "20230002",
    studentName: "李四",
    teacherName: "陈老师",
    appointmentDate: "2026-03-19",
    timeSlot: "15:00-16:00",
    status: "已完成",
    remark: "复诊咨询"
  },
  {
    studentId: "20230003",
    studentName: "王五",
    teacherName: "周老师",
    appointmentDate: "2026-03-25",
    timeSlot: "10:00-11:00",
    status: "已预约",
    remark: "辅导员关注"
  }
])
</script>

<style scoped>
.page-card {
  border-radius: 8px;
}
</style>