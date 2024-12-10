<template>
  <div class="app-layout">
    <!-- 사이드바 -->
    <SideBar class="sidebar" :class="{ expanded: uiStore.visible }" />
    <!-- 스피드다이얼 영역-->
    <SideSpeedDial />
    <!-- 메인 컨텐츠 -->
    <div class="contents-wrapper">
      <Header class="header" />
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useUIStore } from '@/stores/uiStore';
const uiStore = useUIStore();
</script>

<style>
.app-layout {
  display: flex;
}
.sidebar {
  flex: 0; /* 사이드바영역 전체비율 설정 -> 디폴트 : 접힌 상태 */
  background-color: #333;
  color: white;
  transition: all 0.3s ease-in-out;
}

.sidebar.expanded {
  flex: 0.58; /* 펼쳐진 상태 */
}

.contents-wrapper {
  flex: 3;
  background-color: #f8f9fa;
  overflow: auto;
}

.hidden {
  display: none; /* 애니메이션 종료 후 숨기기 */
}
</style>
