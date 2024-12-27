<template>
  <div class="total-frame">
    <!-- 상단: 타이틀, 버튼 그룹, 아이콘 그룹 -->
    <div class="title-bar">
      <!-- 왼쪽: 현재 월 -->
      <div class="title">{{ currentMonth }}</div>
      <!-- 가운데: 아이콘 그룹 -->
      <div class="icon-button-group">
        <i
          class="pi pi-caret-left"
          style="font-size: 1rem"
          @click="updateMonth(-1)"
        ></i>
        <i class="pi pi-calendar-times" style="font-size: 1rem"></i>
        <i
          class="pi pi-caret-right"
          style="font-size: 1rem"
          @click="updateMonth(1)"
        ></i>
      </div>
      <!-- 회비 납부 모달 -->
      <Button
        label="회비 납부"
        icon="pi pi-check"
        iconPos="right"
        rounded
        class="rimittance-button"
        @click="remittance = true"
      />
      <RemittanceModal v-model:visible="remittance" />

      <!-- 계좌 입금 모달-->
      <Button
        label="계좌 입금"
        icon="pi pi-paypal"
        iconPos="right"
        rounded
        class="deposit-button"
        @click="deposit = true"
      />
      <UserDepositModal v-model:visible="deposit" />
    </div>
  </div>
  <div class="card">
    <template v-if="members && members.length">
      <DataTable
        :value="members"
        paginator
        :rows="5"
        tableStyle="min-width: 60rem"
      >
        <!-- 번호 -->
        <Column field="number" header="번호" style="width: 12%" />

        <!-- <Column header="회원사진" style="width: 12%">
          <template #body="slotProps">
            <img :src="slotProps.data.photo" alt="사진" style="width: 40px; height: 40px; border-radius: 50%;" />
          </template>
</Column> -->

        <!-- 이름 -->
        <Column field="userName" header="이름" style="width: 12%" />

        <!-- 금액 -->
        <Column field="amount" header="금액" style="width: 12%" />

        <!-- 납부일자 -->
        <Column field="transactionAt" header="납부일자" style="width: 12%" />

        <!-- 상태 -->
        <Column header="상태" style="width: 12%">
          <template #body="slotProps">
            <Tag
              :severity="getSeverity(slotProps.data.status)"
              :value="slotProps.data.status"
            />
          </template>
        </Column>
      </DataTable>
    </template>
  </div>
  1
</template>

<script setup>
import { ref, onMounted } from 'vue';
import 'primeicons/primeicons.css';
import '@/views/transaction/style/Transaction.style.css';
// import '@/views/transaction/style/Button.style.css';
import '@/views/transaction/style/Tag.style.css';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import axios from 'axios';
import RemittanceModal from './RemittanceModal.vue';
import UserDepositModal from './UserDepositModal.vue';
import { useRouter } from 'vue-router';
import { useLoadingStore } from '@/stores/useLoadingStore';
import Tag from 'primevue/tag';

const router = useRouter();
const loadingStore = useLoadingStore();

const currentMonth = ref('');
const currentDate = ref(new Date()); // 현재 날짜를 기준으로 동작
const members = ref([]);

const deposit = ref(false);
const remittance = ref(false);

// Local Storage에서 accessToken 가져오기
const accessToken = localStorage.getItem('accessToken');
const userRaw = localStorage.getItem('user');
let moeimId = null;
let userId = null;
let photo = null;

if (userRaw) {
  const user = JSON.parse(userRaw); // JSON 파싱
  userId = user?.userId || null;
  moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
}

if (!accessToken || !moeimId) {
  console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
  router.push('/login'); // 로그인 화면으로 리디렉션
}

// 상태에 따른 severity 결정
const getSeverity = (status) => {
  switch (status) {
    case '납부완료':
      return 'success';
    default:
      return 'danger';
  }
};

// 송금 내역 가져오기
async function fetchRemittanceList() {
  try {
    loadingStore.startLoading(); // 로딩 시작
    // 현재 월의 첫 번째 날짜과 마지막 날짜 계산
    const startOfMonth = new Date(
      Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1)
    );
    const endOfMonth = new Date(
      Date.UTC(
        currentDate.value.getFullYear(),
        currentDate.value.getMonth() + 1,
        0
      )
    );

    const response = await axios.get('/api/v1/transaction/remittance-list', {
      headers: { Authorization: `Bearer ${accessToken}` },
      params: {
        moeimId: moeimId,
        startDate: startOfMonth.toISOString().split('T')[0],
        endDate: endOfMonth.toISOString().split('T')[0],
      },
    });

    // 응답 데이터 처리
    if (response.data.remittanceList) {
      members.value = response.data.remittanceList.map((remittance, index) => ({
        number: index + 1, // 번호 추가
        userName: remittance.userName, // 이름
        amount: remittance.amount, // 금액
        transactionAt: remittance.transactionAt, // 납부일자
        status:
          remittance.transactionType === 'REMITTANCE' ? '납부완료' : '회비미납', // 상태
        photo: remittance.photo || null, // 사진 (기본값 처리)
      }));
      console.log('송금 내역:', members.value);
    } else {
      members.value = [];
      console.warn('송금 내역이 없습니다.');
    }
  } catch (error) {
    console.error(
      '송금 내역 조회 중 오류 발생:',
      error.response?.data || error.message
    );
    members.value = [];
  } finally {
    loadingStore.stopLoading(); // 로딩 중지
  }
}

// 월 변경 및 데이터 다시 가져오기
const updateMonth = (offset) => {
  currentDate.value.setMonth(currentDate.value.getMonth() + offset);
  currentMonth.value = `${currentDate.value.getMonth() + 1}월 회비 납부내역`;
  fetchRemittanceList(); // 함수 호출
};

// Mounted Hook에서 송금 내역 가져오기 및 초기 데이터 로드
onMounted(() => {
  const date = new Date();
  currentMonth.value = `${date.getMonth() + 1}월 회비 납부내역`; // 초기 월 설정
  fetchRemittanceList();
});
</script>

<style scoped></style>
