<template>
  <div class="total-frame">
    <div class="header">
      <div class="title-bar">
        <div class="title">{{ currentMonth }}</div>
      </div>
    </div>
    <div class="card datatable-card">
      <template v-if="products.length">
        <DataTable :value="products" removableSort virtualScroll scrollHeight="500px" stripedRows
          tableStyle="min-width: 50rem" ref="dt" class="centered-datatable">
          <template #header>
            <div class="icon-button-group">
              <i class="pi pi-caret-left icon-hover" @click="updateMonth(-1)"></i>
              <i class="pi pi-calendar-times"></i>
              <i class="pi pi-caret-right icon-hover" @click="updateMonth(1)"></i>
            </div>
            <div class="export-button-container">
              <div class="money">
                <span class="money-item">
                  <Tag class="custom-tag">잔액 : {{ accountBalance.toLocaleString() }} 원</Tag>
                </span>
                <span class="money-item">
                  <Tag class="custom-tag" style="color: #519de0; background-color: #519de024;">
                    수입 : {{ totalDeposit.toLocaleString() }} 원</Tag>
                </span>
                <span class="money-item">
                  <Tag class="custom-tag" style="color: #e051a9; background-color: #e051a920;">
                    지출 : {{ totalExpense.toLocaleString() }} 원</Tag>
                </span>
              </div>
              <Button icon="pi pi-external-link" label="내보내기" iconPos="right" @click="exportCSV($event)"
                class="export-button" />
            </div>
          </template>

          <!-- 날짜 컬럼 -->
          <Column field="tradedate" header="날짜" sortable style="width: 25%"></Column>

          <!-- 거래내역 컬럼 -->
          <Column field="tradelist" header="거래내역" sortable style="width: 25%"></Column>

          <!-- 금액 컬럼 -->
          <Column field="trademoney" header="금액" sortable style="width: 25%">
            <template #body="slotProps">
              <span :class="slotProps.data.tradetype === '출금' ? 'money-negative' : 'money-positive'">
                {{ slotProps.data.tradetype === '출금' ? '-' : '' }}
                {{ Number(slotProps.data.trademoney).toLocaleString() }}원
              </span>
            </template>
          </Column>

          <!-- 구분 컬럼 (Tag 컴포넌트 사용 예시) -->
          <Column field="tradetype" header="구분" sortable style="width: 25%">
            <template #body="slotProps">
              <Tag :value="slotProps.data.tradetype"
                :severity="slotProps.data.tradetype === '입금' ? 'success' : 'danger'" />
            </template>
          </Column>
        </DataTable>
      </template>
      <template v-else>
        데이터가 없습니다.
      </template>
    </div>
    <ConfirmDialog />
  </div>
</template>
<script setup>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import axios from "axios";
import '@/views/transaction/style/Transaction.style.css';
import '@/views/transaction/style/Button.style.css';
import '@/views/transaction/style/Modal.style.css';
import 'primeicons/primeicons.css';
import { ref, onMounted } from 'vue';
import { useLoadingStore } from '@/stores/useLoadingStore';
import { useRouter } from 'vue-router'; // router 사용을 위해 추가
import { useConfirm } from "primevue/useconfirm"; // confirm 사용을 위해 추가

const router = useRouter(); // router 사용을 위해 추가
const loadingStore = useLoadingStore();
const currentMonth = ref('');
const dt = ref();
const products = ref([]); // 초기 값으로 빈 배열 설정
const currentDate = ref(new Date()); // 현재 날짜를 기준으로 동작
const accountBalance = ref(0);
const totalDeposit = ref(0);
const totalExpense = ref(0);
const confirm = useConfirm(); // alert 창
const previousDate = ref(new Date()); // 이전 월
const isReverting = ref(false); // 되돌림 상태


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

      // 잔액, 수입, 지출
      const lastTransaction = transactionList[transactionList.length - 1];
      accountBalance.value = lastTransaction.balance;
      totalDeposit.value = lastTransaction.monthDeposit;
      totalExpense.value = lastTransaction.monthExpense;

      // 거래내역 데이터
      products.value = transactionList.map(transaction => ({
        tradedate: transaction.transactedAt,
        tradelist: transaction.transactionTargetName,
        trademoney: transaction.amount,
        tradetype: transaction.type,
      }));
    }

  } catch (error) {
    if (!isReverting.value) {
      isReverting.value = true;
      confirm1(error.response.data);
    }
    products.value = [];
    accountBalance.value = 0;
    totalDeposit.value = 0;
    totalExpense.value = 0;
  } finally {
    loadingStore.stopLoading(); // 로딩 중지
  }
};


// 월 변경 및 데이터 다시 가져오기
const updateMonth = (offset) => {
  if (!isReverting.value) {
    previousDate.value = new Date(currentDate.value); // 이전 날짜 저장
    currentDate.value.setMonth(currentDate.value.getMonth() + offset);
    const month = currentDate.value.getMonth() + 1;
    currentMonth.value = `${month}월 거래내역`;
    fetchTransactions();
  }
};

// 컴포넌트 초기화
onMounted(() => {
  const date = new Date();
  currentMonth.value = `${date.getMonth() + 1}월 거래내역`;
  fetchTransactions();
});

// CSV 내보내기
const exportCSV = () => {
  if (dt.value) {
    dt.value.exportCSV();
  }
};

const confirm1 = (message) => {
  confirm.require({
    message: message,
    header: 'Error',
    icon: 'pi pi-times',
    rejectProps: {
      label: '취소',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: '확인'
    },
    accept: () => {
      // 이전 날짜로 되돌리기
      currentDate.value = previousDate.value;
      const month = currentDate.value.getMonth() + 1;
      currentMonth.value = `${month}월 거래내역`;
      isReverting.value = false;
      fetchTransactions(); // 이전 월 데이터 다시 가져오기
    },
    reject: () => {
    }
  });
};
</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');

.total-frame {
  width: 95%;
  margin: 30px auto;
  /* 가운데 정렬 */
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  padding: 10px;
  justify-content: center;
  box-sizing: border-box;
}

.title-bar {
  flex-direction: column;
}

.icon-button-group {
  justify-content: flex-end !important;
  margin-bottom: 20px;
}

.money {
  display: grid;
  grid-template-columns: repeat(3, auto);
  gap: 1.5em;
  justify-content: center;
  align-items: center;
}

.datatable-card {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.export-button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
.centered-datatable .p-datatable {
  font-family: 'Noto Sans KR', sans-serif !important;
  font-size: 1.5rem !important;
  color: #333 !important;
}

/* 금액 컬럼 스타일 */
.money-positive {
  font-weight: bold;
  color: #519de0;
}

.custom-tag {
  /* 원하는 커스터마이징 스타일을 여기에 추가 */
  font-weight: bold;
  font-size: 1.1em;
}
</style>
