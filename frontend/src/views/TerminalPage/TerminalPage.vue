<script setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import {
  snmpGetService,
  snmpGetNextService,
  snmpGetBulkService,
  snmpWalkService,
  snmpGetSubtreeService,
} from '@/api/snmp.js'
import { useTerminalStore } from '@/stores'

// 使用终端 store
const terminalStore = useTerminalStore()

// 使用 computed 双向绑定 store 中的 currentInput
const currentInput = computed({
  get: () => terminalStore.currentInput,
  set: (value) => terminalStore.updateCurrentInput(value),
})

// 历史命令导航索引
const historyIndex = ref(-1)

// 终端容器引用
const terminalContainer = ref(null)
const inputRef = ref(null)

// 自动滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (terminalContainer.value) {
      terminalContainer.value.scrollTop = terminalContainer.value.scrollHeight
    }
  })
}

// 添加输出
const addOutput = (type, content) => {
  terminalStore.addOutput(type, content)
  scrollToBottom()
}

// 解析命令
const parseCommand = (input) => {
  const trimmed = input.trim()
  if (!trimmed) return null

  const parts = trimmed.split(/\s+/)
  const command = parts[0].toLowerCase()

  return { command, parts, original: trimmed }
}

// Get 命令处理
const handleGetCommand = async (parts) => {
  if (parts.length < 2) {
    addOutput('error', '错误: get 命令需要指定 OID')
    addOutput('info', '用法: get [OID]')
    return
  }

  const oid = parts[1]
  addOutput('info', `正在执行: Get ${oid}`)

  const { code, data } = await snmpGet(oid)
  if (code === 0) addOutput('success', `成功：\n${data}`)
  else addOutput('error', '错误：' + data)
}

// GetNext 命令处理
const handleGetNextCommand = async (parts) => {
  if (parts.length < 2) {
    addOutput('error', '错误: getnext 命令需要指定 OID')
    addOutput('info', '用法: getnext [OID]')
    return
  }

  const oid = parts[1]
  addOutput('info', `正在执行: GetNext ${oid}`)

  const { code, data } = await snmpGetNext(oid)
  if (code === 0) addOutput('success', `成功：\n${data}`)
  else addOutput('error', '错误：' + data)
}

// GetBulk 命令处理
const handleGetBulkCommand = async (parts) => {
  if (parts.length < 4) {
    addOutput('error', '错误: getbulk 命令参数不足')
    addOutput('info', '用法: getbulk [n] [m] [OID1] [OID2] ...')
    addOutput('info', '  n: non-repeaters (非重复变量数)')
    addOutput('info', '  m: max-repetitions (最大重复次数)')
    return
  }

  const n = parseInt(parts[1])
  const m = parseInt(parts[2])
  const oids = parts.slice(3)

  if (isNaN(n) || isNaN(m)) {
    addOutput('error', '错误: n 和 m 必须是数字')
    return
  }

  addOutput('info', `正在执行: GetBulk n=${n}, m=${m}, OIDs=[${oids.join(', ')}]`)

  const { code, data } = await snmpGetBulk(n, m, oids)
  if (code === 0) addOutput('success', data)
  else addOutput('error', '错误：' + data)
}

// Walk 命令处理
const handleWalkCommand = async (parts) => {
  if (parts.length < 2) {
    addOutput('error', '错误: walk 命令需要指定 OID')
    addOutput('info', '用法: walk [OID]')
    return
  }

  const oid = parts[1]
  addOutput('info', `正在执行: Walk ${oid}`)

  const { code, data } = await snmpWalk(oid)
  if (code === 0) addOutput('success', data)
  else addOutput('error', '错误：' + data)
}

// GetSubtree 命令处理
const handleGetSubtreeCommand = async (parts) => {
  if (parts.length < 2) {
    addOutput('error', '错误: getsubtree 命令需要指定 OID')
    addOutput('info', '用法: getsubtree [OID]')
    return
  }

  const oid = parts[1]
  addOutput('info', `正在执行: GetSubtree ${oid}`)

  const { code, data } = await snmpGetSubtree(oid)
  if (code === 0) addOutput('success', data)
  else addOutput('error', '错误：' + data)
}

