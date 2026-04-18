import request from '@/utils/request'

export function queryCounselorTrendReport(data) {
  return request({
    url: '/api/counselor/trend-report/query',
    method: 'post',
    data
  })
}