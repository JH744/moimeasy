import { defineStore } from 'pinia';
//사이드바 상태관리 스토어 (ui용도임)
export const useUIStore = defineStore('ui', {
  state: () => ({
    visible: false, // 사이드바 열림/닫힘 상태
  }),
  actions: {
    toggleSidebar() {
      this.visible = !this.visible; // 상태 토글
    },
  },
});
