<template>
    <div class="frame flex justify-center gap-4">
      <Button
        v-for="menu in menus"
        :key="menu.label"
        :as="'router-link'"
        :label="menu.label"
        :to="menu.path"
        class="p-button-rounded p-button-outlined custom-button"
      />
    </div>
  </template>
  

  <script setup>
  const menus = [
    { label: '거래내역', path: '/transactions' },
    { label: '카테고리 별 소비 분석', path: '/category-analysis' },
    { label: '회비 납부내역', path: '/dues' },
  ];
  </script>

<style scoped>
.frame {
  display: flex;
  flex-direction: row;
  gap: 30px;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.custom-button {
  font-size: 18px;
  font-family: "Poppins-Regular", sans-serif;
  font-weight: 400;
  letter-spacing: 0.04em;
  line-height: 1.5rem;
  padding: 0.5rem 1rem;
  background-color: rgba(152, 134, 226, 0.2);
  color: #000;
  border-radius: 30px;
}

.custom-button:hover {
  background-color: rgba(152, 134, 226, 0.4);
  color: #333;
}
</style>