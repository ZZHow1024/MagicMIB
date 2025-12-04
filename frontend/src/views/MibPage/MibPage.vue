<script setup>
import { computed, ref } from 'vue'
import { getAuthenticationService, setAuthenticationService } from '@/api/authentication.js'
import { Message } from '@arco-design/web-vue'
import { snmpGetNextService, snmpGetService, snmpWalkService, snmpGetSubtreeService } from '@/api/snmp.js'
import { getMibService, loadMibService } from '@/api/mib.js'
import { useAuthenticationStore } from '@/stores'
import GetBulkModal from '@/components/GetBulkModal.vue'

const oidInput = ref('1.3.6.1.2.1.1.1.0')
const operations = ['Get', 'GetNext', 'GetBulk', 'Walk', 'GetSubtree']
const selectedOperation = ref(operations[0])

const isAdvancedModalOpen = ref(false)
const isMibTreeModalOpen = ref(false)
const isGetBulkModalOpen = ref(false)

// 使用 Pinia store
const authenticationStore = useAuthenticationStore()

const advancedForm = ref({
  address: '127.0.0.1',
  port: '161',
  readCommunity: 'public',
  writeCommunity: 'public',
  snmpVersion: 1,
})

const mibTreeOptions = [
  { label: 'SNMPv2-SMI', value: 'SNMPv2-SMI', disabled: false },
  { label: 'SNMPv2-MIB', value: 'SNMPv2-MIB', disabled: false },
  { label: 'RFC1213-MIB', value: 'RFC1213-MIB', disabled: true },
  { label: 'IF-MIB', value: 'IF-MIB', disabled: false },
  { label: 'IP-MIB', value: 'IP-MIB', disabled: false },
  { label: 'TCP-MIB', value: 'TCP-MIB', disabled: false },
  { label: 'UDP-MIB', value: 'UDP-MIB', disabled: false },
  { label: 'RMON-MIB', value: 'RMON-MIB', disabled: false },
  { label: 'RMON2-MIB', value: 'RMON2-MIB', disabled: false },
]
const mibFiles = ref([])
const mibTree = ref([])
const mibLoading = ref(true)
const selectedNode = ref(null)

// 处理树节点选择
const handleSelect = (selectedKeysArray, { node }) => {
  // 查找父节点
  const parentNode = findParentNode(mibTree.value, node)
  selectNode(node, parentNode)
}

// 查找父节点的辅助函数
const findParentNode = (nodes, targetNode, parent = null) => {
  for (const node of nodes) {
    if (node.key === targetNode.key) {
      return parent
    }
    if (node.children?.length) {
      const found = findParentNode(node.children, targetNode, node)
      if (found !== null) return found
    }
  }
  return null
}

// 默认展开的节点keys - 设置默认展开的节点
const expandedKeys = ref([])

// 处理树节点展开
const handleTreeExpand = (keys) => {
  expandedKeys.value = keys
}

// 选中的节点keys - 响应式处理
const selectedKeys = ref([])

const detailFields = computed(() => {
  if (!selectedNode.value) {
    return []
  }

  const node = selectedNode.value
  return [
    { label: '名称', value: node.label },
    { label: 'OID', value: node.oid },
    { label: 'MIB', value: node.mib },
    { label: '语法', value: node.syntax },
    { label: '权限', value: node.access },
    { label: '状态', value: node.status },
  ]
})

const descriptionText = computed(
  () => selectedNode.value?.description ?? '选择树节点以查看详细信息。',
)

const resultRows = ref([])

const hasResults = computed(() => resultRows.value.length > 0)

const selectNode = (node, parentNode = null) => {
  if (!node) return
  selectedNode.value = node

  // 判断是否需要补 .0
  let oid = node.oid || ''
  const isLeafNode = !node.children || node.children.length === 0
  const parentLabelHasEntry = parentNode?.label?.includes('Entry') ?? false
  const isObjectType = node.syntax === 'OBJECT-TYPE'

  // 如果父节点label不包含Entry,并且自己是叶子节点,并且syntax为OBJECT-TYPE,则补.0
  if (isLeafNode && !parentLabelHasEntry && isObjectType && oid && !oid.endsWith('.0')) {
    oid = oid + '.0'
  }

  oidInput.value = oid
  selectedKeys.value = node.key ? [node.key] : [] // 同步更新选中状态
}

