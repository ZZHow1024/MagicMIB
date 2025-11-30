import request from '@/utils/request.js'

// 获取当前加载的 MIB 文件接口
export const getMibService = () => {
  return request.get('/api/mib/get')
}
