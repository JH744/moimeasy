<template>
  <div class="card">
    <Tabs value="/dashboard">
      <TabList>
        <Tab v-for="tab in items" :key="tab.label" :value="tab.route">
          <router-link v-if="tab.route" v-slot="{ href, navigate }" :to="tab.route" custom>
            <a :href="href" @click="navigate" class="tab-link">
              <i :class="tab.icon" />
              <span class="tab-label">{{ tab.label }}</span> <!-- 클래스 추가 -->
            </a>
          </router-link>
        </Tab>
      </TabList>
    </Tabs>
  </div>
</template>

<script setup>
import { ref } from "vue";
import Tabs from 'primevue/tabs';
import TabList from 'primevue/tablist';
import Tab from 'primevue/tab';
import 'primeicons/primeicons.css';

const items = ref([
  { route: '/remittance-list', label: '회비 납부내역', icon: 'pi pi-table' },
  { route: '/transaction-list', label: '거래내역', icon: 'pi pi-receipt' },
  { route: '/category', label: '카테고리별 소비 분석', icon: 'pi pi-chart-pie' },
]);
</script>

<style scoped>
.tab-link {
  display: flex;
  /* 아이콘과 텍스트를 가로로 배치 */
  align-items: center;
  /* 세로 가운데 정렬 */
  margin-left: 30px;
  gap: 10px;
  /* 아이콘과 텍스트 간격 */
}

:deep(.tab-label) {
  font-size: 1rem !important;
  /* 글자 크기 */
  font-weight: bold;
  /* 글자 굵게 */

}
</style>
