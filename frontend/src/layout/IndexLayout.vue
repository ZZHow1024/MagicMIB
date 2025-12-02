<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const menuItems = [
  { key: 'home', label: 'MIB浏览器', path: '/mib' },
  { key: 'terminal', label: 'SNMP终端', path: '/terminal' },
]

const activeKey = ref(menuItems[0].key)
const selectedKeys = computed(() => [activeKey.value])
const currentYear = new Date().getFullYear()

watch(
  () => route.path,
  (currentPath) => {
    const matched = menuItems.find((item) => item.path === currentPath)
    activeKey.value = matched ? matched.key : menuItems[0].key
  },
  { immediate: true },
)

const handleMenuSelect = (key) => {
  const target = menuItems.find((item) => item.key === key)
  if (target?.path && target.path !== route.path) {
    router.push(target.path)
  }
  activeKey.value = key
}
</script>

<template>
  <a-layout id="index-layout" class="layout-shell">
    <a-layout-header class="layout-header">
      <div class="brand">
        <div class="brand__logo">MagicMIB</div>
        <div class="brand__desc">Designed by ZZHow</div>
      </div>

      <a-menu mode="horizontal" :selected-keys="selectedKeys" @menu-item-click="handleMenuSelect">
        <a-menu-item v-for="item in menuItems" :key="item.key" :disabled="!item.path">
          {{ item.label }}
        </a-menu-item>
      </a-menu>

      <a-space>
        <a-button type="text">关于</a-button>
      </a-space>
    </a-layout-header>

    <a-layout-content class="layout-content">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </a-layout-content>

    <a-layout-footer class="layout-footer">
      © {{ currentYear }} MagicMIB · All Rights Reserved
    </a-layout-footer>
  </a-layout>
</template>

<style scoped lang="scss">
$bg-grey: #f6f7fb;
$border-grey: #e5e7ef;
$text-muted: #6b758b;

.layout-shell {
  min-height: 100vh;
  background: $bg-grey;
}

.layout-header {
  height: 72px;
  padding: 0 32px;
  background: #fff;
  border-bottom: 1px solid $border-grey;
  display: flex;
  align-items: center;
  gap: 24px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand__logo {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}

.brand__desc {
  font-size: 12px;
  color: $text-muted;
}

.layout-header .arco-menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

.layout-content {
  background: $bg-grey;
  display: flex;
  flex-direction: column;
}

.layout-footer {
  text-align: center;
  padding: 24px 16px;
  font-size: 12px;
  color: $text-muted;
  background: #fff;
  border-top: 1px solid $border-grey;
  margin-top: auto;
}
</style>
