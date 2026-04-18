<template>
  <div class="counselor-manage">
    <el-card class="search-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>辅导员管理</span>
        </div>
      </template>

      <el-form :model="queryForm" label-width="80px" class="search-form">
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="账号">
              <el-input
                  v-model="queryForm.account"
                  placeholder="请输入辅导员账号"
                  clearable
                  @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="姓名">
              <el-input
                  v-model="queryForm.name"
                  placeholder="请输入辅导员姓名"
                  clearable
                  @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="学院">
              <el-input
                  v-model="queryForm.college"
                  placeholder="请输入学院"
                  clearable
                  @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="年级">
              <el-input
                  v-model="queryForm.grade"
                  placeholder="请输入年级"
                  clearable
                  @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header toolbar">
          <span>辅导员列表</span>
          <el-button type="primary" @click="openAddDialog">新增辅导员</el-button>
        </div>
      </template>

      <el-table
          v-loading="loading"
          :data="tableData"
          border
          stripe
          style="width: 100%"
          empty-text="暂无数据"
      >
        <el-table-column type="index" label="序号" width="70" align="center">
          <template #default="scope">
            {{ (pagination.pageNum - 1) * pagination.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>

        <el-table-column prop="account" label="账号" min-width="130" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="college" label="学院" min-width="180" />
        <el-table-column prop="grade" label="年级" min-width="120" />

        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="openDetailDialog(scope.row.account)">
              详情
            </el-button>
            <el-button type="warning" link @click="openEditDialog(scope.row.account)">
              修改
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :current-page="pagination.pageNum"
            :page-size="pagination.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
        v-model="addDialogVisible"
        title="新增辅导员"
        width="620px"
        :close-on-click-modal="false"
        destroy-on-close
        lock-scroll
    >
      <el-form
          ref="addFormRef"
          :model="addForm"
          :rules="addRules"
          label-width="90px"
      >
        <el-form-item label="账号" prop="account">
          <el-input v-model="addForm.account" placeholder="请输入辅导员账号" />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入辅导员姓名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="addForm.password"
              type="password"
              show-password
              placeholder="请输入登录密码"
          />
        </el-form-item>

        <el-form-item label="学院" prop="college">
          <el-input v-model="addForm.college" placeholder="请输入学院" />
        </el-form-item>

        <el-form-item label="年级" prop="grade">
          <el-input v-model="addForm.grade" placeholder="请输入年级" />
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="负责班级">
          <div class="class-editor">
            <div class="class-tag-list">
              <el-tag
                  v-for="(item, index) in addForm.classList"
                  :key="item + index"
                  closable
                  class="class-tag"
                  @close="removeAddClass(index)"
              >
                {{ item }}
              </el-tag>

              <el-input
                  v-if="addClassInputVisible"
                  ref="addClassInputRef"
                  v-model="addClassInputValue"
                  class="class-input"
                  size="small"
                  placeholder="请输入班级名"
                  @keyup.enter="confirmAddClass"
                  @blur="confirmAddClass"
              />

              <el-button
                  v-else
                  class="add-class-btn"
                  size="small"
                  @click="showAddClassInput"
              >
                + 新增班级
              </el-button>
            </div>
            <div class="class-tip">
              可选，不填也可以；同一个班级不能分配给多个辅导员。
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span>
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitAdd">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
        v-model="editDialogVisible"
        title="修改辅导员信息"
        width="620px"
        :close-on-click-modal="false"
        destroy-on-close
        lock-scroll
    >
      <el-form
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-width="90px"
      >
        <el-form-item label="账号">
          <el-input v-model="editForm.account" disabled />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入辅导员姓名" />
        </el-form-item>

        <el-form-item label="新密码">
          <el-input
              v-model="editForm.password"
              type="password"
              show-password
              placeholder="不修改可留空"
          />
        </el-form-item>

        <el-form-item label="学院" prop="college">
          <el-input v-model="editForm.college" placeholder="请输入学院" />
        </el-form-item>

        <el-form-item label="年级" prop="grade">
          <el-input v-model="editForm.grade" placeholder="请输入年级" />
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitEdit">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
        v-model="detailDialogVisible"
        title="辅导员详情与班级管理"
        width="680px"
        top="6vh"
        :close-on-click-modal="false"
        destroy-on-close
        lock-scroll
    >
      <div class="detail-container">
        <el-card shadow="never" class="detail-info-card">
          <template #header>
            <div class="detail-section-header">
              <span>基本信息</span>
            </div>
          </template>

          <el-descriptions :column="2" border size="small" class="detail-descriptions">
            <el-descriptions-item label="账号">
              {{ detailData.account || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="姓名">
              {{ detailData.name || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="学院">
              {{ detailData.college || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ detailData.grade || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="电话" :span="2">
              {{ detailData.phone || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="detail-class-card">
          <template #header>
            <div class="detail-section-header">
              <div class="class-header-left">
                <span>负责班级</span>
                <el-tag type="primary" effect="plain">
                  共 {{ detailClassList.length }} 个班级
                </el-tag>
              </div>
            </div>
          </template>

          <div class="class-tip single-tip">
            点击卡片右上角可删除班级；点击“新增班级”可添加新班级，完成后点击下方“保存班级”。
          </div>

          <div class="class-manage-grid">
            <div
                v-for="(item, index) in detailClassList"
                :key="'detail-' + item + index"
                class="class-grid-item editable-class-card"
            >
              <div class="class-card-top">
                <div class="class-grid-index">{{ index + 1 }}</div>
                <button
                    class="class-delete-btn"
                    type="button"
                    @click="removeDetailClass(index)"
                >
                  删除
                </button>
              </div>
              <div class="class-grid-name">{{ item }}</div>
            </div>

            <div
                v-if="detailClassInputVisible"
                class="class-grid-item editable-class-card input-class-card"
            >
              <div class="class-card-top">
                <div class="class-grid-index">+</div>
              </div>
              <el-input
                  ref="detailClassInputRef"
                  v-model="detailClassInputValue"
                  placeholder="请输入班级名"
                  @keyup.enter="confirmDetailClass"
                  @blur="confirmDetailClass"
              />
            </div>

            <div
                v-else
                class="class-grid-item add-class-card"
                @click="showDetailClassInput"
            >
              <div class="add-class-plus">+</div>
              <div class="add-class-text">新增班级</div>
            </div>
          </div>

          <el-empty
              v-if="detailClassList.length === 0 && !detailClassInputVisible"
              description="当前未分配班级"
              :image-size="80"
              class="class-empty"
          />
        </el-card>
      </div>

      <template #footer>
        <span>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" :loading="classSubmitLoading" @click="submitDetailClasses">
            保存班级
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export default {
  name: 'CounselorManage',
  data() {
    return {
      loading: false,
      submitLoading: false,
      classSubmitLoading: false,

      queryForm: {
        account: '',
        name: '',
        college: '',
        grade: ''
      },

      pagination: {
        pageNum: 1,
        pageSize: 5,
        total: 0
      },

      tableData: [],

      addDialogVisible: false,
      editDialogVisible: false,
      detailDialogVisible: false,

      addForm: {
        account: '',
        name: '',
        password: '',
        college: '',
        grade: '',
        phone: '',
        classList: []
      },

      editForm: {
        account: '',
        name: '',
        password: '',
        college: '',
        grade: '',
        phone: ''
      },

      detailData: {
        account: '',
        name: '',
        college: '',
        grade: '',
        phone: '',
        classList: []
      },

      detailClassList: [],

      addClassInputVisible: false,
      addClassInputValue: '',

      detailClassInputVisible: false,
      detailClassInputValue: '',

      addRules: {
        account: [{ required: true, message: '请输入辅导员账号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入辅导员姓名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
        grade: [{ required: true, message: '请输入年级', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      },

      editRules: {
        name: [{ required: true, message: '请输入辅导员姓名', trigger: 'blur' }],
        college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
        grade: [{ required: true, message: '请输入年级', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      }
    }
  },

  mounted() {
    this.fetchTableData()
  },

  methods: {
    parseResult(res) {
      if (res === null || res === undefined) {
        throw new Error('服务器返回为空')
      }

      const hasCode = Object.prototype.hasOwnProperty.call(res, 'code')
      const hasSuccess = Object.prototype.hasOwnProperty.call(res, 'success')

      if (hasCode) {
        const ok = res.code === 200 || res.code === 0
        if (!ok) {
          throw new Error(res.message || res.msg || '请求失败')
        }
        return res.data !== undefined ? res.data : res
      }

      if (hasSuccess) {
        if (!res.success) {
          throw new Error(res.message || res.msg || '请求失败')
        }
        return res.data !== undefined ? res.data : res
      }

      return res
    },

    normalizeClassName(value) {
      if (value === null || value === undefined) return ''
      return String(value).trim()
    },

    addUniqueClass(targetList, value) {
      const className = this.normalizeClassName(value)
      if (!className) return false
      if (targetList.includes(className)) {
        ElMessage.warning('班级已存在')
        return false
      }
      targetList.push(className)
      return true
    },

    async fetchTableData() {
      this.loading = true
      try {
        const res = await request({
          url: '/admin/counselor/page',
          method: 'post',
          data: {
            pageNum: this.pagination.pageNum,
            pageSize: this.pagination.pageSize,
            account: this.queryForm.account,
            name: this.queryForm.name,
            college: this.queryForm.college,
            grade: this.queryForm.grade
          }
        })

        const data = this.parseResult(res)

        this.tableData = Array.isArray(data.list) ? data.list : []
        this.pagination.total = data.total || 0
        this.pagination.pageNum = data.pageNum || this.pagination.pageNum
        this.pagination.pageSize = data.pageSize || this.pagination.pageSize
      } catch (error) {
        ElMessage.error(error.message || '查询失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchTableData()
    },

    handleReset() {
      this.queryForm = {
        account: '',
        name: '',
        college: '',
        grade: ''
      }
      this.pagination.pageNum = 1
      this.pagination.pageSize = 5
      this.fetchTableData()
    },

    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.pageNum = 1
      this.fetchTableData()
    },

    handleCurrentChange(page) {
      this.pagination.pageNum = page
      this.fetchTableData()
    },

    resetAddForm() {
      this.addForm = {
        account: '',
        name: '',
        password: '',
        college: '',
        grade: '',
        phone: '',
        classList: []
      }
      this.addClassInputVisible = false
      this.addClassInputValue = ''
    },

    openAddDialog() {
      this.addDialogVisible = true
      this.$nextTick(() => {
        this.resetAddForm()
        if (this.$refs.addFormRef) {
          this.$refs.addFormRef.clearValidate()
        }
      })
    },

    showAddClassInput() {
      this.addClassInputVisible = true
      this.$nextTick(() => {
        if (this.$refs.addClassInputRef) {
          this.$refs.addClassInputRef.focus()
        }
      })
    },

    confirmAddClass() {
      this.addUniqueClass(this.addForm.classList, this.addClassInputValue)
      this.addClassInputVisible = false
      this.addClassInputValue = ''
    },

    removeAddClass(index) {
      this.addForm.classList.splice(index, 1)
    },

    submitAdd() {
      if (!this.$refs.addFormRef) return

      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const res = await request({
            url: '/admin/counselor/create',
            method: 'post',
            data: {
              account: this.addForm.account,
              name: this.addForm.name,
              password: this.addForm.password,
              college: this.addForm.college,
              grade: this.addForm.grade,
              phone: this.addForm.phone,
              classList: this.addForm.classList
            }
          })

          this.parseResult(res)
          ElMessage.success(res.message || res.msg || '新增成功')
          this.addDialogVisible = false
          this.fetchTableData()
        } catch (error) {
          ElMessage.error(error.message || '新增失败')
        } finally {
          this.submitLoading = false
        }
      })
    },

    async openEditDialog(account) {
      try {
        const res = await request({
          url: '/admin/counselor/detail',
          method: 'get',
          params: { account }
        })

        const data = this.parseResult(res)
        this.editForm = {
          account: data.account || '',
          name: data.name || '',
          password: '',
          college: data.college || '',
          grade: data.grade || '',
          phone: data.phone || ''
        }

        this.editDialogVisible = true

        this.$nextTick(() => {
          if (this.$refs.editFormRef) {
            this.$refs.editFormRef.clearValidate()
          }
        })
      } catch (error) {
        ElMessage.error(error.message || '获取辅导员信息失败')
      }
    },

    submitEdit() {
      if (!this.$refs.editFormRef) return

      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const payload = {
            account: this.editForm.account,
            name: this.editForm.name,
            college: this.editForm.college,
            grade: this.editForm.grade,
            phone: this.editForm.phone
          }

          if (this.editForm.password && this.editForm.password.trim() !== '') {
            payload.password = this.editForm.password
          }

          const res = await request({
            url: '/admin/counselor/update',
            method: 'post',
            data: payload
          })

          this.parseResult(res)
          ElMessage.success(res.message || res.msg || '修改成功')
          this.editDialogVisible = false
          this.fetchTableData()
        } catch (error) {
          ElMessage.error(error.message || '修改失败')
        } finally {
          this.submitLoading = false
        }
      })
    },

    async openDetailDialog(account) {
      try {
        const res = await request({
          url: '/admin/counselor/detail',
          method: 'get',
          params: { account }
        })

        const data = this.parseResult(res)

        this.detailData = {
          account: data.account || '',
          name: data.name || '',
          college: data.college || '',
          grade: data.grade || '',
          phone: data.phone || '',
          classList: Array.isArray(data.classList) ? data.classList : []
        }

        this.detailClassList = Array.isArray(data.classList) ? [...data.classList] : []
        this.detailClassInputVisible = false
        this.detailClassInputValue = ''
        this.detailDialogVisible = true
      } catch (error) {
        ElMessage.error(error.message || '获取详情失败')
      }
    },

    showDetailClassInput() {
      this.detailClassInputVisible = true
      this.$nextTick(() => {
        if (this.$refs.detailClassInputRef) {
          this.$refs.detailClassInputRef.focus()
        }
      })
    },

    confirmDetailClass() {
      this.addUniqueClass(this.detailClassList, this.detailClassInputValue)
      this.detailClassInputVisible = false
      this.detailClassInputValue = ''
    },

    removeDetailClass(index) {
      this.detailClassList.splice(index, 1)
    },

    async submitDetailClasses() {
      this.classSubmitLoading = true
      try {
        const res = await request({
          url: '/admin/counselor/update-classes',
          method: 'post',
          data: {
            account: this.detailData.account,
            classList: this.detailClassList
          }
        })

        const data = this.parseResult(res)

        this.detailData = {
          account: data.account || '',
          name: data.name || '',
          college: data.college || '',
          grade: data.grade || '',
          phone: data.phone || '',
          classList: Array.isArray(data.classList) ? data.classList : []
        }
        this.detailClassList = Array.isArray(data.classList) ? [...data.classList] : []

        ElMessage.success(res.message || res.msg || '班级更新成功')
        this.fetchTableData()
      } catch (error) {
        ElMessage.error(error.message || '班级更新失败')
      } finally {
        this.classSubmitLoading = false
      }
    }
  }
}
</script>

<style scoped>
.counselor-manage {
  padding: 16px;
  background: #f5f7fa;
  min-height: 100%;
  box-sizing: border-box;
}

.search-card,
.table-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.toolbar {
  gap: 12px;
}

.search-form {
  margin-top: 4px;
}

.search-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.class-editor {
  width: 100%;
}

.class-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.class-tag {
  margin: 0;
}

.class-input {
  width: 150px;
}

.add-class-btn {
  border-style: dashed;
}

.class-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}

.detail-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-info-card,
.detail-class-card {
  border-radius: 8px;
}

.detail-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 14px;
}

.class-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-descriptions {
  margin-bottom: 0;
}

.single-tip {
  margin-top: 0;
  margin-bottom: 10px;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.class-manage-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.class-grid-item {
  min-height: 82px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #ffffff;
  padding: 10px;
  box-sizing: border-box;
  transition: all 0.2s;
}

.editable-class-card:hover,
.add-class-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.08);
}

.class-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.class-grid-index {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
  font-weight: 600;
}

.class-grid-name {
  font-size: 13px;
  color: #303133;
  word-break: break-all;
  line-height: 1.35;
}

.class-delete-btn {
  border: none;
  background: transparent;
  color: #f56c6c;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
}

.class-delete-btn:hover {
  color: #dd6161;
}

.add-class-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-style: dashed;
  background: #fafcff;
}

.add-class-plus {
  font-size: 22px;
  line-height: 1;
  color: #409eff;
  margin-bottom: 6px;
}

.add-class-text {
  font-size: 13px;
  color: #409eff;
}

.input-class-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.class-empty {
  margin-top: 4px;
}

:deep(.el-dialog) {
  border-radius: 10px;
}

:deep(.el-dialog__header) {
  padding: 16px 20px 8px;
}

:deep(.el-dialog__body) {
  padding: 8px 20px 12px;
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}

:deep(.el-dialog__footer) {
  padding: 8px 20px 16px;
}

:deep(.el-card__header) {
  padding: 10px 14px;
}

:deep(.el-card__body) {
  padding: 12px 14px;
}

:deep(.el-descriptions__label) {
  width: 72px;
}

@media (max-width: 768px) {
  .counselor-manage {
    padding: 10px;
  }

  .search-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>