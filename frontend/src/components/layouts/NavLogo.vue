<template>
  <div class="nav-logo">
    <div class="logo-container" @click="onVisible">
      <div class="logo-background">
        <!-- vite-svg-loader 사용 -->
        <BlurIcon class="logo-icon" fill="#7f56d9" />
      </div>
    </div>
    <div class="logo-text-container">
      <div class="logo-text">MoeimEasy</div>
    </div>
  </div>
</template>

<script setup>
import { useUIStore } from '@/stores/uiStore';
import BlurIcon from '@/assets/icons/blurIcon.svg';
const uiStore = useUIStore();

/** 사이드바 visible 토글 이벤트 */
const onVisible = () => {
  uiStore.toggleSidebar();
};
</script>

<style>
.nav-logo {
  display: flex;
  flex-direction: row;
  gap: 16px;
  align-items: center;
  justify-content: flex-start;
  position: absolute;
  left: 24px;
  top: 24px;
}

.logo-container {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  position: static;
}

.logo-background {
  background: #ffffff;
  border-radius: 6px;
  /* border: 0.75px solid #e8e8e8; */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 42px;
  height: 42px;
  position: absolute;
  left: 0px;
  top: 0px;
  transition: all 0.2s;
}
.logo-background:hover {
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 15px rgba(139, 78, 189, 0.5); /* 블러 그림자 효과 */
}

.logo-icon {
  width: 24px;
  height: 24px;
  position: absolute;
  left: 9px;
  top: 9px;
  overflow: visible;
  cursor: pointer;
}

.logo-text-container {
  flex-shrink: 0;
  width: 116px;
  height: 24px;
  position: static;
}

.logo-text {
  color: var(--main-color);
  text-align: left;
  font-family: 'Poppins-Medium', sans-serif;
  font-size: 20px;
  line-height: 24px;
  font-weight: 500;
  position: absolute;
  left: 58px;
  top: 9px;
}
</style>
