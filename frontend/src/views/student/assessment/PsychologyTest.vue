<template>
  <div class="assessment-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="page-card">
          <template #header>
            <div class="card-header">
              <span>心理测试列表</span>
            </div>
          </template>

          <div class="semester-box">
            <span class="semester-label">当前学期：</span>
            <el-select v-model="selectedSemester" placeholder="请选择学期" style="width: 180px">
              <el-option
                  v-for="item in semesterOptions"
                  :key="item"
                  :label="item"
                  :value="item"
              />
            </el-select>
          </div>

          <el-table :data="scaleList" style="width: 100%" v-loading="listLoading">
            <el-table-column prop="scaleName" label="量表名称" min-width="160" />
            <el-table-column prop="questionCount" label="题数" width="80" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="primary" size="small" @click="loadDetail(scope.row)">
                  开始测试
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="page-card" v-if="currentScale && currentQuestion">
          <template #header>
            <div class="card-header">
              <span>{{ currentScale.scaleName }}</span>
              <el-tag type="info">
                第 {{ currentQuestionIndex + 1 }} / {{ totalQuestionCount }} 题
              </el-tag>
            </div>
          </template>

          <div class="scale-desc">{{ currentScale.description || "暂无量表说明" }}</div>

          <div class="progress-box">
            <el-progress
                :percentage="progressPercent"
                :stroke-width="14"
                :show-text="true"
            />
          </div>

          <div class="question-block">
            <div class="question-title">
              {{ currentQuestion.questionNo }}. {{ currentQuestion.questionText }}
            </div>

            <el-radio-group v-model="answerMap[currentQuestion.id]" class="option-group">
              <el-radio
                  v-for="option in currentQuestion.options"
                  :key="option.id"
                  :label="option.id"
                  class="option-item"
              >
                {{ option.optionText }}
              </el-radio>
            </el-radio-group>
          </div>

          <div class="submit-area">
            <div class="left-btn">
              <el-button v-if="currentQuestionIndex > 0" @click="prevQuestion">
                上一题
              </el-button>

              <el-button @click="resetAnswers">
                重置作答
              </el-button>
            </div>

            <div class="right-btn">
              <el-button
                  v-if="currentQuestionIndex < totalQuestionCount - 1"
                  type="primary"
                  @click="nextQuestion"
              >
                下一题
              </el-button>

              <el-button
                  v-if="currentQuestionIndex === totalQuestionCount - 1"
                  type="primary"
                  :loading="submitLoading"
                  @click="submitAssessment"
              >
                提交测试
              </el-button>
            </div>
          </div>
        </el-card>

        <el-empty v-else description="请选择左侧量表开始测试" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import request from "@/utils/request"

const listLoading = ref(false)
const submitLoading = ref(false)

const scaleList = ref([])
const currentScale = ref(null)
const currentScaleId = ref(null)
const currentVersionId = ref(null)
const currentQuestionIndex = ref(0)
const answerMap = reactive({})

const selectedSemester = ref("第1学期")
const semesterOptions = [
  "第1学期",
  "第2学期",
  "第3学期",
  "第4学期",
  "第5学期",
  "第6学期",
  "第7学期",
  "第8学期"
]

const totalQuestionCount = computed(() => currentScale.value?.questions?.length || 0)

const currentQuestion = computed(() => {
  if (!currentScale.value?.questions?.length) return null
  return currentScale.value.questions[currentQuestionIndex.value] || null
})

const progressPercent = computed(() => {
  if (!totalQuestionCount.value) return 0
  return Math.round(((currentQuestionIndex.value + 1) / totalQuestionCount.value) * 100)
})

const clearAnswerMap = () => {
  Object.keys(answerMap).forEach((key) => delete answerMap[key])
}

const resetAnswers = () => {
  clearAnswerMap()
  currentQuestionIndex.value = 0
  ElMessage.success("已重置当前作答")
}

const loadScaleList = async () => {
  listLoading.value = true
  try {
    const res = await request.get("/api/student/assessment/scales")
    if (res?.success) {
      scaleList.value = res.data || []
    } else {
      ElMessage.error(res?.message || "量表列表加载失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "量表列表加载失败")
  } finally {
    listLoading.value = false
  }
}

const loadDetail = async (scale) => {
  try {
    const res = await request.get(`/api/student/assessment/detail/${scale.id}`)
    if (res?.success) {
      currentScale.value = res.data
      currentScaleId.value = res.data?.scaleId || scale.id
      currentVersionId.value = res.data?.versionId || null
      currentQuestionIndex.value = 0
      clearAnswerMap()
    } else {
      ElMessage.error(res?.message || "量表详情加载失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "量表详情加载失败")
  }
}

const nextQuestion = () => {
  if (!currentQuestion.value) return

  const selectedOptionId = answerMap[currentQuestion.value.id]
  if (!selectedOptionId) {
    ElMessage.warning("请先完成当前题目")
    return
  }

  if (currentQuestionIndex.value < totalQuestionCount.value - 1) {
    currentQuestionIndex.value += 1
  }
}

const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value -= 1
  }
}

const buildResultMessage = (data) => {
  return [
    `量表：${data.scaleName || ""}`,
    `学期：${data.semester || selectedSemester.value}`,
    `本次得分：${data.rawScore ?? 0}`,
    `结果等级：${data.resultLevel || "未分级"}`,
    `结果说明：${data.resultSummary || "暂无说明"}`,
    `建议：${data.suggestion || "暂无建议"}`
  ].join("\n")
}

const submitAssessment = async () => {
  const studentId = localStorage.getItem("studentId")

  if (!studentId) {
    ElMessage.error("未获取到学生学号，请重新登录")
    return
  }

  if (!currentScale.value || !currentScaleId.value) {
    ElMessage.error("请先选择量表")
    return
  }

  if (!currentVersionId.value) {
    ElMessage.error("当前量表版本信息缺失，无法提交")
    return
  }

  const questions = currentScale.value.questions || []
  const answers = []

  for (let i = 0; i < questions.length; i++) {
    const question = questions[i]
    const selectedOptionId = answerMap[question.id]

    if (!selectedOptionId) {
      ElMessage.error(`请完成第 ${question.questionNo} 题`)
      currentQuestionIndex.value = i
      return
    }

    answers.push({
      versionQuestionId: question.id,
      versionOptionId: selectedOptionId
    })
  }

  submitLoading.value = true
  try {
    const res = await request.post("/api/student/assessment/submit", {
      studentId,
      semester: selectedSemester.value,
      scaleId: currentScaleId.value,
      versionId: currentVersionId.value,
      answers
    })

    if (res?.success) {
      const data = res.data || {}

      await ElMessageBox.alert(buildResultMessage(data), "测试完成", {
        confirmButtonText: "确定"
      })

      currentScale.value = null
      currentScaleId.value = null
      currentVersionId.value = null
      currentQuestionIndex.value = 0
      clearAnswerMap()
      loadScaleList()
    } else {
      ElMessage.error(res?.message || "提交失败")
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || "提交失败")
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadScaleList()
})
</script>

<style scoped>
.assessment-page {
  width: 100%;
}

.page-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.semester-box {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.semester-label {
  margin-right: 10px;
  color: #606266;
}

.scale-desc {
  margin-bottom: 20px;
  color: #606266;
  line-height: 1.8;
}

.progress-box {
  margin-bottom: 20px;
}

.question-block {
  min-height: 260px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  background: #fafafa;
}

.question-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.8;
}

.option-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.option-item {
  display: flex;
  margin: 0;
  line-height: 2;
}

.submit-area {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-btn,
.right-btn {
  display: flex;
  gap: 12px;
}
</style>