const goLoading = ref(false)
const handleGo = async () => {
  goLoading.value = true
  if (selectedOperation.value === operations[0]) await snmpGet()
  else if (selectedOperation.value === operations[1]) await snmpGetNext()
  else if (selectedOperation.value === operations[2]) await handleGetBulk()
  else if (selectedOperation.value === operations[3]) await snmpWalk()
  else if (selectedOperation.value === operations[4]) await snmpGetSubtree()
  goLoading.value = false
}

const setAuthenticationLoading = ref(false)
const openAdvancedModal = () => {
  getAuthentication()
  isAdvancedModalOpen.value = true
}

const confirmAdvancedModal = async () => {
  setAuthenticationLoading.value = true
  await setAuthentication()
  setAuthenticationLoading.value = false
  isAdvancedModalOpen.value = false
}
const closeAdvancedModal = () => {
  isAdvancedModalOpen.value = false
}

const openMibTreeModal = () => {
  isMibTreeModalOpen.value = true
}

const mibFilesLoading = ref(false)
const confirmMibFiles = async () => {
  mibFilesLoading.value = true
  await loadMib()
  mibFilesLoading.value = false
  isMibTreeModalOpen.value = false
}

const closeMibTreeModal = () => {
  isMibTreeModalOpen.value = false
}

// 处理GetBulk操作
const handleGetBulk = () => {
  isGetBulkModalOpen.value = true
}

// GetBulk确认处理
const handleGetBulkConfirm = (bulkData) => {
  console.log('GetBulk result:', bulkData)

  // 处理GetBulk结果数据
  const results = []

  if (bulkData.result && bulkData.result.data) {
    // 使用新的后端返回格式
    bulkData.result.data.forEach((item, index) => {
      const newResult = {
        id: `bulk-${Date.now()}-${index}`,
        name: item.name || `GetBulk[${index}]`,
        oid: item.oid,
        value: item.value || '',
        type: item.type || 'OCTET STRING',
        endpoint: `${bulkData.result.address}:${bulkData.result.port}`,
      }
      results.push(newResult)
    })
  }

  // 添加到结果表格
  resultRows.value.push(...results)

  Message.success(`GetBulk成功，获取了 ${results.length} 条数据`)
}

// 清空结果表格
const clearResults = () => {
  resultRows.value = []
}

