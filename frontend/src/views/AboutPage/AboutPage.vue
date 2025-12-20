<script setup>
const appInfo = {
  name: 'MagicMIB',
  version: '1.0.0',
  github: 'https://github.com/ZZHow1024/MagicMIB',
  license: 'GPL-3.0',
  changelog: [
    {
      version: '1.0.0',
      date: '2025-12-25',
      changes: ['初始版本发布', '实现了基本的 MIB 浏览器功能', '实现了基本的 SNMP 终端功能'],
    },
  ],
}
</script>

<template>
  <div id="about-page">
    <div class="about-container">
      <div class="app-icon">
        <img src="@/assets/MagicMIB.png" :alt="appInfo.name" />
      </div>
      <h1 class="app-name">{{ appInfo.name }}</h1>

      <div class="info-list">
        <div class="info-item">
          <span class="label">版本：</span>
          <span class="value">{{ appInfo.version }}</span>
        </div>
        <div class="info-item">
          <span class="label">开源协议：</span>
          <span class="value">{{ appInfo.license }}</span>
        </div>
        <div class="info-item">
          <span class="label">GitHub：</span>
          <a :href="appInfo.github" target="_blank" class="link">{{ appInfo.github }}</a>
        </div>
        <div class="changelog-container">
          <div class="changelog-header-title">
            <h3 class="section-title">更新日志</h3>
          </div>
          <div class="changelog-scrollable">
            <div class="changelog-list">
              <div v-for="log in appInfo.changelog" :key="log.version" class="changelog-item">
                <div class="changelog-header">
                  <span class="changelog-version">{{ log.version }}</span>
                  <span class="changelog-date">{{ log.date }}</span>
                </div>
                <ul class="changelog-changes">
                  <li v-for="(change, index) in log.changes" :key="index" class="changelog-change">
                    {{ change }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer">
      <a href="https://www.zzhow.com" target="_blank" class="website-link">ZZHow.com</a>
    </div>
  </div>
</template>

<style scoped lang="scss">
#about-page {
  padding: 40px 20px;
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.about-container {
  flex: 1;
  max-width: 500px;
  margin: 0 auto;
  text-align: center;
}

.app-icon {
  margin-bottom: 5px;

  img {
    width: 8vw;
    height: auto;
    object-fit: contain;
  }
}

.app-name {
  font-size: 1vw;
  font-weight: 600;
  margin-bottom: 40px;
  color: #1d2129;
}

.info-list {
  height: 30vh;
  text-align: left;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7ef;
  flex-shrink: 0;

  &:last-child {
    border-bottom: none;
  }
}

.label {
  font-weight: 500;
  color: #6b758b;
}

.value {
  color: #1d2129;
}

.link {
  color: #165dff;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
}

.footer {
  text-align: center;
  padding: 20px 0;
}

.website-link {
  color: #6b758b;
  text-decoration: none;
  font-size: 14px;

  &:hover {
    color: #165dff;
    text-decoration: underline;
  }
}

/* Changelog Styles */
.changelog-container {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.changelog-header-title {
  margin-bottom: 12px;
}

.changelog-scrollable {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 8px;
}

.changelog-scrollable::-webkit-scrollbar {
  width: 6px;
}

.changelog-scrollable::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.changelog-scrollable::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.changelog-scrollable::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.changelog-list {
  text-align: left;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 0;
  text-align: left;
}

.changelog-item {
  margin-bottom: 16px;
}

.changelog-item:last-child {
  margin-bottom: 0;
}

.changelog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid #e5e7ef;
}

.changelog-version {
  font-weight: 600;
  color: #1d2129;
  font-size: 14px;
}

.changelog-date {
  color: #6b758b;
  font-size: 12px;
}

.changelog-changes {
  margin: 0;
  padding-left: 16px;
}

.changelog-change {
  color: #4e5969;
  line-height: 1.5;
  margin-bottom: 6px;
  font-size: 13px;
}

.changelog-change:last-child {
  margin-bottom: 0;
}
</style>
