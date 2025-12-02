import request from '@/utils/request.js'

// 发起 SNMP Get 请求接口
export const snmpGetService = (oid) => {
  return request.get('/api/snmp/get', {
    params: { oid },
  })
}

// 发起 SNMP GetNext 请求接口
export const snmpGetNextService = (oid) => {
  return request.get('/api/snmp/get-next', {
    params: { oid },
  })
}

// 发起 SNMP GetBulk 请求接口
export const snmpGetBulkService = (nonRepeaters, maxRepetitions, oids) => {
  return request.get('/api/snmp/get-bulk', {
    params: {
      nonRepeaters,
      maxRepetitions,
      oids,
    },
  })
}
