<template>
  <div
    class="speed-dial-box"
    :class="{ 'slide-in': !uiStore.visible }"
    v-show="!uiStore.visible"
  >
    <SpeedDial
      :model="items"
      :radius="80"
      type="semi-circle"
      direction="right"
      buttonClass="btn-color"
      style="position: absolute; top: calc(50% - 2rem); left: 10px"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import SpeedDial from 'primevue/speeddial';
import { useUIStore } from '@/stores/uiStore';
import useSpeedDialItems from '@/data/speedDialItems';
import { useRouter } from 'vue-router';
const router = useRouter();

const uiStore = useUIStore();
// SpeedDial Items 가져오기
const items = ref(useSpeedDialItems(router));
</script>

<style>
/* Speed Dial 버튼 커스텀 스타일적용  */
.btn-color {
  background: var(--main-color) !important;
  border: none !important;
}

/*스피드 다이얼 애니메이션 추가 */
@keyframes slideInFromLeft {
  0% {
    opacity: 0;
    transform: translateX(-200%) translateY(-50%) scale(0.5) rotate(-30deg);
  }
  60% {
    opacity: 0.7;
    transform: translateX(15%) translateY(-50%) scale(1.2) rotate(10deg);
  }
  80% {
    opacity: 0.9;
    transform: translateX(-5%) translateY(-50%) scale(0.95) rotate(-5deg);
  }
  100% {
    opacity: 1;
    transform: translateX(0) translateY(-50%) scale(1) rotate(0deg);
  }
}

.slide-in {
  animation: slideInFromLeft 0.7s cubic-bezier(0.215, 0.61, 0.355, 1) forwards;
}

.speed-dial-box {
  position: fixed;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1000;
  will-change: transform, opacity;

  .p-button-secondary {
    background-color: #f2eefb !important;
    color: #7f56d9;
    transition: all 0.3s;
  }
  .p-button-secondary:hover {
    background-color: var(--p-violet-400) !important;
    color: #ffffff !important;
    border: 1px solid var(--p-violet-400) !important;
    transform: scale(1.5);
  }
}
</style>
