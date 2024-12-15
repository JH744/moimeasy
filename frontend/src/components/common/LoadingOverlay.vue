<template>
  <Loading
    :active="loadingStore.isLoading"
    :loader="spinner"
    background="rgba(255,255,255,0.8)"
    :color="spinnerColor"
    width="140"
    height="140"
  />
</template>

<script setup>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/css/index.css';
import { useLoadingStore } from '@/stores/useLoadingStore'; // 경로는 프로젝트 구조에 맞추어 변경

const spinner = 'dots'; // 커스텀 가능
const spinnerColor = 'var(--p-primary-500)';
const loadingStore = useLoadingStore();
</script>