// 设置身份认证信息
const setAuthentication = async () => {
  try {
    const res = await setAuthenticationService(advancedForm.value)
    if (res.data.code === 0) {
      Message.success('配置成功')
      // 配置成功后存储到 Pinia
      authenticationStore.setAuthConfig(advancedForm.value)
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}

// 获取身份认证信息
const getAuthentication = async () => {
  try {
    const res = await getAuthenticationService()
    if (res.data.code === 0) {
      advancedForm.value = res.data.data
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}
getAuthentication()

// 发起 SNMP Get 请求
const snmpGet = async () => {
  const currentOid = oidInput.value

  try {
    Message.clear()
    const res = await snmpGetService(currentOid)
    if (res.data.code === 0) {
      const responseData = res.data.data
      if (responseData.data && responseData.data.length > 0) {
        responseData.data.forEach((item, index) => {
          const newResult = {
            id: `get-${Date.now()}-${index}`,
            name: item.name || 'Unknown',
            oid: item.oid,
            value: item.value || '',
            type: item.type || 'OCTET STRING',
            endpoint: `${responseData.address}:${responseData.port}`,
          }
          resultRows.value.push(newResult)
        })
      }
      Message.success('请求成功')
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}

// 发起 SNMP GetNext 请求
const snmpGetNext = async () => {
  const currentOid = oidInput.value

  try {
    Message.clear()
    const res = await snmpGetNextService(currentOid)
    if (res.data.code === 0) {
      const responseData = res.data.data
      if (responseData.data && responseData.data.length > 0) {
        responseData.data.forEach((item, index) => {
          const newResult = {
            id: `next-${Date.now()}-${index}`,
            name: item.name || 'Unknown',
            oid: item.oid,
            value: item.value || '',
            type: item.type || 'OCTET STRING',
            endpoint: `${responseData.address}:${responseData.port}`,
          }
          resultRows.value.push(newResult)
          
          // 更新输入框的OID为最后一个返回的OID
          if (index === responseData.data.length - 1) {
            oidInput.value = item.oid
          }
        })
      }
      Message.success('请求成功')
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}

// 发起 SNMP Walk 请求
const snmpWalk = async () => {
  const currentOid = oidInput.value

  try {
    Message.clear()
    const res = await snmpWalkService(currentOid)
    if (res.data.code === 0) {
      const responseData = res.data.data
      if (responseData.data && responseData.data.length > 0) {
        responseData.data.forEach((item, index) => {
          const newResult = {
            id: `walk-${Date.now()}-${index}`,
            name: item.name || 'Unknown',
            oid: item.oid,
            value: item.value || '',
            type: item.type || 'OCTET STRING',
            endpoint: `${responseData.address}:${responseData.port}`,
          }
          resultRows.value.push(newResult)
        })
      }
      Message.success(`Walk成功，获取了 ${responseData.data?.length || 0} 条数据`)
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}

// 发起 SNMP GetSubtree 请求
const snmpGetSubtree = async () => {
  const currentOid = oidInput.value

  try {
    Message.clear()
    const res = await snmpGetSubtreeService(currentOid)
    if (res.data.code === 0) {
      const responseData = res.data.data
      if (responseData.data && responseData.data.length > 0) {
        responseData.data.forEach((item, index) => {
          const newResult = {
            id: `subtree-${Date.now()}-${index}`,
            name: item.name || 'Unknown',
            oid: item.oid,
            value: item.value || '',
            type: item.type || 'OCTET STRING',
            endpoint: `${responseData.address}:${responseData.port}`,
          }
          resultRows.value.push(newResult)
        })
      }
      Message.success(`GetSubtree成功，获取了 ${responseData.data?.length || 0} 条数据`)
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}

// 获取当前加载的 MIB 文件
const getMib = async () => {
  mibLoading.value = true
  try {
    const res = await getMibService()
    if (res.data.code === 0) {
      mibFiles.value = res.data.data.mibFiles || []
      mibTree.value = res.data.data.mibTree || []
      console.log('MIB tree loaded:', mibTree.value)

      // 设置默认展开的节点：iso, org, dod, internet, mib-2
      const defaultExpandedIds = ['1', '1.3', '1.3.6', '1.3.6.1', '1.3.6.1.2', '1.3.6.1.2.1']
      expandedKeys.value = defaultExpandedIds

      // 如果没有选中节点且有数据，默认选择第一个叶子节点
      if (!selectedNode.value && mibTree.value && mibTree.value.length > 0) {
        const findFirstLeaf = (nodes, parent = null) => {
          for (const node of nodes) {
            if (!node.children || node.children.length === 0) {
              return { node, parent }
            }
            const leaf = findFirstLeaf(node.children, node)
            if (leaf) return leaf
          }
          return null
        }
        const result = findFirstLeaf(mibTree.value)
        if (result) {
          selectNode(result.node, result.parent)
        }
      }
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  } finally {
    mibLoading.value = false
  }
}
getMib()
// 加载 MIB 文件
const loadMib = async () => {
  try {
    const res = await loadMibService(mibFiles.value)
    if (res.data.code === 0) {
      mibTree.value = res.data.data || []
      Message.success('加载成功')
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}
</script>

<template>
  <div class="mib-page">
    <section class="control-bar">
      <div class="field address-field">
        <label for="mib-address">IP地址</label>
        <input
          id="mib-address"
          v-model="advancedForm.address"
          type="text"
          @blur="setAuthentication"
        />
      </div>
      <a-button class="advanced-button" type="outline" size="small" @click="openAdvancedModal">
        高级...
      </a-button>
      <div class="field oid-field">
        <label for="mib-oid">OID</label>
        <input id="mib-oid" v-model="oidInput" type="text" />
      </div>
      <div class="field operation-field">
        <label for="mib-operation">操作</label>
        <select id="mib-operation" v-model="selectedOperation">
          <option v-for="operation in operations" :key="operation" :value="operation">
            {{ operation }}
          </option>
        </select>
      </div>
      <button class="go-button" type="button" @click="handleGo">Go</button>
    </section>

    <section class="content-area">
      <div class="left-panel">
        <div class="card tree-section">
          <div class="section-header">
            <h3>MIB Tree</h3>
            <button class="manage-mib-button" type="button" @click="openMibTreeModal">
              管理MIB树
            </button>
          </div>
          <div class="arco-tree-container">
            <div v-if="mibLoading" class="loading-state">
              <a-spin size="large" />
              <p>正在加载MIB树...</p>
            </div>
            <a-tree
              ref="treeRef"
              v-else
              :data="mibTree"
              :selected-keys="selectedKeys"
              v-model:expanded-keys="expandedKeys"
              :show-line="true"
              :block-node="true"
              :virtual-list-props="{
                height: 200,
                threshold: 50,
                itemKey: 'key',
              }"
              @select="handleSelect"
              @expand="handleTreeExpand"
            >
              <template #title="{ label, oid }">
                <div class="tree-node-content">
                  <span class="tree-node-label">{{ label }}</span>
                  <span class="tree-node-oid">{{ oid }}</span>
                </div>
              </template>
            </a-tree>
          </div>
        </div>

        <div class="card detail-section">
          <div class="section-header">
            <h3>对象详情</h3>
            <span class="hint" v-if="selectedNode">OID: {{ selectedNode.oid }}</span>
          </div>
          <div class="detail-content">
            <div v-if="selectedNode" class="detail-grid">
              <div v-for="field in detailFields" :key="field.label" class="detail-row">
                <span class="label">{{ field.label }}</span>
                <span class="value">{{ field.value }}</span>
              </div>
              <div class="description-block">
                <span class="label">描述</span>
                <p class="description">{{ descriptionText }}</p>
              </div>
            </div>
            <div v-else class="empty-state">请选择左侧树节点查看详细信息</div>
          </div>
        </div>
      </div>

      <div class="right-panel card">
        <div class="section-header">
          <h3>Result Table</h3>
          <div class="table-meta">
            <span class="hint" v-if="hasResults">{{ resultRows.length }} 条记录</span>
            <button v-if="hasResults" class="refresh-button" type="button" @click="clearResults">
              清空
            </button>
          </div>
        </div>
        <div v-if="hasResults" class="result-table-wrapper">
          <div class="result-table-container">
            <table class="result-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Name / OID</th>
                  <th>Value</th>
                  <th>Type</th>
                  <th>IP:Port</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in resultRows" :key="row.id">
                  <td>{{ index + 1 }}</td>
                  <td>
                    <p class="name">{{ row.name }}</p>
                    <p class="oid">{{ row.oid }}</p>
                  </td>
                  <td>{{ row.value }}</td>
                  <td>{{ row.type }}</td>
                  <td>{{ row.endpoint }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else class="no-result">暂无数据，点击 GO 按钮执行 SNMP 请求后将展示返回结果</div>
      </div>
    </section>

    <a-modal
      v-model:visible="isAdvancedModalOpen"
      title="SNMP 代理的高级属性"
      modal-class="advanced-modal"
      :modal-style="{ width: '520px', maxWidth: '90vw' }"
      :footer="false"
      :mask-closable="true"
      unmount-on-close
      @cancel="closeAdvancedModal"
    >
      <a-form
        class="advanced-form"
        :model="advancedForm"
        layout="horizontal"
        label-align="right"
        :label-col-props="{ span: 8 }"
        :wrapper-col-props="{ span: 16 }"
        size="small"
      >
        <a-form-item field="address" label="IP地址">
          <a-input v-model="advancedForm.address" allow-clear />
        </a-form-item>
        <a-form-item field="port" label="端口">
          <a-input v-model="advancedForm.port" allow-clear />
        </a-form-item>
        <a-form-item field="readCommunity" label="只读共同体名">
          <a-input v-model="advancedForm.readCommunity" allow-clear />
        </a-form-item>
        <a-form-item field="writeCommunity" label="读写共同体名">
          <a-input v-model="advancedForm.writeCommunity" allow-clear />
        </a-form-item>
        <a-form-item field="snmpVersion" label="SNMP版本">
          <a-select v-model="advancedForm.snmpVersion">
            <a-option :value="1">1</a-option>
            <a-option :value="2">2</a-option>
            <a-option :value="3" disabled>3</a-option>
          </a-select>
        </a-form-item>
      </a-form>
      <div class="advanced-modal-footer">
        <a-space>
          <a-button
            type="primary"
            size="small"
            @click="confirmAdvancedModal"
            :loading="setAuthenticationLoading"
            >确定</a-button
          >
          <a-button size="small" @click="closeAdvancedModal">取消</a-button>
        </a-space>
      </div>
    </a-modal>

    <a-modal
      v-model:visible="isMibTreeModalOpen"
      title="管理MIB树"
      modal-class="mib-tree-modal"
      :modal-style="{ width: '400px', maxWidth: '90vw' }"
      :footer="false"
      :mask-closable="true"
      unmount-on-close
      @cancel="closeMibTreeModal"
    >
      <div class="mib-tree-content">
        <a-checkbox-group :options="mibTreeOptions" direction="vertical" v-model="mibFiles" />
        <div class="mib-tree-footer">
          <a-space>
            <a-button
              type="primary"
              size="small"
              @click="confirmMibFiles"
              :loading="mibFilesLoading"
            >
              确定
            </a-button>
            <a-button size="small" @click="closeMibTreeModal">取消</a-button>
          </a-space>
        </div>
      </div>
    </a-modal>

    <!-- GetBulk弹窗 -->
    <GetBulkModal
      v-model:visible="isGetBulkModalOpen"
      :mib-tree="mibTree"
      @confirm="handleGetBulkConfirm"
    />
  </div>
</template>

<style scoped lang="scss">
.mib-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 170px);
  padding: 16px;
  background: #f4f5f7;
  overflow: hidden;
}

.control-bar {
  display: grid;
  grid-template-columns: minmax(200px, 1fr) auto minmax(260px, 2fr) minmax(160px, 0.8fr) auto;
  gap: 12px;
  align-items: end;
  background: #ffffff;
  border: 1px solid #d9dfe7;
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.08);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-size: 13px;
  color: #4a5568;
}

input,
select {
  height: 34px;
  border: 1px solid #cfd8e3;
  border-radius: 6px;
  padding: 0 10px;
  font-size: 14px;
  background: #fefefe;
  transition: border-color 0.2s ease;
}

input:focus,
select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.15);
}

.advanced-button,
.go-button,
.refresh-button {
  height: 34px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  padding: 0 18px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.advanced-button {
  background: #ffffff;
  border-color: #d1d9e6;
  color: #1f2937;
}

.advanced-button:hover {
  border-color: #94a3b8;
}

.go-button {
  background: #059669;
  color: #ffffff;
  box-shadow: inset 0 -2px 0 rgba(0, 0, 0, 0.12);
}

.go-button:hover {
  background: #047857;
}

.refresh-button {
  background: #1d4ed8;
  color: #ffffff;
}

.refresh-button:hover {
  background: #1e40af;
}

.content-area {
  display: grid;
  grid-template-columns: 11fr 14fr;
  gap: 16px;
  flex: 1;
  overflow: hidden;
}

.left-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
  overflow: hidden;
}

.right-panel {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: calc(100vh - 305px);
}

.card {
  background: #ffffff;
  border: 1px solid #dfe5ef;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(15, 23, 42, 0.04);
}

.tree-section {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.detail-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.detail-content {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

/* 自定义滚动条样式 */
.detail-content::-webkit-scrollbar,
.result-table-container::-webkit-scrollbar {
  width: 6px;
}

.detail-content::-webkit-scrollbar-track,
.result-table-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.detail-content::-webkit-scrollbar-thumb,
.result-table-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
  transition: background 0.2s ease;
}

.detail-content::-webkit-scrollbar-thumb:hover,
.result-table-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.section-header {
  height: 5px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-header h3 {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.hint {
  font-size: 12px;
  color: #94a3b8;
}

.table-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.arco-tree-container {
  border-top: 1px solid #edf1f7;
  margin-top: 12px;
  max-height: 320px;
  overflow: hidden;
}

.tree-node-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  width: 100%;
  padding: 2px 0;
}

.tree-node-label {
  flex: 1;
  font-size: 13px;
  color: #1f2937;
  white-space: nowrap;
  font-weight: 500;
}

.tree-node-oid {
  font-size: 12px;
  color: #64748b;
  white-space: nowrap;
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

/* Arco Tree组件样式定制 */
:deep(.arco-tree-node) {
  padding: 4px 8px;
  transition: all 0.2s ease;
  border-radius: 6px;
  margin: 2px 4px;
}

:deep(.arco-tree-node:hover) {
  background: #f0f9ff;
}

:deep(.arco-tree-node-selected) {
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
}

:deep(.arco-tree-node-title) {
  width: 100%;
}

:deep(.arco-tree-node-switcher) {
  color: #64748b;
  transition: transform 0.2s ease;
}

:deep(.arco-tree-node-switcher:hover) {
  color: #1d4ed8;
}

:deep(.arco-tree-node-indent-line) {
  border-color: #e2e8f0;
}

:deep(.arco-virtual-list) {
  border-radius: 8px;
}

.loading-state,
.empty-tree-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 320px;
  gap: 16px;
  color: #94a3b8;
  font-size: 14px;
}

.empty-tree-state .refresh-button {
  margin-top: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 14px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-row .label,
.description-block .label {
  font-size: 12px;
  color: #94a3b8;
  text-transform: uppercase;
}

.detail-row .value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
}

.description-block {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 8px;
}

.description-block .description {
  font-size: 13px;
  line-height: 1.4;
  color: #4b5563;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 12px;
}

.empty-state {
  padding: 24px;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
}

.result-table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: inset 0 1px 0 rgba(15, 23, 42, 0.04);
}

.result-table-container {
  flex: 1;
  overflow-y: auto;
}

.result-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.result-table thead {
  background: #f8fafc;
  position: sticky;
  top: 0;
  z-index: 10;
}

.result-table th,
.result-table td {
  padding: 10px 12px;
  border-bottom: 1px solid #edf2f7;
  text-align: left;
}

.result-table th {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.result-table tbody tr:hover {
  background: #f1f5f9;
}

.result-table .name {
  font-weight: 600;
  color: #111827;
  margin-bottom: 2px;
}

.result-table .oid {
  color: #94a3b8;
  font-size: 12px;
}

.no-result {
  flex: 1;
  border: 1px dashed #c7ced9;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 14px;
  padding: 24px;
  margin: 0;
}

@media (max-width: 1280px) {
  .content-area {
    grid-template-columns: 1fr;
  }
}

.advanced-modal-footer {
  margin-top: 4px;
  display: flex;
  justify-content: center;
}

.manage-mib-button {
  height: 28px;
  border-radius: 4px;
  font-size: 12px;
  padding: 0 12px;
  border: 1px solid #d1d9e6;
  background: #ffffff;
  color: #1f2937;
  cursor: pointer;
  transition: all 0.2s ease;
}

.manage-mib-button:hover {
  border-color: #94a3b8;
  background: #f8fafc;
}

.mib-tree-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.mib-tree-footer {
  display: flex;
  justify-content: center;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
}
</style>
