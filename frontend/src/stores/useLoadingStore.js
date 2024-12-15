import { defineStore } from 'pinia';
import { ref } from 'vue';
// 로딩스피너 스토어
export const useLoadingStore = defineStore('loading', () => {
  const isLoading = ref(false);

  function toggleLoading() {
    isLoading.value = !isLoading.value;
  }

  function startLoading() {
    isLoading.value = true;
  }

  function stopLoading() {
    isLoading.value = false;
  }

  return {
    isLoading,
    toggleLoading,
    startLoading,
    stopLoading,
  };
});
