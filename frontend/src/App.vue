<template>
  <div class="app-layout">
    <!-- 로딩 스피너 -->
    <LoadingOverlay />
    <!-- 사이드바 -->
    <SideBar
      v-if="userData && !hideLayout"
      class="sidebar"
      :class="{ expanded: uiStore.visible }"
    />
    <!-- 스피드다이얼 영역-->
    <SideSpeedDial v-if="userData && !hideLayout" />
    <!-- 메인 영역 -->
    <div class="contents-wrapper" ref="contentsWrapperRef">
      <!-- 헤더 -->
      <Header v-if="userData && !hideLayout" class="header" />
      <!-- 메인 컨텐츠 -->
      <router-view />
      <!-- 스크롤 탑 버튼 -->
      <div v-if="isButtonVisible" class="arrow-button" @click="scrollToTop">
        <i class="pi pi-angle-up" style="font-size: 1.5rem"></i>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUIStore } from '@/stores/uiStore';
import { ref, watch, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();
const userData = ref('');
const uiStore = useUIStore();
const isButtonVisible = ref(false);
const hideLayout = computed(() => route.meta.hideLayout);
const contentsWrapperRef = ref(null); // 콘텐츠 WRAPPER 참조
const scrollTopPosition = ref(0); // 스크롤 위치 추적

// 스크롤을 최상단으로 이동
const scrollToTop = () => {
  if (contentsWrapperRef.value) {
    contentsWrapperRef.value.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

watch(
  () => route.matched,
  () => {
    const userDataStorage = localStorage.getItem('user');
    userData.value = userDataStorage ? JSON.parse(userDataStorage) : null;
  },
  { immediate: true } // 컴포넌트가 처음 마운트될 때도 실행
);
// 스크롤 위치 감시
watch(scrollTopPosition, (newVal) => {
  isButtonVisible.value = newVal > 1000; // 스크롤 위치가 1000 이상일 때 버튼 표시
});

onMounted(() => {
  // 스크롤 탑 이벤트 리스너 적용
  if (contentsWrapperRef.value) {
    contentsWrapperRef.value.addEventListener('scroll', () => {
      scrollTopPosition.value = contentsWrapperRef.value.scrollTop;
    });
  }
});
</script>

<style>
.app-layout {
  display: flex;
}
/* 사이드바 설정 */
.sidebar {
  flex: 0; /* 사이드바영역 전체비율 설정 -> 디폴트 : 접힌 상태 */
  background-color: #333;
  color: white;
  transition: all 0.3s ease-in-out;
}

.sidebar.expanded {
  flex: 0.58; /* 펼쳐진 상태 */
}

@media (min-width: 1490px) {
  /* md 브레이크포인트: 1490px 이상 */
  .sidebar.expanded {
    flex: 0.52; /* 펼쳐진 상태 */
  }
}
@media (min-width: 1600px) {
  /* md 브레이크포인트: 1600px 이상 */
  .sidebar.expanded {
    flex: 0.42; /* 펼쳐진 상태 */
  }
}

.contents-wrapper {
  flex: 3;
  background-color: #f8f9fa;
  overflow: auto;
  height: 100vh;
}

/* 스크롤 탑 버튼 스타일 */
.arrow-button {
  position: fixed;
  right: 30px;
  bottom: 30px;
  cursor: pointer;
  z-index: 1000;
  background-color: var(--p-primary-500);
  color: #ffffff;
  padding: 5px;
  border-radius: 50%;
  animation: bounce 1s infinite;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s ease-in-out;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}
</style>
