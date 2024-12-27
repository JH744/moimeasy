<template>
  <div class="total-frame">
    <!-- <PayMenuList /> 메뉴 위치 -->
    <div class="title-bar">
      <div class="title">{{ currentMonth }}</div>
      <div class="accountBalance"> 잔액 : {{ accountBalance }}원</div>
      <div class="deposit"> 수입 : {{ totalDeposit }}</div>
      <div class="expense"> 지출 : {{ totalExpense }}</div>
      <div class="icon-button-group">
        <i class="pi pi-caret-left" style="font-size: 1rem" @click="updateMonth(-1)"></i>
        <i class="pi pi-calendar-times" style="font-size: 1rem"></i>
        <i class="pi pi-caret-right" style="font-size: 1rem" @click="updateMonth(1)"></i>
      </div>
    </div>
    <div class="card datatable-card">
      <template v-if="products.length">
        <DataTable :value="products" ref="dt" class="centered-datatable" tableStyle="min-width: 50rem">
          <template #header>
            <div class="export-button-container">
              <Button icon="pi pi-external-link" label="내보내기" iconPos="right" @click="exportCSV($event)"
                class="export-button" />
            </div>
          </template>
          <Column v-for="col of columns" :key="col.field" :field="col.field" :header="col.header"></Column>
        </DataTable>
      </template>
      <template v-else>
      </template>
    </div>
  </div>
</template>

<script setup>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import axios from "axios";
import Dialog from 'primevue/dialog';
import '@/views/transaction/style/transaction.style.css';
import '@/views/transaction/style/Modal.style.css';
import 'primeicons/primeicons.css';
import { ref, onMounted } from 'vue';
import { useLoadingStore } from '@/stores/useLoadingStore';

const loadingStore = useLoadingStore();
const currentMonth = ref('');
const dt = ref();
const products = ref([]); // 초기 값으로 빈 배열 설정
const currentDate = ref(new Date()); // 현재 날짜를 기준으로 동작
const accountBalance = ref(0);
const totalDeposit = ref(0);
const totalExpense = ref(0);
const visible = ref(false);


// 데이터가 없을 때 다이얼로그를 표시
if (!products.value.length) {
  visible.value = true;
}

// 다이얼로그 닫기 함수
const closeDialog = () => {
  visible.value = false;
};


const columns = [
  { field: 'tradedate', header: '날짜' },
  { field: 'tradelist', header: '거래내역' },
  { field: 'trademoney', header: '금액' },
  { field: 'tradetype', header: '구분' },
];

// 데이터 불러오기 함수
const fetchTransactions = async () => {
  loadingStore.startLoading();  // 로딩 시작
  try {
    const accessToken = localStorage.getItem('accessToken');
    const userRaw = localStorage.getItem('user');
    let moeimId = null;

    if (userRaw) {
      const user = JSON.parse(userRaw); // JSON 파싱
      moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
    }

    if (!accessToken || !moeimId) {
      console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
      router.push('/login'); // 로그인 화면으로 리디렉션
      return;
    }

    const startOfMonth = new Date(Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1));
    const endOfMonth = new Date(Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 0));

    const response = await axios.get('/api/v1/transaction/transaction-list', {
      headers: { Authorization: `Bearer ${accessToken}` },
      params: {
        moeimId: moeimId,
        startDate: startOfMonth.toISOString().split('T')[0],
        endDate: endOfMonth.toISOString().split('T')[0],
      },
    });

    if (response.data.transactionList) {
      const transactionList = response.data.transactionList;

      if (transactionList)
        accountBalance.value = transactionList[transactionList.length - 1].balance;

      totalDeposit.value = transactionList[transactionList.length - 1].monthDeposit;

      totalExpense.value = transactionList[transactionList.length - 1].monthExpense;


      products.value = response.data.transactionList.map(transaction => ({
        tradedate: transaction.transactedAt,
        tradelist: transaction.transactionTargetName,
        trademoney: transaction.amount,
        tradetype: transaction.type,
      }));
    } else {
      products.value = [];
      console.warn('응답 데이터에 transactionList가 없습니다.');
    }
  } catch (error) {
    console.error('거래내역을 가져오는 중 오류가 발생했습니다:', error);
    products.value = [];
  } finally {
    loadingStore.stopLoading(); // 로딩 중지
  }
};

// 월 변경 및 데이터 다시 가져오기
const updateMonth = (offset) => {
  currentDate.value.setMonth(currentDate.value.getMonth() + offset);
  currentMonth.value = `${currentDate.value.getMonth() + 1}월 거래내역`;
  fetchTransactions();
};

// 컴포넌트 초기화
onMounted(() => {
  const date = new Date();
  currentMonth.value = `${date.getMonth() + 1}월 거래내역`;
  fetchTransactions();
});

// CSV 내보내기
const exportCSV = () => {
  dt.value.exportCSV();
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');

.total-frame {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: flex-start;
  padding: 1rem;
  margin: 10px;
}

.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  width: 100%;
}

.icon-button-group {
  display: flex;
  align-items: center;
  gap: 20px;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 auto;
}

.datatable-card {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.datatable-aligned {
  width: 50rem;
  margin: 0 auto;
}

.export-button-container {
  display: flex;
  justify-content: flex-end;
  width: 100%;
  padding-right: 1rem;
}

:deep(.export-button) {
  background-color: #7f56d9 !important;
  color: #ffffff !important;
  padding: 0.5rem 1rem !important;
  border-radius: 20px !important;
  font-size: 0.9rem !important;
}

:deep(.export-button:hover) {
  background-color: #6c44c3 !important;
}

:deep(.export-button:active) {
  background-color: #5a37a7 !important;
}

:deep(.export-button .p-button-label) {
  font-weight: bold !important;
}

/* DataTable 전체에 폰트 적용 */
.custom-datatable .p-datatable {
  font-family: 'Noto Sans KR', sans-serif !important;
  font-size: 1.5rem !important;
  color: #333 !important;
}
</style>