// 显示帮助信息
const showHelp = () => {
  addOutput('info', '=== SNMP 终端命令帮助 ===')
  addOutput('info', '')
  addOutput('info', '1. Get 命令:')
  addOutput('info', '   get [OID]')
  addOutput('info', '   示例: get 1.3.6.1.2.1.1.1.0')
  addOutput('info', '')
  addOutput('info', '2. GetNext 命令:')
  addOutput('info', '   getnext [OID]')
  addOutput('info', '   示例: getnext 1.3.6.1.2.1.1')
  addOutput('info', '')
  addOutput('info', '3. GetBulk 命令:')
  addOutput('info', '   getbulk [n] [m] [OID1] [OID2] ...')
  addOutput('info', '   n: non-repeaters (非重复变量数)')
  addOutput('info', '   m: max-repetitions (最大重复次数)')
  addOutput('info', '   示例: getbulk 0 10 1.3.6.1.2.1.2.2.1')
  addOutput('info', '')
  addOutput('info', '4. Walk 命令:')
  addOutput('info', '   walk [OID]')
  addOutput('info', '   示例: walk 1.3.6.1.2.1.1')
  addOutput('info', '')
  addOutput('info', '5. GetSubtree 命令:')
  addOutput('info', '   getsubtree [OID]')
  addOutput('info', '   示例: getsubtree 1.3.6.1.2.1.1')
  addOutput('info', '')
  addOutput('info', '其他命令:')
  addOutput('info', '   clear  - 清空终端')
  addOutput('info', '   help   - 显示帮助信息')
  addOutput('info', '   history - 显示命令历史')
}

// 显示历史命令
const showHistory = () => {
  if (terminalStore.commandHistory.length === 0) {
    addOutput('info', '暂无命令历史')
    return
  }

  addOutput('info', '=== 命令历史 ===')
  terminalStore.commandHistory.forEach((cmd, index) => {
    addOutput('info', `${index + 1}. ${cmd}`)
  })
}

// 清空终端
const clearTerminal = () => {
  terminalStore.clearTerminal()
}

// 执行命令
const executeCommand = () => {
  const input = currentInput.value.trim()
  if (!input) return

  // 添加到输出
  addOutput('command', `$ ${input}`)

  // 添加到历史
  terminalStore.addCommandHistory(input)
  historyIndex.value = -1

  // 解析命令
  const parsed = parseCommand(input)
  if (!parsed) {
    currentInput.value = ''
    return
  }

  const { command, parts } = parsed

  // 执行对应命令
  switch (command) {
    case 'get':
      handleGetCommand(parts)
      break
    case 'getnext':
      handleGetNextCommand(parts)
      break
    case 'getbulk':
      handleGetBulkCommand(parts)
      break
    case 'walk':
      handleWalkCommand(parts)
      break
    case 'getsubtree':
      handleGetSubtreeCommand(parts)
      break
    case 'help':
      showHelp()
      break
    case 'clear':
      clearTerminal()
      break
    case 'history':
      showHistory()
      break
    default:
      addOutput('error', `未知命令: ${command}`)
      addOutput('info', '输入 help 查看可用命令')
  }

  // 清空输入
  terminalStore.clearCurrentInput()
}

// 历史命令导航
const navigateHistory = (direction) => {
  if (terminalStore.commandHistory.length === 0) return

  if (direction === 'up') {
    if (historyIndex.value < terminalStore.commandHistory.length - 1) {
      historyIndex.value++
      terminalStore.updateCurrentInput(
        terminalStore.commandHistory[terminalStore.commandHistory.length - 1 - historyIndex.value],
      )
    }
  } else if (direction === 'down') {
    if (historyIndex.value > 0) {
      historyIndex.value--
      terminalStore.updateCurrentInput(
        terminalStore.commandHistory[terminalStore.commandHistory.length - 1 - historyIndex.value],
      )
    } else if (historyIndex.value === 0) {
      historyIndex.value = -1
      terminalStore.clearCurrentInput()
    }
  }
}

