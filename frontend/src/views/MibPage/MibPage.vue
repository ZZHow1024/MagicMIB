<script setup>
import { computed, ref } from 'vue'

const address = ref('127.0.0.1')
const oidInput = ref('1.3.6.1.2.1.1.1.0')
const operations = ['Get', 'GetNext', 'Walk']
const selectedOperation = ref(operations[0])

const mibTree = ref([
  {
    id: 'iso',
    label: 'iso',
    oid: '1',
    mib: 'root',
    syntax: 'SEQUENCE',
    access: 'not-accessible',
    status: 'mandatory',
    description: 'Root node of the global OID namespace.',
    children: [
      {
        id: 'org',
        label: 'org',
        oid: '1.3',
        mib: 'root',
        syntax: 'SEQUENCE',
        access: 'not-accessible',
        status: 'mandatory',
        description: 'Routes object identifiers allocated to organizations.',
        children: [
          {
            id: 'dod',
            label: 'dod',
            oid: '1.3.6',
            mib: 'root',
            syntax: 'SEQUENCE',
            access: 'not-accessible',
            status: 'mandatory',
            description: 'Department of Defense branch.',
            children: [
              {
                id: 'internet',
                label: 'internet',
                oid: '1.3.6.1',
                mib: 'root',
                syntax: 'SEQUENCE',
                access: 'not-accessible',
                status: 'mandatory',
                description: 'Internet-specific information.',
                children: [
                  {
                    id: 'mgmt',
                    label: 'mgmt',
                    oid: '1.3.6.1.2',
                    mib: 'root',
                    syntax: 'SEQUENCE',
                    access: 'not-accessible',
                    status: 'mandatory',
                    description: 'Management branch containing standard MIBs.',
                    children: [
                      {
                        id: 'mib2',
                        label: 'mib-2',
                        oid: '1.3.6.1.2.1',
                        mib: 'RFC1213-MIB',
                        syntax: 'SEQUENCE',
                        access: 'not-accessible',
                        status: 'mandatory',
                        description: 'Standard managed objects defined in RFC1213.',
                        children: [
                          {
                            id: 'system',
                            label: 'system',
                            oid: '1.3.6.1.2.1.1',
                            mib: 'RFC1213-MIB',
                            syntax: 'SEQUENCE',
                            access: 'not-accessible',
                            status: 'mandatory',
                            description: 'System group exposing device level properties.',
                            children: [
                              {
                                id: 'sysDescr',
                                label: 'sysDescr',
                                oid: '1.3.6.1.2.1.1.1.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'DisplayString (SIZE 0..255)',
                                access: 'read-only',
                                status: 'mandatory',
                                description:
                                  'Textual description of the entity including hardware, operating system, and networking software.',
                              },
                              {
                                id: 'sysObjectID',
                                label: 'sysObjectID',
                                oid: '1.3.6.1.2.1.1.2.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'OBJECT IDENTIFIER',
                                access: 'read-only',
                                status: 'mandatory',
                                description:
                                  'Vendor specific identification for the managed device.',
                              },
                              {
                                id: 'sysContact',
                                label: 'sysContact',
                                oid: '1.3.6.1.2.1.1.4.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'DisplayString',
                                access: 'read-write',
                                status: 'mandatory',
                                description:
                                  'The textual identification of the system contact person.',
                              },
                              {
                                id: 'sysName',
                                label: 'sysName',
                                oid: '1.3.6.1.2.1.1.5.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'DisplayString',
                                access: 'read-write',
                                status: 'mandatory',
                                description: 'An administratively assigned name for the node.',
                              },
                              {
                                id: 'sysLocation',
                                label: 'sysLocation',
                                oid: '1.3.6.1.2.1.1.6.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'DisplayString',
                                access: 'read-write',
                                status: 'mandatory',
                                description: 'The physical location of this node.',
                              },
                              {
                                id: 'sysServices',
                                label: 'sysServices',
                                oid: '1.3.6.1.2.1.1.7.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'Integer32',
                                access: 'read-only',
                                status: 'mandatory',
                                description:
                                  'A value indicating the set of services that this entity offers.',
                              },
                            ],
                          },
                          {
                            id: 'interfaces',
                            label: 'interfaces',
                            oid: '1.3.6.1.2.1.2',
                            mib: 'RFC1213-MIB',
                            syntax: 'SEQUENCE',
                            access: 'not-accessible',
                            status: 'mandatory',
                            description: 'Information about network interfaces.',
                            children: [
                              {
                                id: 'ifNumber',
                                label: 'ifNumber',
                                oid: '1.3.6.1.2.1.2.1.0',
                                mib: 'RFC1213-MIB',
                                syntax: 'Integer32',
                                access: 'read-only',
                                status: 'mandatory',
                                description:
                                  'The number of network interfaces present on the system.',
                              },
                              {
                                id: 'ifTable',
                                label: 'ifTable',
                                oid: '1.3.6.1.2.1.2.2',
                                mib: 'RFC1213-MIB',
                                syntax: 'SEQUENCE OF ifEntry',
                                access: 'not-accessible',
                                status: 'mandatory',
                                description:
                                  'Table of interface entries describing individual interfaces.',
                                children: [
                                  {
                                    id: 'ifEntry',
                                    label: 'ifEntry',
                                    oid: '1.3.6.1.2.1.2.2.1',
                                    mib: 'RFC1213-MIB',
                                    syntax: 'SEQUENCE',
                                    access: 'not-accessible',
                                    status: 'mandatory',
                                    description: 'Entry describing a particular interface row.',
                                  },
                                ],
                              },
                            ],
                          },
                        ],
                      },
                    ],
                  },
                ],
              },
            ],
          },
        ],
      },
    ],
  },
])

