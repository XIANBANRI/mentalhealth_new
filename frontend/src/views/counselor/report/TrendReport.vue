<template>
  <div class="trend-report-page">
    <el-card class="filter-card" shadow="never">
      <div class="filter-bar">
        <div class="filter-left">
          <span class="filter-label">学期：</span>
          <el-select
              v-model="selectedSemester"
              placeholder="请选择学期"
              style="width: 180px"
              @change="handleSemesterChange"
          >
            <el-option
                v-for="item in semesterOptions"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </div>

        <div class="filter-right">
          <el-button type="primary" @click="loadReport">刷新</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header-wrap">
              <span class="card-header">各班级危险人数统计</span>
              <el-button
                  type="success"
                  size="small"
                  @click="exportBarChart"
              >
                导出图片
              </el-button>
            </div>
          </template>
          <div v-loading="loading" ref="barChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header-wrap">
              <span class="card-header">8个学期危险总人数趋势</span>
              <el-button
                  type="success"
                  size="small"
                  @click="exportLineChart"
              >
                导出图片
              </el-button>
            </div>
          </template>
          <div v-loading="loading" ref="lineChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from '@/utils/request'

export default {
  name: 'TrendReport',
  data() {
    return {
      loading: false,
      semesterOptions: [],
      selectedSemester: '第1学期',
      barChart: [],
      lineChart: [],
      barInstance: null,
      lineInstance: null
    }
  },
  mounted() {
    this.loadReport()
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    if (this.barInstance) {
      this.barInstance.dispose()
      this.barInstance = null
    }
    if (this.lineInstance) {
      this.lineInstance.dispose()
      this.lineInstance = null
    }
  },
  methods: {
    async loadReport() {
      this.loading = true
      try {
        const res = await request.post('/api/counselor/trend-report/query', {
          semester: this.selectedSemester
        })

        if (res.code !== 200) {
          this.$message.error(res.message || '查询失败')
          return
        }

        const data = res.data || {}
        this.semesterOptions = data.semesterOptions || [
          '第1学期',
          '第2学期',
          '第3学期',
          '第4学期',
          '第5学期',
          '第6学期',
          '第7学期',
          '第8学期'
        ]
        this.selectedSemester = data.selectedSemester || this.selectedSemester
        this.barChart = data.barChart || []
        this.lineChart = data.lineChart || []

        this.$nextTick(() => {
          this.renderBarChart()
          this.renderLineChart()
        })
      } catch (e) {
        console.error(e)
        this.$message.error(
            e?.response?.data?.message ||
            e?.message ||
            '趋势报告加载失败'
        )
      } finally {
        this.loading = false
      }
    },

    handleSemesterChange() {
      this.loadReport()
    },

    renderBarChart() {
      const dom = this.$refs.barChartRef
      if (!dom) return

      if (!this.barInstance) {
        this.barInstance = echarts.init(dom)
      }

      const classNames = this.barChart.map(item => item.className)
      const dangerCounts = this.barChart.map(item => item.dangerCount)

      const option = {
        title: {
          text: `${this.selectedSemester}各班级危险人数统计`,
          left: 'center',
          top: 0,
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '5%',
          right: '5%',
          top: 50,
          bottom: 60,
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: classNames,
          axisLabel: {
            interval: 0,
            rotate: classNames.length > 6 ? 25 : 0
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          name: '危险人数'
        },
        series: [
          {
            name: '危险人数',
            type: 'bar',
            barMaxWidth: 50,
            data: dangerCounts,
            label: {
              show: true,
              position: 'top'
            }
          }
        ]
      }

      this.barInstance.setOption(option, true)
    },

    renderLineChart() {
      const dom = this.$refs.lineChartRef
      if (!dom) return

      if (!this.lineInstance) {
        this.lineInstance = echarts.init(dom)
      }

      const semesters = this.lineChart.map(item => item.semester)
      const dangerCounts = this.lineChart.map(item => item.dangerCount)

      const option = {
        title: {
          text: '8个学期危险总人数趋势',
          left: 'center',
          top: 0,
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '5%',
          right: '5%',
          top: 50,
          bottom: 40,
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: semesters
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          name: '危险总人数'
        },
        series: [
          {
            name: '危险总人数',
            type: 'line',
            smooth: true,
            data: dangerCounts,
            label: {
              show: true
            }
          }
        ]
      }

      this.lineInstance.setOption(option, true)
    },

    exportBarChart() {
      if (!this.barInstance) {
        this.$message.warning('柱状图尚未生成')
        return
      }

      const url = this.barInstance.getDataURL({
        type: 'png',
        pixelRatio: 2,
        backgroundColor: '#ffffff'
      })

      this.downloadImage(url, `趋势报告-各班级危险人数统计-${this.selectedSemester}.png`)
    },

    exportLineChart() {
      if (!this.lineInstance) {
        this.$message.warning('折线图尚未生成')
        return
      }

      const url = this.lineInstance.getDataURL({
        type: 'png',
        pixelRatio: 2,
        backgroundColor: '#ffffff'
      })

      this.downloadImage(url, '趋势报告-8个学期危险总人数趋势.png')
    },

    downloadImage(dataUrl, fileName) {
      const link = document.createElement('a')
      link.href = dataUrl
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      this.$message.success('导出成功')
    },

    handleResize() {
      if (this.barInstance) {
        this.barInstance.resize()
      }
      if (this.lineInstance) {
        this.lineInstance.resize()
      }
    }
  }
}
</script>

<style scoped>
.trend-report-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
  box-sizing: border-box;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
}

.card-header-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-box {
  width: 100%;
  height: 420px;
}
</style>