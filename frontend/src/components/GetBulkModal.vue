<script setup>
import { ref, computed, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import { snmpGetBulkService } from '@/api/snmp.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  mibTree: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:visible', 'confirm'])

// 表单数据
const form = ref({
  nonRepeaters: 0,
  maxRepetitions: 10,
})

// 选中的OID列表
const selectedOids = ref([])

// Tree组件的展开keys
const treeExpandedKeys = ref([])

// 处理树节点展开
const handleTreeExpand = (keys) => {
  treeExpandedKeys.value = keys
}

// 处理树节点选择（多选）
const handleTreeSelect = (selectedKeys, { selected, node, selectedNodes }) => {
  if (selected && node) {
    // 所有节点都可以被选中
    if (!selectedOids.value.includes(node.oid)) {
      selectedOids.value.push(node.oid)
    }
  }
}

// 搜索关键词
const searchKeyword = ref('')

// 过滤后的树数据
const filteredTreeData = computed(() => {
  if (!searchKeyword.value) {
    return props.mibTree
  }

  const filterTree = (nodes, keyword) => {
    const filtered = []
    for (const node of nodes) {
      const matchesKeyword =
        node.label?.toLowerCase().includes(keyword.toLowerCase()) ||
        node.oid?.toLowerCase().includes(keyword.toLowerCase())

      let children = []
      if (node.children && node.children.length > 0) {
        children = filterTree(node.children, keyword)
      }

      // 如果节点匹配或有匹配的子节点，则包含该节点
      if (matchesKeyword || children.length > 0) {
        filtered.push({
          ...node,
          children: children.length > 0 ? children : undefined,
        })
      }
    }
    return filtered
  }

  return filterTree(props.mibTree, searchKeyword.value)
})

// 选中的OID显示
const selectedOidsDisplay = computed(() => {
  const findNodeByOid = (nodes, targetOid) => {
    for (const node of nodes) {
      if (node.oid === targetOid) {
        return node.label
      }
      if (node.children?.length) {
        const found = findNodeByOid(node.children, targetOid)
        if (found) return found
      }
    }
    return null
  }

  return selectedOids.value.map((oid) => {
    const label = findNodeByOid(props.mibTree, oid)
    return label ? `${label} (${oid})` : oid
  })
})

// 移除选中的OID
const removeSelectedOid = (oid) => {
  const index = selectedOids.value.indexOf(oid)
  if (index > -1) {
    selectedOids.value.splice(index, 1)
  }
}

// 验证表单
const validateForm = () => {
  if (form.value.nonRepeaters < 0) {
    Message.warning('非重复变量数不能小于0')
    return false
  }

  if (form.value.maxRepetitions < 1) {
    Message.warning('最大重复次数不能小于1')
    return false
  }

  if (selectedOids.value.length === 0) {
    Message.warning('请至少选择一个OID')
    return false
  }

  if (form.value.nonRepeaters > selectedOids.value.length) {
    Message.warning('非重复变量数不能大于选择的OID数量')
    return false
  }

  return true
}

// 确认执行
const handleConfirm = async () => {
  if (!validateForm()) {
    return
  }

  try {
    Message.clear()
    const res = await snmpGetBulkService(
      form.value.nonRepeaters,
      form.value.maxRepetitions,
      selectedOids.value,
    )

    if (res.data.code === 0) {
      emit('confirm', {
        nonRepeaters: form.value.nonRepeaters,
        maxRepetitions: form.value.maxRepetitions,
        oids: selectedOids.value,
        result: res.data.data,
      })
      Message.success('GetBulk请求成功')
      handleClose()
    } else {
      Message.warning(res.data.message)
    }
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    Message.error('系统错误')
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
  // 重置表单
  form.value = {
    nonRepeaters: 0,
    maxRepetitions: 10,
  }
  selectedOids.value = []
  searchKeyword.value = ''
  treeExpandedKeys.value = []
}

// 设置默认展开的节点（与MibPage保持一致）
const setDefaultExpanded = () => {
  const defaultExpandedIds = ['1', '1.3', '1.3.6', '1.3.6.1', '1.3.6.1.2', '1.3.6.1.2.1']
  treeExpandedKeys.value = defaultExpandedIds
}

// 监听props.visible变化，设置默认展开
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      setDefaultExpanded()
    }
  },
)
</script>

