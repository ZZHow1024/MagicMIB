import request from '@/utils/request.js'

// 设置身份认证信息接口
export const setAuthenticationService = ({
  address,
  port,
  readCommunity,
  writeCommunity,
  snmpVersion,
}) => {
  return request.post('/api/authentication/set', {
    address,
    port,
    readCommunity,
    writeCommunity,
    snmpVersion,
  })
}

// 获取身份认证信息接口
export const getAuthenticationService = () => {
  return request.get('/api/authentication/get')
}
