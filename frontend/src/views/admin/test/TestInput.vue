<template>
  <div class="test-input-page">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <span>心理测试题集管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openImportDialog">新增导入</el-button>
            <el-button @click="loadScaleList">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="scaleList" border stripe v-loading="listLoading" style="width: 100%">
        <el-table-column prop="scaleCode" label="量表编码" min-width="120" />
        <el-table-column prop="scaleName" label="量表名称" min-width="220" />
        <el-table-column prop="scaleType" label="量表类型" min-width="140" />
        <el-table-column prop="questionCount" label="题目数" width="90" align="center" />
        <el-table-column label="分值范围" width="120" align="center">
          <template #default="{ row }">
            {{ row.scoreMin }} ~ {{ row.scoreMax }}
          </template>
        </el-table-column>
        <el-table-column label="版本" width="90" align="center">
          <template #default="{ row }">
            V{{ row.currentVersionNo || 1 }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" size="small" @click="viewDetail(row)">查看</el-button>
              <el-button link type="warning" size="small" @click="openEditDialog(row)">修改</el-button>

              <el-button
                  v-if="row.status === 1"
                  link
                  type="info"
                  size="small"
                  @click="handleDisable(row)"
              >
                停用
              </el-button>
              <el-button
                  v-else
                  link
                  type="success"
                  size="small"
                  @click="handleEnable(row)"
              >
                启用
              </el-button>

              <el-popconfirm
                  title="删除后学生端将不可见，但不会删除历史数据，确定删除吗？"
                  @confirm="handleDelete(row)"
              >
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        v-model="importDialogVisible"
        title="新增导入量表"
        width="760px"
        top="5vh"
        destroy-on-close
        class="import-scale-dialog"
    >
      <el-form
          ref="importFormRef"
          :model="importForm"
          :rules="importRules"
          label-width="110px"
      >

        <el-form-item label="量表编码" prop="scaleCode">
          <el-input v-model="importForm.scaleCode" placeholder="如 HAMD24" />
        </el-form-item>

        <el-form-item label="量表名称" prop="scaleName">
          <el-input v-model="importForm.scaleName" placeholder="请输入量表名称" />
        </el-form-item>

        <el-form-item label="量表类型" prop="scaleType">
          <el-select
              v-model="importForm.scaleType"
              placeholder="请选择量表类型"
              style="width: 100%"
              clearable
          >
            <el-option
                v-for="item in scaleTypeOptions"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="量表描述">
          <el-input
              v-model="importForm.description"
              type="textarea"
              :rows="2"
              resize="none"
              placeholder="请输入量表描述"
          />
        </el-form-item>

        <div class="import-tip">
          请先下载模板，按模板填写后再上传。题目集选项分数可根据自己需求更改
        </div>

        <el-form-item label="模板下载">
          <div class="template-actions">
            <el-button link type="primary" @click="downloadTemplate('question')">
              下载题目模板
            </el-button>
            <el-button link type="primary" @click="downloadTemplate('rule')">
              下载评分判定表模板
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="题目Excel" prop="questionFile">
          <el-upload
              class="upload-block"
              :auto-upload="false"
              :limit="1"
              :file-list="questionFileList"
              accept=".xls,.xlsx"
              :on-change="handleQuestionFileChange"
              :on-remove="handleQuestionFileRemove"
          >
            <el-button type="primary">选择题目文件</el-button>
            <template #tip>
              <div class="el-upload__tip">请上传题目集 Excel 文件</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="规则Excel" prop="ruleFile">
          <el-upload
              class="upload-block"
              :auto-upload="false"
              :limit="1"
              :file-list="ruleFileList"
              accept=".xls,.xlsx"
              :on-change="handleRuleFileChange"
              :on-remove="handleRuleFileRemove"
          >
            <el-button type="primary">选择规则文件</el-button>
            <template #tip>
              <div class="el-upload__tip">请上传评分判定表 Excel 文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importLoading" @click="submitImport">
          确认导入
        </el-button>
      </template>
    </el-dialog>

    <el-drawer
        v-model="detailDrawerVisible"
        title="量表详情"
        size="70%"
        destroy-on-close
        @closed="resetDetailData"
    >
      <div v-loading="detailLoading" class="detail-wrapper" v-if="detailData.scale">
        <el-descriptions :column="2" border class="detail-base">
          <el-descriptions-item label="量表编码">
            {{ detailData.scale.scaleCode }}
          </el-descriptions-item>
          <el-descriptions-item label="量表名称">
            {{ detailData.scale.scaleName }}
          </el-descriptions-item>
          <el-descriptions-item label="量表类型">
            {{ detailData.scale.scaleType }}
          </el-descriptions-item>
          <el-descriptions-item label="版本">
            V{{ detailData.version?.versionNo || 1 }}
          </el-descriptions-item>
          <el-descriptions-item label="题目数">
            {{ detailData.scale.questionCount }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            {{ detailData.scale.status === 1 ? '启用中' : '已停用' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ detailData.scale.description || '暂无' }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">题目集</el-divider>

        <el-collapse>
          <el-collapse-item
              v-for="question in detailData.questions"
              :key="question.id || `${question.questionNo}-${question.questionText}`"
              :title="`第 ${question.questionNo} 题：${question.questionText}`"
              :name="question.id || question.questionNo"
          >
            <el-table :data="question.options || []" border size="small">
              <el-table-column prop="optionNo" label="选项序号" width="100" align="center" />
              <el-table-column prop="optionText" label="选项内容" min-width="260" />
              <el-table-column prop="optionScore" label="分值" width="80" align="center" />
            </el-table>
          </el-collapse-item>
        </el-collapse>

        <el-divider content-position="left">规则集</el-divider>

        <el-table :data="detailData.rules || []" border>
          <el-table-column label="分值范围" min-width="120" align="center">
            <template #default="{ row }">
              {{ row.minScore }} ~ {{ row.maxScore }}
            </template>
          </el-table-column>
          <el-table-column prop="resultLevel" label="等级" min-width="120" />
          <el-table-column prop="resultSummary" label="结果说明" min-width="220" />
          <el-table-column prop="suggestion" label="建议" min-width="240" />
        </el-table>
      </div>
    </el-drawer>

    <el-dialog
        v-model="editDialogVisible"
        title="修改量表"
        width="1000px"
        top="4vh"
        destroy-on-close
        class="edit-scale-dialog"
    >
      <div v-loading="editLoading" class="edit-dialog-content">
        <el-form :model="editForm" label-width="100px" class="edit-base-form">
          <el-form-item label="量表名称">
            <el-input v-model="editForm.scaleName" />
          </el-form-item>
          <el-form-item label="量表类型">
            <el-select
                v-model="editForm.scaleType"
                placeholder="请选择量表类型"
                style="width: 100%"
                clearable
            >
              <el-option
                  v-for="item in scaleTypeOptions"
                  :key="item"
                  :label="item"
                  :value="item"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="量表描述">
            <el-input v-model="editForm.description" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="版本备注">
            <el-input v-model="editForm.versionRemark" placeholder="如：修订第2版" />
          </el-form-item>
        </el-form>

        <el-divider content-position="left">题目集</el-divider>

        <div class="question-toolbar">
          <span>共 {{ editQuestionTotal }} 题，每页 10 题</span>
          <el-button type="primary" plain @click="addQuestion">新增题目</el-button>
        </div>

        <div
            v-for="(question, qIndex) in pagedQuestions"
            :key="`${editQuestionPage}-${qIndex}`"
            class="edit-question-card"
        >
          <div class="question-header">
            <span>题目 {{ getQuestionDisplayNo(qIndex) }}</span>
            <el-button type="danger" link @click="removeQuestion(getQuestionRealIndex(qIndex))">
              删除题目
            </el-button>
          </div>

          <el-form label-width="90px">
            <el-form-item label="题号">
              <el-input-number v-model="question.questionNo" :min="1" :controls="false" />
            </el-form-item>
            <el-form-item label="题目内容">
              <el-input v-model="question.questionText" type="textarea" :rows="2" />
            </el-form-item>
          </el-form>

          <div class="option-area">
            <div class="option-header">
              <span>选项</span>
              <el-button type="primary" link @click="addOption(question)">新增选项</el-button>
            </div>

            <div
                v-for="(option, oIndex) in question.options"
                :key="oIndex"
                class="option-row"
            >
              <el-input-number
                  v-model="option.optionNo"
                  :min="1"
                  :controls="false"
                  class="option-no"
              />
              <el-input v-model="option.optionText" placeholder="选项内容" class="option-text" />
              <el-input-number
                  v-model="option.optionScore"
                  :controls="false"
                  class="option-score"
              />
              <el-button
                  type="danger"
                  link
                  @click="removeOption(question, oIndex)"
                  class="option-delete"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>

        <div class="question-pagination" v-if="editQuestionTotal > editQuestionPageSize">
          <el-pagination
              background
              layout="total, prev, pager, next"
              :total="editQuestionTotal"
              :page-size="editQuestionPageSize"
              :current-page="editQuestionPage"
              @current-change="handleQuestionPageChange"
          />
        </div>

        <el-divider content-position="left">规则集</el-divider>

        <div
            v-for="(rule, rIndex) in editForm.rules"
            :key="rIndex"
            class="rule-row"
        >
          <el-input-number v-model="rule.minScore" :controls="false" />
          <span class="rule-sep">~</span>
          <el-input-number v-model="rule.maxScore" :controls="false" />
          <el-input v-model="rule.resultLevel" placeholder="等级" />
          <el-input v-model="rule.resultSummary" placeholder="结果说明" />
          <el-input v-model="rule.suggestion" placeholder="建议" />
          <el-button type="danger" link @click="removeRule(rIndex)">删除</el-button>
        </div>

        <div class="section-btn">
          <el-button type="primary" plain @click="addRule">新增规则</el-button>
        </div>
      </div>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="updateLoading" @click="submitUpdate">
          保存修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const scaleTypeOptions = [
  '焦虑',
  '抑郁',
  '压力与应激',
  '睡眠',
  '人格',
  '强迫与躯体'
]

const listLoading = ref(false)
const detailLoading = ref(false)
const importLoading = ref(false)
const editLoading = ref(false)
const updateLoading = ref(false)

const scaleList = ref([])

const importDialogVisible = ref(false)
const detailDrawerVisible = ref(false)
const editDialogVisible = ref(false)

const importFormRef = ref()

const questionFileList = ref([])
const ruleFileList = ref([])

const editQuestionPage = ref(1)
const editQuestionPageSize = 10

const importForm = reactive({
  scaleCode: '',
  scaleName: '',
  scaleType: '',
  description: '',
  operator: 'admin',
  questionFile: null,
  ruleFile: null
})

const importRules = {
  scaleCode: [{ required: true, message: '请输入量表编码', trigger: 'blur' }],
  scaleName: [{ required: true, message: '请输入量表名称', trigger: 'blur' }],
  scaleType: [{ required: true, message: '请选择量表类型', trigger: 'change' }],
  questionFile: [{ required: true, message: '请上传题目Excel', trigger: 'change' }],
  ruleFile: [{ required: true, message: '请上传规则Excel', trigger: 'change' }]
}

const detailData = reactive({
  scale: null,
  version: null,
  questions: [],
  rules: []
})

const editForm = reactive({
  scaleId: null,
  scaleName: '',
  scaleType: '',
  description: '',
  versionRemark: '',
  operator: 'admin',
  questions: [],
  rules: []
})

const editQuestionTotal = computed(() => editForm.questions.length)
const pagedQuestions = computed(() => {
  const start = (editQuestionPage.value - 1) * editQuestionPageSize
  const end = start + editQuestionPageSize
  return editForm.questions.slice(start, end)
})

onMounted(() => {
  loadScaleList()
})

function resetDetailData() {
  detailData.scale = null
  detailData.version = null
  detailData.questions = []
  detailData.rules = []
}

function downloadTemplate(type) {
  const fileMap = {
    question: {
      url: '/templates/题目模板.xlsx',
      name: '题目模板.xlsx'
    },
    rule: {
      url: '/templates/评分判定表模板.xlsx',
      name: '评分判定表模板.xlsx'
    }
  }

  const target = fileMap[type]
  if (!target) return

  const link = document.createElement('a')
  link.href = encodeURI(target.url)
  link.download = target.name
  link.style.display = 'none'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

function extractErrorMessage(error, defaultMessage) {
  return (
      error?.response?.data?.message ||
      error?.response?.data?.msg ||
      error?.message ||
      defaultMessage
  )
}

function getQuestionPageCount() {
  return Math.max(1, Math.ceil(editForm.questions.length / editQuestionPageSize))
}

function handleQuestionPageChange(page) {
  editQuestionPage.value = page
}

function getQuestionRealIndex(pageIndex) {
  return (editQuestionPage.value - 1) * editQuestionPageSize + pageIndex
}

function getQuestionDisplayNo(pageIndex) {
  return getQuestionRealIndex(pageIndex) + 1
}

function adjustQuestionPageAfterDelete() {
  const maxPage = getQuestionPageCount()
  if (editQuestionPage.value > maxPage) {
    editQuestionPage.value = maxPage
  }
}

function refreshQuestionNumbers() {
  editForm.questions.forEach((q, idx) => {
    q.questionNo = idx + 1
  })
}

function loadScaleList() {
  listLoading.value = true
  request
  .get('/admin/assessment-scale/list')
  .then((res) => {
    if (res?.code === 200) {
      scaleList.value = res.data || []
    } else {
      ElMessage.error(res?.message || '加载失败')
    }
  })
  .catch((error) => {
    ElMessage.error(extractErrorMessage(error, '加载量表列表失败'))
  })
  .finally(() => {
    listLoading.value = false
  })
}

function resetImportForm() {
  importForm.scaleCode = ''
  importForm.scaleName = ''
  importForm.scaleType = ''
  importForm.description = ''
  importForm.operator = 'admin'
  importForm.questionFile = null
  importForm.ruleFile = null
  questionFileList.value = []
  ruleFileList.value = []
  importFormRef.value?.clearValidate()
}

function openImportDialog() {
  resetImportForm()
  importDialogVisible.value = true
}

function handleQuestionFileChange(file, files) {
  questionFileList.value = files.slice(-1)
  importForm.questionFile = file.raw
}

function handleQuestionFileRemove() {
  questionFileList.value = []
  importForm.questionFile = null
}

function handleRuleFileChange(file, files) {
  ruleFileList.value = files.slice(-1)
  importForm.ruleFile = file.raw
}

function handleRuleFileRemove() {
  ruleFileList.value = []
  importForm.ruleFile = null
}

function submitImport() {
  importFormRef.value.validate((valid) => {
    if (!valid) return

    if (!importForm.questionFile || !importForm.ruleFile) {
      ElMessage.error('请先上传题目和规则文件')
      return
    }

    const formData = new FormData()
    formData.append('scaleCode', importForm.scaleCode)
    formData.append('scaleName', importForm.scaleName)
    formData.append('scaleType', importForm.scaleType)
    formData.append('description', importForm.description || '')
    formData.append('operator', importForm.operator || 'admin')
    formData.append('questionFile', importForm.questionFile)
    formData.append('ruleFile', importForm.ruleFile)

    importLoading.value = true
    request
    .post('/admin/assessment-scale/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    .then((res) => {
      if (res?.code === 200) {
        ElMessage.success(res?.message || '导入成功')
        importDialogVisible.value = false
        loadScaleList()
      } else {
        ElMessage.error(res?.message || '导入失败')
      }
    })
    .catch((error) => {
      ElMessage.error(extractErrorMessage(error, '导入失败'))
    })
    .finally(() => {
      importLoading.value = false
    })
  })
}

async function viewDetail(row) {
  detailLoading.value = true
  detailDrawerVisible.value = false
  resetDetailData()

  try {
    const res = await request.get(`/admin/assessment-scale/detail/${row.scaleId}`)
    if (res?.code === 200 && res.data) {
      detailData.scale = res.data.scale || null
      detailData.version = res.data.version || null
      detailData.questions = res.data.questions || []
      detailData.rules = res.data.rules || []

      await nextTick()
      detailDrawerVisible.value = true
    } else {
      ElMessage.error(res?.message || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error(extractErrorMessage(error, '获取详情失败'))
  } finally {
    detailLoading.value = false
  }
}

function openEditDialog(row) {
  editDialogVisible.value = true
  editLoading.value = true
  editQuestionPage.value = 1

  request
  .get(`/admin/assessment-scale/detail/${row.scaleId}`)
  .then((res) => {
    if (res?.code === 200 && res.data) {
      const data = res.data
      editForm.scaleId = data.scale?.id || row.scaleId
      editForm.scaleName = data.scale?.scaleName || ''
      editForm.scaleType = data.scale?.scaleType || ''
      editForm.description = data.scale?.description || ''
      editForm.versionRemark = ''
      editForm.operator = 'admin'

      editForm.questions = (data.questions || []).map((q, idx) => ({
        questionNo: idx + 1,
        questionText: q.questionText,
        options: (q.options || []).map((o) => ({
          optionNo: o.optionNo,
          optionText: o.optionText,
          optionScore: o.optionScore
        }))
      }))

      editForm.rules = (data.rules || []).map((r) => ({
        minScore: r.minScore,
        maxScore: r.maxScore,
        resultLevel: r.resultLevel,
        resultSummary: r.resultSummary,
        suggestion: r.suggestion
      }))
    } else {
      ElMessage.error(res?.message || '加载修改数据失败')
    }
  })
  .catch((error) => {
    ElMessage.error(extractErrorMessage(error, '加载修改数据失败'))
  })
  .finally(() => {
    editLoading.value = false
  })
}

function addQuestion() {
  editForm.questions.push({
    questionNo: editForm.questions.length + 1,
    questionText: '',
    options: [{ optionNo: 1, optionText: '', optionScore: 0 }]
  })
  editQuestionPage.value = getQuestionPageCount()
}

function removeQuestion(index) {
  editForm.questions.splice(index, 1)
  refreshQuestionNumbers()
  adjustQuestionPageAfterDelete()
}

function addOption(question) {
  question.options.push({
    optionNo: question.options.length + 1,
    optionText: '',
    optionScore: 0
  })
}

function removeOption(question, index) {
  question.options.splice(index, 1)
  question.options.forEach((opt, i) => {
    opt.optionNo = i + 1
  })
}

function addRule() {
  editForm.rules.push({
    minScore: 0,
    maxScore: 0,
    resultLevel: '',
    resultSummary: '',
    suggestion: ''
  })
}

function removeRule(index) {
  editForm.rules.splice(index, 1)
}

function validateRuleSequence(rules) {
  for (let i = 0; i < rules.length; i++) {
    const rule = rules[i]

    if (
        rule.minScore === null ||
        rule.minScore === undefined ||
        rule.maxScore === null ||
        rule.maxScore === undefined
    ) {
      ElMessage.error(`第 ${i + 1} 条规则缺少最低分数或最高分数`)
      return false
    }

    if (!rule.resultLevel || !rule.resultSummary) {
      ElMessage.error(`第 ${i + 1} 条规则的结果等级和结果说明不能为空`)
      return false
    }

    if (rule.maxScore < rule.minScore) {
      ElMessage.error(`第 ${i + 1} 条规则不符合要求：最高分数不能低于最低分数`)
      return false
    }

    if (i > 0) {
      const prev = rules[i - 1]
      if (rule.minScore <= prev.maxScore) {
        ElMessage.error(`第 ${i + 1} 条规则不符合要求：当前最低分数必须大于上一条规则的最高分数`)
        return false
      }
    }
  }
  return true
}

function validateEditForm() {
  if (!editForm.scaleName) {
    ElMessage.error('量表名称不能为空')
    return false
  }
  if (!editForm.scaleType) {
    ElMessage.error('量表类型不能为空')
    return false
  }
  if (!editForm.questions.length) {
    ElMessage.error('至少保留一道题目')
    return false
  }
  if (!editForm.rules.length) {
    ElMessage.error('至少保留一条规则')
    return false
  }

  for (const question of editForm.questions) {
    if (!question.questionNo || !question.questionText) {
      ElMessage.error('题号和题目内容不能为空')
      return false
    }
    if (!question.options?.length) {
      ElMessage.error(`第 ${question.questionNo} 题至少需要一个选项`)
      return false
    }
    for (const option of question.options) {
      if (
          option.optionNo === null ||
          option.optionNo === undefined ||
          option.optionText === ''
      ) {
        ElMessage.error(`第 ${question.questionNo} 题存在未填写完整的选项`)
        return false
      }
    }
  }

  if (!validateRuleSequence(editForm.rules)) {
    return false
  }

  return true
}

function submitUpdate() {
  if (!validateEditForm()) return

  updateLoading.value = true
  request
  .post('/admin/assessment-scale/update', {
    scaleId: editForm.scaleId,
    scaleName: editForm.scaleName,
    scaleType: editForm.scaleType,
    description: editForm.description,
    versionRemark: editForm.versionRemark || '管理员修改版本',
    operator: editForm.operator || 'admin',
    questions: editForm.questions,
    rules: editForm.rules
  })
  .then((res) => {
    if (res?.code === 200) {
      ElMessage.success(res?.message || '修改成功')
      editDialogVisible.value = false
      loadScaleList()
    } else {
      ElMessage.error(res?.message || '修改失败')
    }
  })
  .catch((error) => {
    ElMessage.error(extractErrorMessage(error, '修改失败'))
  })
  .finally(() => {
    updateLoading.value = false
  })
}

function handleEnable(row) {
  ElMessageBox.confirm(`确认启用量表【${row.scaleName}】吗？`, '提示', {
    type: 'warning'
  })
  .then(() => request.post(`/admin/assessment-scale/enable/${row.scaleId}`))
  .then((res) => {
    if (res?.code === 200) {
      ElMessage.success(res?.message || '启用成功')
      loadScaleList()
    } else {
      ElMessage.error(res?.message || '启用失败')
    }
  })
  .catch(() => {})
}

function handleDisable(row) {
  ElMessageBox.confirm(`确认停用量表【${row.scaleName}】吗？`, '提示', {
    type: 'warning'
  })
  .then(() => request.post(`/admin/assessment-scale/disable/${row.scaleId}`))
  .then((res) => {
    if (res?.code === 200) {
      ElMessage.success(res?.message || '停用成功')
      loadScaleList()
    } else {
      ElMessage.error(res?.message || '停用失败')
    }
  })
  .catch(() => {})
}

function handleDelete(row) {
  request
  .post(`/admin/assessment-scale/delete/${row.scaleId}`)
  .then((res) => {
    if (res?.code === 200) {
      ElMessage.success(res?.message || '删除成功')
      loadScaleList()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  })
  .catch((error) => {
    ElMessage.error(extractErrorMessage(error, '删除失败'))
  })
}
</script>

<style scoped>
.test-input-page {
  padding: 20px;
}

.page-card {
  border-radius: 12px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.table-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 2px 6px;
  justify-content: center;
}

.upload-block {
  width: 100%;
}

.template-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.import-tip {
  margin-bottom: 14px;
  padding: 8px 12px;
  font-size: 13px;
  line-height: 1.5;
  color: #606266;
  background: #f4f4f5;
  border-radius: 6px;
}

.detail-wrapper {
  padding-right: 8px;
}

.detail-base {
  margin-bottom: 20px;
}

.edit-base-form {
  margin-bottom: 12px;
}

.edit-dialog-content {
  max-height: 72vh;
  overflow-y: auto;
  padding-right: 8px;
  box-sizing: border-box;
}

:deep(.import-scale-dialog .el-dialog) {
  margin-bottom: 0;
}

:deep(.import-scale-dialog .el-dialog__body) {
  padding-top: 14px;
  padding-bottom: 10px;
}

:deep(.import-scale-dialog .el-form-item) {
  margin-bottom: 14px;
}

:deep(.import-scale-dialog .el-textarea__inner) {
  min-height: 66px !important;
}

:deep(.import-scale-dialog .el-upload__tip) {
  line-height: 1.35;
  margin-top: 4px;
}

:deep(.edit-scale-dialog) {
  margin-bottom: 0;
}

:deep(.edit-scale-dialog .el-dialog__body) {
  padding-top: 18px;
}

.question-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.edit-question-card {
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  background: #fafafa;
}

.question-header,
.option-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  font-weight: 600;
}

.option-area {
  margin-top: 12px;
}

.option-row {
  display: grid;
  grid-template-columns: 80px 1fr 100px 80px;
  gap: 12px;
  align-items: center;
  margin-bottom: 10px;
}

.option-no,
.option-text,
.option-score,
.option-delete {
  width: 100%;
}

.question-pagination {
  display: flex;
  justify-content: center;
  margin: 20px 0 10px;
}

.rule-row {
  display: grid;
  grid-template-columns: 120px 30px 120px 140px 1fr 1fr 60px;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.rule-sep {
  text-align: center;
  color: #606266;
}

.section-btn {
  margin: 10px 0 18px;
}

@media (max-width: 992px) {
  .option-row {
    grid-template-columns: 1fr;
  }

  .rule-row {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .question-toolbar {
    align-items: flex-start;
  }
}
</style>