<template>
  <a-modal
    :visible="visible"
    title="SNMP GetBulk 请求"
    modal-class="get-bulk-modal"
    :modal-style="{ width: '800px', maxWidth: '90vw' }"
    :footer="false"
    :mask-closable="false"
    unmount-on-close
    @cancel="handleClose"
  >
    <div class="get-bulk-content">
      <!-- 参数设置 -->
      <div class="form-section">
        <h4>参数设置</h4>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="非重复变量数 (n)" field="nonRepeaters">
              <a-input-number
                v-model="form.nonRepeaters"
                :min="0"
                :max="selectedOids.length"
                placeholder="0"
                style="width: 100%"
              />
              <div class="form-help">前n个OID只获取单个值</div>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="最大重复次数 (m)" field="maxRepetitions">
              <a-input-number
                v-model="form.maxRepetitions"
                :min="1"
                :max="100"
                placeholder="10"
                style="width: 100%"
              />
              <div class="form-help">后m个OID最多获取m次</div>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- OID选择 -->
      <div class="form-section">
        <h4>OID选择</h4>

        <!-- 已选择的OID -->
        <div class="selected-oids" v-if="selectedOids.length > 0">
          <div class="selected-header">
            <span>已选择 ({{ selectedOids.length }})</span>
          </div>
          <div class="selected-list">
            <a-tag
              v-for="oid in selectedOids"
              :key="oid"
              closable
              @close="removeSelectedOid(oid)"
              class="oid-tag"
            >
              {{ selectedOidsDisplay.find((item) => item.includes(oid)) || oid }}
            </a-tag>
          </div>
        </div>

        <!-- OID搜索和树形选择 -->
        <div class="oid-selector">
          <a-input-search
            v-model="searchKeyword"
            placeholder="搜索OID..."
            allow-clear
            class="search-input"
          />

          <div class="tree-container">
            <a-tree
              :data="filteredTreeData"
              v-model:expanded-keys="treeExpandedKeys"
              :show-line="true"
              :block-node="true"
              :virtual-list-props="{
                height: 250,
                threshold: 50,
                itemKey: 'key',
              }"
              @select="handleTreeSelect"
              @expand="handleTreeExpand"
            >
              <template #title="{ label, oid }">
                <div class="tree-node-content">
                  <span class="tree-node-label">{{ label }}</span>
                  <span class="tree-node-oid">{{ oid }}</span>
                </div>
              </template>
            </a-tree>

            <div v-if="filteredTreeData.length === 0" class="empty-result">暂无匹配的OID</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="modal-footer">
      <a-space>
        <a-button @click="handleClose">取消</a-button>
        <a-button type="primary" @click="handleConfirm"> 执行 GetBulk </a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<style scoped lang="scss">
.get-bulk-content {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 8px;
}

.form-section {
  margin-bottom: 24px;

  h4 {
    margin: 0 0 16px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    border-bottom: 2px solid #e5e7eb;
    padding-bottom: 8px;
  }
}

.form-help {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
  line-height: 1.4;
}

.selected-oids {
  margin-bottom: 16px;

  .selected-header {
    font-weight: 500;
    color: #374151;
    margin-bottom: 8px;
  }

  .selected-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .oid-tag {
      max-width: 300px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.oid-selector {
  .search-input {
    margin-bottom: 12px;
  }

  .tree-container {
    max-height: 300px;
    overflow: hidden;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    background: #ffffff;
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

  .empty-result {
    padding: 32px 16px;
    text-align: center;
    color: #6b7280;
    font-size: 14px;
  }
}

.modal-footer {
  margin-top: 24px;
  text-align: right;
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
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

/* 自定义滚动条 */
.get-bulk-content::-webkit-scrollbar,
.tree-container::-webkit-scrollbar {
  width: 6px;
}

.get-bulk-content::-webkit-scrollbar-track,
.tree-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.get-bulk-content::-webkit-scrollbar-thumb,
.tree-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
  transition: background 0.2s ease;
}

.get-bulk-content::-webkit-scrollbar-thumb:hover,
.tree-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
