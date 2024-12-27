import { defineStore } from 'pinia';
//헤더 브레드크럼 상태관리 스토어
export const usePathStore = defineStore('path ', {
  state: () => ({
    pathItems: [],
  }),
  actions: {
    updatePath(items) {
      this.pathItems = items;
    },
  },
});
