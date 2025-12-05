<script setup>
import { ref, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import { snmpSetService, snmpGetService } from '@/api/snmp.js'
import { useAuthenticationStore } from '@/stores'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  initialOid: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:visible', 'confirm'])

const authenticationStore = useAuthenticationStore()

// 表单数据
const formData = ref({
  oid: '',
  value: '',
  type: 'i',
})

// 数据类型选项
const typeOptions = [
  { label: 'INTEGER (i)', value: 'i' },
  { label: 'OCTET STRING (s)', value: 's' },
  { label: 'HEX-STRING (x)', value: 'x' },
  { label: 'DECIMAL (d)', value: 'd' },
  { label: 'IPADDRESS (a)', value: 'a' },
  { label: 'OBJECTID (o)', value: 'o' },
  { label: 'TIMETICKS (t)', value: 't' },
  { label: 'UNSIGNED32 (u)', value: 'u' },
  { label: 'COUNTER32 (c)', value: 'c' },
  { label: 'GAUGE32 (g)', value: 'g' },
]

const loading = ref(false)
const fetchingData = ref(false)

// SNMP类型映射到表单类型
const mapSnmpTypeToFormType = (snmpType) => {
  const typeMap = {
    'INTEGER': 'i',
    'INTEGER32': 'i',
    'OCTETSTRING': 's',
    'OCTET STRING': 's',
    'HEXSTRING': 'x',
    'HEX-STRING': 'x',
    'DECIMAL': 'd',
    'DECIMAL STRING': 'd',
    'IPADDRESS': 'a',
    'IP ADDRESS': 'a',
    'OBJECTID': 'o',
    'OBJECT IDENTIFIER': 'o',
    'TIMETICKS': 't',
    'TIME TICKS': 't',
    'UNSIGNED32': 'u',
    'UNSIGNEDINTEGER32': 'u',
    'COUNTER32': 'c',
    'COUNTER': 'c',
    'COUNTER64': 'c',
    'GAUGE32': 'g',
    'GAUGE': 'g',
  }
  
  const upperType = snmpType?.toUpperCase().replace(/\s+/g, '')
  return typeMap[upperType] || 'i'
}

// 获取当前 OID 的值
const fetchCurrentValue = async (oid) => {
  if (!oid) return
  
  fetchingData.value = true
  try {
    const res = await snmpGetService(oid)
    if (res.data.code === 0) {
      const responseData = res.data.data
      if (responseData.data && responseData.data.length > 0) {
        const item = responseData.data[0]
        // 填充表单
        formData.value.value = item.value || ''
        formData.value.type = mapSnmpTypeToFormType(item.type)
      }
    } else {
      console.warn('Get operation failed:', res.data.message)
    }
  } catch (e) {
    console.error('Failed to fetch current value:', e)
  } finally {
    fetchingData.value = false
  }
}

// 监听弹窗打开,初始化 OID 并获取当前值
watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      formData.value.oid = props.initialOid || ''
      formData.value.value = ''
      formData.value.type = 'i'
      
      // 如果有 OID,执行 Get 操作获取当前值
      if (props.initialOid) {
        await fetchCurrentValue(props.initialOid)
      }
    }
  },
)

// 取消
const handleCancel = () => {
  emit('update:visible', false)
}

// 确认执行 Set 操作
const handleConfirm = async () => {
  // 验证表单
  if (!formData.value.oid) {
    Message.warning('请输入 OID')
    return
  }
  if (!formData.value.value) {
    Message.warning('请输入值')
    return
  }

  loading.value = true

  try {
    const res = await snmpSetService(
      formData.value.oid,
      formData.value.value,
      formData.value.type,
    )

    if (res.data.code === 0) {
      Message.success('Set 操作成功')
      
      const authConfig = authenticationStore.getAuthConfig()
      
      // 返回结果数据
      emit('confirm', {
        result: {
          ...res.data.data,
          address: authConfig.address,
          port: authConfig.port,
        },
        formData: { ...formData.value },
      })
      
      emit('update:visible', false)
    } else {
      Message.warning(res.data.message || 'Set 操作失败')
    }
  } catch (e) {
    console.error('Set error:', e)
    Message.error('系统错误')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <a-modal
    :visible="visible"
    title="SNMP Set 操作"
    modal-class="set-modal"
    :modal-style="{ width: '520px', maxWidth: '90vw' }"
    :footer="false"
    :mask-closable="true"
    unmount-on-close
    @cancel="handleCancel"
  >
    <div v-if="fetchingData" class="loading-container">
      <a-spin size="large" />
      <p>正在获取当前值...</p>
    </div>

    <a-form
      v-else
      class="set-form"
      :model="formData"
      layout="vertical"
      size="medium"
    >
      <a-form-item field="oid" label="目标 OID">
        <a-input
          v-model="formData.oid"
          placeholder="请输入目标 OID，例如：1.3.6.1.2.1.1.4.0"
          allow-clear
        />
      </a-form-item>

      <a-form-item field="type" label="数据类型">
        <a-select v-model="formData.type" placeholder="选择数据类型">
          <a-option
            v-for="option in typeOptions"
            :key="option.value"
            :value="option.value"
            :label="option.label"
          />
        </a-select>
      </a-form-item>

      <a-form-item field="value" label="设置的值">
        <a-textarea
          v-model="formData.value"
          placeholder="请输入要设置的值"
          :auto-size="{ minRows: 3, maxRows: 6 }"
          allow-clear
        />
      </a-form-item>

      <div class="type-hint">
        <a-alert type="info" :show-icon="false">
          <template #icon>
            <icon-info-circle />
          </template>
          <div class="hint-content">
            <p><strong>数据类型说明：</strong></p>
            <ul>
              <li><strong>i (INTEGER):</strong> 有符号整数类型</li>
              <li><strong>s (OCTET STRING):</strong> 字符串类型</li>
              <li><strong>x (HEX-STRING):</strong> 十六进制字符串</li>
              <li><strong>d (DECIMAL):</strong> 十进制字符串</li>
              <li><strong>a (IPADDRESS):</strong> IP 地址</li>
              <li><strong>o (OBJECTID):</strong> 对象标识符</li>
              <li><strong>t (TIMETICKS):</strong> 时间刻度</li>
              <li><strong>u (UNSIGNED32):</strong> 无符号32位整数</li>
              <li><strong>c (COUNTER32):</strong> 计数器32位</li>
              <li><strong>g (GAUGE32):</strong> 仪表32位</li>
            </ul>
          </div>
        </a-alert>
      </div>
    </a-form>

    <div class="set-modal-footer">
      <a-space>
        <a-button type="primary" :loading="loading" @click="handleConfirm">
          执行 Set
        </a-button>
        <a-button @click="handleCancel">取消</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<style scoped lang="scss">
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 16px;

  p {
    color: #94a3b8;
    font-size: 14px;
    margin: 0;
  }
}

.set-form {
  padding: 8px 0;
}

.type-hint {
  margin-top: 16px;
  margin-bottom: 8px;

  .hint-content {
    font-size: 13px;
    line-height: 1.6;

    p {
      margin-bottom: 8px;
    }

    ul {
      margin: 0;
      padding-left: 20px;
      list-style-type: disc;

      li {
        margin-bottom: 4px;
        color: #4b5563;

        strong {
          color: #1f2937;
        }
      }
    }
  }
}

.set-modal-footer {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
}
</style>
