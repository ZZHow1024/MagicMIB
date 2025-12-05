import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useTerminalStore = defineStore('terminal', () => {
  // 终端输出历史
  const terminalOutput = ref([
    {
      type: 'system',
      content: 'MagicMIB Terminal\nDesigned by ZZHow',
      timestamp: new Date(),
    },
    {
      type: 'system',
      content: '支持的命令: get, getnext, getbulk, walk',
      timestamp: new Date(),
    },
    {
      type: 'system',
      content: '输入 help 查看帮助信息',
      timestamp: new Date(),
    },
  ])

  // 命令历史
  const commandHistory = ref([])

  // 当前输入内容
  const currentInput = ref('')

  // 添加输出
  const addOutput = (type, content) => {
    terminalOutput.value.push({
      type,
      content,
      timestamp: new Date(),
    })
  }

  // 添加命令到历史
  const addCommandHistory = (command) => {
    commandHistory.value.push(command)
  }

  // 更新当前输入
  const updateCurrentInput = (value) => {
    currentInput.value = value
  }

  // 清空当前输入
  const clearCurrentInput = () => {
    currentInput.value = ''
  }

  // 清空终端
  const clearTerminal = () => {
    terminalOutput.value = [
      {
        type: 'system',
        content: 'MagicMIB Terminal\nDesigned by ZZHow',
        timestamp: new Date(),
      },
      {
        type: 'system',
        content: '支持的命令: get, getnext, getbulk, walk',
        timestamp: new Date(),
      },
      {
        type: 'system',
        content: '输入 help 查看帮助信息',
        timestamp: new Date(),
      },
    ]
  }

  // 重置所有状态
  const resetAll = () => {
    clearTerminal()
    commandHistory.value = []
    currentInput.value = ''
  }

  return {
    terminalOutput,
    commandHistory,
    currentInput,
    addOutput,
    addCommandHistory,
    updateCurrentInput,
    clearCurrentInput,
    clearTerminal,
    resetAll,
  }
})
