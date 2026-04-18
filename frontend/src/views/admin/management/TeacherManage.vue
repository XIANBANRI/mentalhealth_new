<template>
  <div class="teacher-manage">
    <el-card class="search-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>心理老师管理</span>
        </div>
      </template>

      <el-form :model="queryForm" label-width="80px" class="search-form">
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :lg="8">
            <el-form-item label="账号">
              <el-input
                  v-model="queryForm.account"
                  placeholder="请输入老师账号"
                  clearable
                  @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="8">
            <el-form-item label="姓名">
              <el-input
                  v-model="queryForm.teacherName"
                  placeholder="请输入老师姓名"
                  clearable
                  @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="8">
            <el-form-item label="办公室">
              <el-input
                  v-model="queryForm.officeLocation"
                  placeholder="请输入办公地点"
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
          <span>老师列表</span>
          <el-button type="primary" @click="openAddDialog">新增心理老师</el-button>
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

        <el-table-column prop="account" label="账号" min-width="140" />
        <el-table-column prop="teacherName" label="姓名" min-width="120" />
        <el-table-column prop="officeLocation" label="办公室" min-width="160" />
        <el-table-column prop="phone" label="电话" min-width="140" />

        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="scope">
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
        title="新增心理老师"
        width="520px"
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
          <el-input v-model="addForm.account" placeholder="请输入老师账号" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="addForm.password"
              type="password"
              show-password
              placeholder="请输入登录密码"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="teacherName">
          <el-input v-model="addForm.teacherName" placeholder="请输入老师姓名" />
        </el-form-item>

        <el-form-item label="办公室" prop="officeLocation">
          <el-input v-model="addForm.officeLocation" placeholder="请输入办公地点" />
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入联系电话" />
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
        title="修改心理老师信息"
        width="520px"
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

        <el-form-item label="新密码" prop="password">
          <el-input
              v-model="editForm.password"
              type="password"
              show-password
              placeholder="不修改可留空"
          />
        </el-form-item>

        <el-form-item label="姓名" prop="teacherName">
          <el-input v-model="editForm.teacherName" placeholder="请输入老师姓名" />
        </el-form-item>

        <el-form-item label="办公室" prop="officeLocation">
          <el-input v-model="editForm.officeLocation" placeholder="请输入办公地点" />
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
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export default {
  name: 'TeacherManage',
  data() {
    return {
      loading: false,
      submitLoading: false,

      queryForm: {
        account: '',
        teacherName: '',
        officeLocation: ''
      },

      pagination: {
        pageNum: 1,
        pageSize: 5,
        total: 0
      },

      tableData: [],

      addDialogVisible: false,
      editDialogVisible: false,

      addForm: {
        account: '',
        password: '',
        teacherName: '',
        officeLocation: '',
        phone: ''
      },

      editForm: {
        account: '',
        password: '',
        teacherName: '',
        officeLocation: '',
        phone: ''
      },

      addRules: {
        account: [
          { required: true, message: '请输入老师账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        teacherName: [
          { required: true, message: '请输入老师姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ]
      },

      editRules: {
        teacherName: [
          { required: true, message: '请输入老师姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ]
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

    async fetchTableData() {
      this.loading = true
      try {
        const res = await request({
          url: '/api/admin/teacher/page',
          method: 'post',
          data: {
            pageNum: this.pagination.pageNum,
            pageSize: this.pagination.pageSize,
            account: this.queryForm.account,
            teacherName: this.queryForm.teacherName,
            officeLocation: this.queryForm.officeLocation
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
        teacherName: '',
        officeLocation: ''
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

    openAddDialog() {
      this.addDialogVisible = true
      this.$nextTick(() => {
        this.resetAddForm()
        if (this.$refs.addFormRef) {
          this.$refs.addFormRef.clearValidate()
        }
      })
    },

    resetAddForm() {
      this.addForm = {
        account: '',
        password: '',
        teacherName: '',
        officeLocation: '',
        phone: ''
      }
    },

    async checkTeacherAccountExists(account) {
      const target = (account || '').trim()
      if (!target) return false

      const res = await request({
        url: '/api/admin/teacher/page',
        method: 'post',
        data: {
          pageNum: 1,
          pageSize: 10,
          account: target
        }
      })

      const data = this.parseResult(res)
      const list = Array.isArray(data.list) ? data.list : []

      return list.some(item => (item.account || '').trim() === target)
    },

    submitAdd() {
      if (!this.$refs.addFormRef) return

      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const payload = {
            account: (this.addForm.account || '').trim(),
            password: this.addForm.password,
            teacherName: (this.addForm.teacherName || '').trim(),
            officeLocation: (this.addForm.officeLocation || '').trim(),
            phone: (this.addForm.phone || '').trim()
          }

          const exists = await this.checkTeacherAccountExists(payload.account)
          if (exists) {
            ElMessage.error('已经存在该账号')
            return
          }

          const res = await request({
            url: '/api/admin/teacher/create',
            method: 'post',
            data: payload
          })

          this.parseResult(res)
          ElMessage.success('新增成功')
          this.addDialogVisible = false
          this.fetchTableData()
        } catch (error) {
          const msg = error.message || ''
          if (msg.includes('老师账号已存在') || msg.includes('已经存在')) {
            ElMessage.error('已经存在该账号')
          } else {
            ElMessage.error(msg || '新增失败')
          }
        } finally {
          this.submitLoading = false
        }
      })
    },

    async openEditDialog(account) {
      try {
        const res = await request({
          url: '/api/admin/teacher/detail',
          method: 'get',
          params: { account }
        })

        const data = this.parseResult(res)
        this.editForm = {
          account: data.account || '',
          password: '',
          teacherName: data.teacherName || '',
          officeLocation: data.officeLocation || '',
          phone: data.phone || ''
        }

        this.editDialogVisible = true

        this.$nextTick(() => {
          if (this.$refs.editFormRef) {
            this.$refs.editFormRef.clearValidate()
          }
        })
      } catch (error) {
        ElMessage.error(error.message || '获取老师信息失败')
      }
    },

    submitEdit() {
      if (!this.$refs.editFormRef) return

      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const payload = {
            account: (this.editForm.account || '').trim(),
            password: this.editForm.password,
            teacherName: (this.editForm.teacherName || '').trim(),
            officeLocation: (this.editForm.officeLocation || '').trim(),
            phone: (this.editForm.phone || '').trim()
          }

          if (!payload.password) delete payload.password

          const res = await request({
            url: '/api/admin/teacher/update',
            method: 'post',
            data: payload
          })

          this.parseResult(res)
          ElMessage.success('修改成功')
          this.editDialogVisible = false
          this.fetchTableData()
        } catch (error) {
          ElMessage.error(error.message || '修改失败')
        } finally {
          this.submitLoading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.teacher-manage {
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
</style>