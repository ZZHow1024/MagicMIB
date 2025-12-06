<script setup>
const appInfo = {
  name: 'MagicMIB',
  version: '1.0.0',
  github: 'https://github.com/ZZHow1024/MagicMIB',
  license: 'GPL-3.0',
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
  margin-bottom: 20px;

  img {
    width: 128px;
    height: 128px;
    object-fit: contain;
  }
}

.app-name {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 40px;
  color: #1d2129;
}

.info-list {
  text-align: left;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7ef;

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
</style>