// 键盘事件处理
const handleKeyDown = (event) => {
  if (event.key === 'ArrowUp') {
    event.preventDefault()
    navigateHistory('up')
  } else if (event.key === 'ArrowDown') {
    event.preventDefault()
    navigateHistory('down')
  }
}

// ==================== API 调用函数 ====================

/**
 * SNMP Get 请求
 * @param {string} oid - 对象标识符
 * @returns {Promise} API 响应数据
 */
const snmpGet = async (oid) => {
  try {
    const res = await snmpGetService(oid)
    if (res.data.code === 0) {
      return { code: 0, data: formatSimpleResponse(res.data.data) }
    } else {
      return { code: 1, data: res.data.message }
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    return { code: 1, data: '系统错误' }
  }
}

/**
 * SNMP GetNext 请求
 * @param {string} oid - 对象标识符
 * @returns {Promise} API 响应数据
 */
const snmpGetNext = async (oid) => {
  try {
    const res = await snmpGetNextService(oid)
    if (res.data.code === 0) {
      return { code: 0, data: formatSimpleResponse(res.data.data) }
    } else {
      return { code: 1, data: res.data.message }
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    return { code: 1, data: '系统错误' }
  }
}

/**
 * SNMP GetBulk 请求
 * @param {number} nonRepeaters - 非重复变量数
 * @param {number} maxRepetitions - 最大重复次数
 * @param {Array<string>} oids - 对象标识符数组
 * @returns {Promise} API 响应数据
 */
const snmpGetBulk = async (nonRepeaters, maxRepetitions, oids) => {
  try {
    const res = await snmpGetBulkService(nonRepeaters, maxRepetitions, oids)
    if (res.data.code === 0) {
      return { code: 0, data: formatWalkResponse(res.data.data) }
    } else {
      return { code: 1, data: res.data.message }
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    return { code: 1, data: '系统错误' }
  }
}

/**
 * SNMP Walk 请求
 * @param {string} oid - 对象标识符
 * @returns {Promise} API 响应数据
 */
const snmpWalk = async (oid) => {
  try {
    const res = await snmpWalkService(oid)
    if (res.data.code === 0) {
      return { code: 0, data: formatWalkResponse(res.data.data) }
    } else {
      return { code: 1, data: res.data.message }
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    return { code: 1, data: '系统错误' }
  }
}

/**
 * SNMP GetSubtree 请求
 * @param {string} oid - 对象标识符
 * @returns {Promise} API 响应数据
 */
const snmpGetSubtree = async (oid) => {
  try {
    const res = await snmpGetSubtreeService(oid)
    if (res.data.code === 0) {
      return { code: 0, data: formatWalkResponse(res.data.data) }
    } else {
      return { code: 1, data: res.data.message }
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    return { code: 1, data: '系统错误' }
  }
}

/**
 * 格式化响应数据 (Get/GetNext)
 * @param {Object} responseData - 响应数据对象
 * @returns {string} 格式化后的字符串
 */
const formatSimpleResponse = (responseData) => {
  if (!responseData || !responseData.data || responseData.data.length === 0) {
    return '未返回任何数据'
  }

  const item = responseData.data[0]
  const lines = []

  lines.push(`地址: ${responseData.address}:${responseData.port}`)
  lines.push(`操作: ${responseData.operation}`)
  lines.push(`执行时间: ${responseData.executionTime}ms`)
  lines.push('---')
  lines.push(`名称:  ${item.name}`)
  lines.push(`OID:   ${item.oid}`)
  lines.push(`类型:  ${item.type}`)
  lines.push(`值:    ${item.value}`)
  if (item.access) {
    lines.push(`权限:  ${item.access}`)
  }

  return lines.join('\n')
}

/**
 * 格式化 Walk/GetBulk 响应数据
 * @param {Object} responseData - 响应数据对象
 * @returns {string} 格式化后的字符串
 */
const formatWalkResponse = (responseData) => {
  if (!responseData || !responseData.data || responseData.data.length === 0) {
    return '未返回任何数据'
  }

  const lines = []
  lines.push(`地址: ${responseData.address}:${responseData.port}`)
  lines.push(`操作: ${responseData.operation}`)
  lines.push(`执行时间: ${responseData.executionTime}ms`)
  lines.push(`结果数量: ${responseData.data.length}`)
  lines.push('---')

  responseData.data.forEach((item, index) => {
    lines.push(`[${index + 1}] ${item.name}`)
    lines.push(`    OID:   ${item.oid}`)
    lines.push(`    类型:  ${item.type}`)
    lines.push(`    值:    ${item.value}`)
    if (item.access) {
      lines.push(`    权限:  ${item.access}`)
    }
    if (index < responseData.data.length - 1) {
      lines.push('')
    }
  })

  return lines.join('\n')
}

// ==================== 生命周期 ====================

onMounted(() => {
  // 聚焦输入框
  if (inputRef.value) {
    inputRef.value.focus()
  }
})

// 点击终端区域时聚焦输入框
const focusInput = () => {
  if (inputRef.value) {
    inputRef.value.focus()
  }
}
</script>

<template>
  <div id="terminal-page" @click="focusInput">
    <div class="terminal-container">
      <!-- 终端头部 -->
      <div class="terminal-header">
        <div class="terminal-title">
          <span class="terminal-icon">⬤</span>
          <span>SNMP Terminal</span>
        </div>
        <div class="terminal-actions">
          <a-button size="small" @click="clearTerminal">清空</a-button>
          <a-button size="small" @click="showHelp">帮助</a-button>
        </div>
      </div>

      <!-- 终端内容区 -->
      <div ref="terminalContainer" class="terminal-content">
        <!-- 输出历史 -->
        <div
          v-for="(output, index) in terminalStore.terminalOutput"
          :key="index"
          class="terminal-line"
          :class="`output-${output.type}`"
        >
          <span v-if="output.type === 'command'" class="command-prompt">
            {{ output.content }}
          </span>
          <pre v-else class="output-content">{{ output.content }}</pre>
        </div>

        <!-- 当前输入行 -->
        <div class="terminal-line input-line">
          <span class="prompt">$</span>
          <input
            ref="inputRef"
            v-model="currentInput"
            type="text"
            class="terminal-input"
            placeholder="输入命令..."
            @keydown.enter="executeCommand"
            @keydown="handleKeyDown"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
#terminal-page {
  width: 100%;
  height: calc(100vh - 135px);
  background: #1e1e1e;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.terminal-container {
  width: 100%;
  max-width: 1200px;
  height: 100%;
  background: #0d1117;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.terminal-header {
  background: #161b22;
  padding: 12px 20px;
  border-bottom: 1px solid #30363d;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.terminal-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #c9d1d9;

  .terminal-icon {
    color: #58a6ff;
    font-size: 12px;
  }
}

.terminal-actions {
  display: flex;
  gap: 8px;
}

.terminal-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #c9d1d9;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #0d1117;
  }

  &::-webkit-scrollbar-thumb {
    background: #30363d;
    border-radius: 4px;

    &:hover {
      background: #484f58;
    }
  }
}

.terminal-line {
  margin-bottom: 8px;
  animation: fadeIn 0.2s ease-in;

  &.output-system {
    color: #58a6ff;
    font-weight: 500;
  }

  &.output-command {
    color: #8b949e;

    .command-prompt {
      user-select: none;
    }
  }

  &.output-info {
    color: #79c0ff;
  }

  &.output-success {
    color: #3fb950;
  }

  &.output-error {
    color: #f85149;
  }
}

.output-content {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  font-size: inherit;
}

.input-line {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 0;

  .prompt {
    color: #58a6ff;
    font-weight: bold;
    user-select: none;
  }

  .terminal-input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: #c9d1d9;
    font-family: inherit;
    font-size: inherit;
    caret-color: #58a6ff;

    &::placeholder {
      color: #484f58;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-2px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
