<template>
  <div class="appointment-apply-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="page-card">
          <template #header>
            <div class="card-header">
              <span>心理咨询预约</span>
              <div class="header-actions">
                <el-date-picker
                    v-model="queryDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                    format="YYYY-MM-DD"
                    placeholder="请选择预约日期"
                    :clearable="false"
                    :disabled-date="disabledPastDate"
                    @change="handleDateChange"
                />
                <el-button type="primary" @click="loadAvailableList">
                  查询当天值班老师
                </el-button>
              </div>
            </div>
          </template>

          <el-alert
              title="请选择日期后查看当天有排班的心理老师，并根据剩余名额提交预约。今天之前的日期不可预约；同一天同一时间段只能预约一个老师。"
              type="info"
              :closable="false"
              show-icon
              class="mb-16"
          />

          <el-table
              :data="availableList"
              border
              style="width: 100%"
              v-loading="loading"
              empty-text="该日期暂无可预约老师"
          >
            <el-table-column prop="teacherName" label="老师姓名" min-width="120" />
            <el-table-column prop="officeLocation" label="办公地点" min-width="180" />
            <el-table-column prop="phone" label="联系电话" min-width="130" />
            <el-table-column label="星期" width="100">
              <template #default="scope">
                {{ formatWeekDay(scope.row.weekDay) }}
              </template>
            </el-table-column>
            <el-table-column label="咨询时间" min-width="150">
              <template #default="scope">
                {{ scope.row.startTime }} - {{ scope.row.endTime }}
              </template>
            </el-table-column>
            <el-table-column prop="maxAppointments" label="总名额" width="90" />
            <el-table-column prop="usedAppointments" label="已预约" width="90" />
            <el-table-column label="剩余名额" width="100">
              <template #default="scope">
                <span :class="scope.row.remainingAppointments > 0 ? 'remain-ok' : 'remain-empty'">
                  {{ scope.row.remainingAppointments }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
                    type="primary"
                    size="small"
                    :disabled="scope.row.remainingAppointments <= 0 || hasSameTimeAppointment(scope.row)"
                    @click="openApplyDialog(scope.row)"
                >
                  {{ hasSameTimeAppointment(scope.row) ? "该时段已预约" : "预约" }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="tips-area" v-if="availableList.length > 0">
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
        v-model="dialogVisible"
        title="提交预约申请"
        width="600px"
        destroy-on-close
    >
      <el-form
          ref="applyFormRef"
          :model="applyForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="预约日期">
          <el-input v-model="applyForm.appointmentDate" disabled />
        </el-form-item>

        <el-form-item label="老师姓名">
          <el-input :value="currentRow.teacherName || ''" disabled />
        </el-form-item>

        <el-form-item label="咨询时间">
          <el-input :value="timeRangeText" disabled />
        </el-form-item>

        <el-form-item label="办公地点">
          <el-input :value="currentRow.officeLocation || ''" disabled />
        </el-form-item>

        <el-form-item label="预约目的" prop="purpose">
          <el-input
              v-model="applyForm.purpose"
              type="textarea"
              :rows="4"
              maxlength="255"
              show-word-limit
              placeholder="请简要填写你的预约目的，例如：学习压力、人际关系、情绪困扰等"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
              v-model="applyForm.remark"
              type="textarea"
              :rows="3"
              maxlength="500"
              show-word-limit
              placeholder="可填写补充说明，没有可不填"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApply">
          提交预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "AppointmentApply",
  data() {
    return {
      loading: false,
      submitLoading: false,
      queryDate: this.formatDate(new Date()),
      availableList: [],
      myAppointmentList: [],
      dialogVisible: false,
      currentRow: {},
      applyForm: {
        scheduleId: null,
        appointmentDate: "",
        purpose: "",
        remark: ""
      },
      rules: {
        purpose: [
          { required: true, message: "请输入预约目的", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    timeRangeText() {
      if (!this.currentRow || !this.currentRow.startTime) return "";
      return `${this.currentRow.startTime} - ${this.currentRow.endTime}`;
    }
  },
  async mounted() {
    await this.loadMyAppointments();
    await this.loadAvailableList();
  },
  methods: {
    normalizeResult(res) {
      if (res && typeof res === "object" && Object.prototype.hasOwnProperty.call(res, "code")) {
        return res;
      }
      if (
          res &&
          typeof res === "object" &&
          Object.prototype.hasOwnProperty.call(res, "data") &&
          res.data &&
          typeof res.data === "object" &&
          Object.prototype.hasOwnProperty.call(res.data, "code")
      ) {
        return res.data;
      }
      return res;
    },

    formatDate(date) {
      const year = date.getFullYear();
      const month = `${date.getMonth() + 1}`.padStart(2, "0");
      const day = `${date.getDate()}`.padStart(2, "0");
      return `${year}-${month}-${day}`;
    },

    disabledPastDate(time) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return time.getTime() < today.getTime();
    },

    formatWeekDay(weekDay) {
      const map = {
        1: "星期一",
        2: "星期二",
        3: "星期三",
        4: "星期四",
        5: "星期五",
        6: "星期六",
        7: "星期日"
      };
      return map[weekDay] || "-";
    },

    async handleDateChange() {
      this.availableList = [];
      this.currentRow = {};
      this.dialogVisible = false;
      this.applyForm = {
        scheduleId: null,
        appointmentDate: this.queryDate,
        purpose: "",
        remark: ""
      };

      await this.loadAvailableList();
    },

    async loadMyAppointments() {
      try {
        const res = await request.get("/api/student/appointment/my");
        const result = this.normalizeResult(res);
        if (result.code === 200 || result.success === true) {
          this.myAppointmentList = result.data || [];
        } else {
          this.myAppointmentList = [];
        }
      } catch (e) {
        this.myAppointmentList = [];
      }
    },

    hasSameTimeAppointment(row) {
      const list = this.myAppointmentList || [];
      return list.some(item => {
        return (
            item.appointmentDate === this.queryDate &&
            item.startTime === row.startTime &&
            item.endTime === row.endTime &&
            (item.status === "PENDING" || item.status === "APPROVED")
        );
      });
    },

    async loadAvailableList() {
      if (!this.queryDate) {
        this.$message.warning("请选择预约日期");
        return;
      }

      const today = this.formatDate(new Date());
      if (this.queryDate < today) {
        this.$message.warning("不能查询今天之前的日期");
        return;
      }

      this.loading = true;
      try {
        await this.loadMyAppointments();

        const res = await request.get("/api/student/appointment/available", {
          params: {
            date: this.queryDate
          }
        });

        const result = this.normalizeResult(res);
        if (result.code === 200 || result.success === true) {
          this.availableList = result.data || [];
        } else {
          this.availableList = [];
          this.$message.error(result.message || "查询失败");
        }
      } catch (e) {
        this.availableList = [];
        this.$message.error(e?.response?.data?.message || "查询失败，请稍后重试");
      } finally {
        this.loading = false;
      }
    },

    openApplyDialog(row) {
      if (!this.queryDate) {
        this.$message.warning("请先选择预约日期");
        return;
      }

      const today = this.formatDate(new Date());
      if (this.queryDate < today) {
        this.$message.warning("不能预约今天之前的日期");
        return;
      }

      if (!row || !row.scheduleId) {
        this.$message.warning("请先查询当天值班老师");
        return;
      }

      const exists = this.availableList.some(item => item.scheduleId === row.scheduleId);
      if (!exists) {
        this.$message.warning("当前老师排班已变化，请重新查询当天值班老师");
        return;
      }

      if (this.hasSameTimeAppointment(row)) {
        this.$message.warning("同一天同一时间段只能预约一个老师");
        return;
      }

      this.currentRow = { ...row };
      this.applyForm = {
        scheduleId: row.scheduleId,
        appointmentDate: this.queryDate,
        purpose: "",
        remark: ""
      };
      this.dialogVisible = true;

      this.$nextTick(() => {
        if (this.$refs.applyFormRef) {
          this.$refs.applyFormRef.clearValidate();
        }
      });
    },

    submitApply() {
      if (!this.$refs.applyFormRef) return;

      this.$refs.applyFormRef.validate(async (valid) => {
        if (!valid) return;

        const today = this.formatDate(new Date());
        if (this.applyForm.appointmentDate < today) {
          this.$message.warning("不能预约今天之前的日期");
          return;
        }

        const matchedRow = this.availableList.find(
            item => item.scheduleId === this.applyForm.scheduleId
        );
        if (!matchedRow) {
          this.$message.warning("当前排班数据已失效，请重新查询当天值班老师后再预约");
          return;
        }

        if (this.hasSameTimeAppointment(matchedRow)) {
          this.$message.warning("同一天同一时间段只能预约一个老师");
          return;
        }

        this.submitLoading = true;
        try {
          const payload = {
            scheduleId: this.applyForm.scheduleId,
            appointmentDate: this.applyForm.appointmentDate,
            purpose: this.applyForm.purpose,
            remark: this.applyForm.remark
          };

          const res = await request.post("/api/student/appointment/create", payload);
          const result = this.normalizeResult(res);

          if (result.code === 200 || result.success === true) {
            this.$message.success(result.message || "预约提交成功");
            this.dialogVisible = false;
            await this.loadMyAppointments();
            await this.loadAvailableList();
          } else {
            this.$message.error(result.message || "预约提交失败");
          }
        } catch (e) {
          this.$message.error(
              e?.response?.data?.message || "预约提交失败，请稍后重试"
          );
        } finally {
          this.submitLoading = false;
        }
      });
    }
  }
};
</script>

<style scoped>
.appointment-apply-page {
  padding: 20px;
}

.page-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mb-16 {
  margin-bottom: 16px;
}

.tips-area {
  margin-top: 16px;
}

.remain-ok {
  color: #67c23a;
  font-weight: 600;
}

.remain-empty {
  color: #f56c6c;
  font-weight: 600;
}
</style>