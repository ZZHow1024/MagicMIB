import request from '@/utils/request.js'

// 发起 SNMP Get 请求接口
export const snmpGetService = (oid) => {
  return request.get('/api/snmp/get', {
    params: { oid },
  })
}
