import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthenticationStore = defineStore('authentication', () => {
  // 身份认证信息
  const authConfig = ref({
    address: '127.0.0.1',
    port: '161',
    readCommunity: 'public',
    writeCommunity: 'public',
    snmpVersion: 1,
  })

  // 更新身份认证信息
  const setAuthConfig = (config) => {
    authConfig.value = { ...config }
  }

  // 获取身份认证信息
  const getAuthConfig = () => {
    return authConfig.value
  }

  // 重置为默认值
  const resetAuthConfig = () => {
    authConfig.value = {
      address: '127.0.0.1',
      port: '161',
      readCommunity: 'public',
      writeCommunity: 'public',
      snmpVersion: 1,
    }
  }

  return {
    authConfig,
    setAuthConfig,
    getAuthConfig,
    resetAuthConfig,
  }
})