const selectedNode = ref(mibTree.value[0]?.children?.[0] ?? mibTree.value[0] ?? null)

const treeRows = computed(() => {
  const rows = []
  const traverse = (nodes, depth = 0) => {
    nodes.forEach((node) => {
      rows.push({ node, depth })
      if (node.children?.length) {
        traverse(node.children, depth + 1)
      }
    })
  }

  traverse(mibTree.value)
  return rows
})

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

const resultRows = ref([
  {
    id: 'row-1',
    name: 'sysDescr',
    oid: '1.3.6.1.2.1.1.1.0',
    value: 'Example SNMP Agent v2 (Mock)',
    type: 'OCTET STRING',
    endpoint: '127.0.0.1:161',
  },
  {
    id: 'row-2',
    name: 'sysObjectID',
    oid: '1.3.6.1.2.1.1.2.0',
    value: '1.3.6.1.4.1.9.1.1208',
    type: 'OBJECT IDENTIFIER',
    endpoint: '127.0.0.1:161',
  },
  {
    id: 'row-3',
    name: 'sysName',
    oid: '1.3.6.1.2.1.1.5.0',
    value: 'SNMP-LAB-NODE',
    type: 'OCTET STRING',
    endpoint: '127.0.0.1:161',
  },
])

const hasResults = computed(() => resultRows.value.length > 0)

const selectNode = (node) => {
  selectedNode.value = node
}

const handleGo = () => {
  console.log('Trigger operation:', selectedOperation.value, oidInput.value)
}
</script>

<template>
  <div class="mib-page">
    <section class="control-bar">
      <div class="field address-field">
        <label for="mib-address">Address</label>
        <input id="mib-address" v-model="address" type="text" />
      </div>
      <button class="advanced-button" type="button">Advanced...</button>
      <div class="field oid-field">
        <label for="mib-oid">OID</label>
        <input id="mib-oid" v-model="oidInput" type="text" />
      </div>
      <div class="field operation-field">
        <label for="mib-operation">Operation</label>
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
            <span class="hint">模拟数据</span>
          </div>
          <div class="tree-container" role="tree">
            <div
              v-for="row in treeRows"
              :key="row.node.id"
              class="tree-row"
              :class="{ active: selectedNode && selectedNode.id === row.node.id }"
              :style="{ paddingLeft: `${row.depth * 16 + 12}px` }"
              role="treeitem"
              tabindex="0"
              @click="selectNode(row.node)"
              @keyup.enter="selectNode(row.node)"
            >
              <span class="tree-label">{{ row.node.label }}</span>
              <span class="tree-oid">{{ row.node.oid }}</span>
            </div>
          </div>
        </div>

        <div class="card detail-section">
          <div class="section-header">
            <h3>对象详情</h3>
            <span class="hint" v-if="selectedNode">OID: {{ selectedNode.oid }}</span>
          </div>
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

      <div class="right-panel card">
        <div class="section-header">
          <h3>Result Table</h3>
        </div>
        <div v-if="hasResults" class="result-table-wrapper">
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
        <div v-else class="no-result">暂无数据，点击 GO 按钮后将展示返回结果</div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
.mib-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  padding: 16px;
  background: #f4f5f7;
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
}

.left-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.right-panel {
  display: flex;
  flex-direction: column;
}

.card {
  background: #ffffff;
  border: 1px solid #dfe5ef;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(15, 23, 42, 0.04);
}

.section-header {
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

.tree-container {
  border-top: 1px solid #edf1f7;
  margin-top: 12px;
  max-height: 320px;
  overflow-y: auto;
}

.tree-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: #1f2937;
  cursor: pointer;
  transition: background 0.15s ease;
}

.tree-row:nth-child(odd) {
  background: #f9fafb;
}

.tree-row:hover {
  background: #e3f2fd;
}

.tree-row.active {
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
}

.tree-label {
  flex: 1;
  white-space: nowrap;
}

.tree-oid {
  font-size: 12px;
  color: #64748b;
  white-space: nowrap;
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
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: inset 0 1px 0 rgba(15, 23, 42, 0.04);
}

.result-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.result-table thead {
  background: #f8fafc;
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
}

@media (max-width: 1280px) {
  .content-area {
    grid-template-columns: 1fr;
  }
}
</style>
