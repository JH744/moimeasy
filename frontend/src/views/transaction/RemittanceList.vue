<template>
  <div class="total-frame">
    <PayMenuList /> <!-- 메뉴 위치 -->
    <div class="title-bar">
      <div class="title">{{ currentMonth }}</div>
      <div class="icon-button-group">
        <i class="pi pi-caret-left" style="font-size: 1rem" @click="updateMonth(-1)"></i>
        <i class="pi pi-calendar-times" style="font-size: 1rem"></i>
        <i class="pi pi-caret-right" style="font-size: 1rem" @click="updateMonth(1)"></i>
      </div>
      <Button label="납부하기" icon="pi pi-check" iconPos="right" rounded class="pay-button" @click="visible1 = true" />
    </div>
  </div>

  <!-- 금액 입력 모달창 -->
  <Dialog v-model:visible="visible1" modal :style="{ width: '30rem', height: '40rem' }" @hide="resetValue">
    <template #header>
      <div class="text-header">보낼 금액</div>
    </template>
    <div class="text-display">
      <InputText type="text" v-model="value" readonly class="text-input" />
    </div>
    <div class="number-buttons">
      <Button v-for="num in numbers" :key="num" rounded class="number-button" @click="handleButtonClick(num)">
        <template v-if="num === 'x'">
          <i class="pi pi-times-circle" style="font-size: 1.5rem"></i>
        </template>
        <template v-else>
          {{ num }}
        </template>
      </Button>
    </div>
    <Button label="다음" rounded class="next-button" @click="depositAmount" />
  </Dialog>

  <!-- 확인 모달 -->
  <Dialog v-model:visible="visible2" modal :style="{ width: '30rem', height: '25rem' }" @hide="resetValue">
    <template #header>
      <div style="text-align: center; font-size: 1.2em; margin-top: 30px; color: black;">
        <span style="font-weight: bold;">{{ moeimName }}</span> 님에게 {{ value }} 원 이체하시겠습니까?
        <p>출금계좌 : {{ userBank }}({{ username }}) {{ userAccount }}</p>
        <p>입금계좌 : {{ moeimBank }}({{ moeimName }}) {{ moeimAccount }}</p>
      </div>
    </template>
    <div class="button-group">
      <Button label="확인" rounded class="next-button" @click="confirmDeposit" />
      <Button label="취소" rounded class="next-button" @click="visible2 = false" />
    </div>
  </Dialog>

  <!-- 결과 모달 -->
  <Dialog v-model:visible="visible3" modal :style="{ width: '27rem', height: '40rem' }" @hide="resetValue"
    :closable="false">
    <template #header>
      <div
        style="display: flex; justify-content: center; align-items: center; font-size: 4em; margin-top: 50px; font-weight: bold; color: #7f56d9;">
        <i class="pi pi-check-circle" style="font-size: 8rem; margin-right: 20px;"></i>
        <div style="margin-top: 30px;">입금</div>
      </div>
    </template>

    <div style="display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100%;">
      <p>{{ username }} 님의 계좌에서 {{ value }} 원을 성공적으로 입금하였습니다.</p>
      <Button label="닫기" rounded @click="visible3 = false" />
    </div>
  </Dialog>

  <div class="card">
    <DataTable :value="members" paginator :rows="5" tableStyle="min-width: 60rem">
      <!-- 번호 -->
      <Column field="number" header="번호" style="width: 12%" />

      <!-- 사진 -->
      <Column header="사진" style="width: 12%">
        <template #body="slotProps">
          <img :src="slotProps.data.photo ? `/assets/${slotProps.data.photo}` : '/assets/default-photo.png'" alt="사진"
            style="width: 40px; height: 40px; border-radius: 50%" />
        </template>
      </Column>

      <!-- 이름 -->
      <Column field="userName" header="이름" style="width: 12%" />

      <!-- 금액 -->
      <Column field="amount" header="금액" style="width: 12%" />

      <!-- 납부일자 -->
      <Column field="transactionAt" header="납부일자" style="width: 12%" />

      <!-- 상태 -->
      <Column header="상태" style="width: 12%">
        <template #body="slotProps">
          <span :class="getStatusClass(slotProps.data.status)">
            {{ slotProps.data.status }}
          </span>
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import PayMenuList from './PayMenuList.vue';
import 'primeicons/primeicons.css';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import axios from "axios";
import { ref, onMounted } from "vue";

const visible1 = ref(false); // 금액 입력 모달
const visible2 = ref(false); // 확인 모달
const visible3 = ref(false); // 결과 모달

// 송금 데이터
const value = ref(""); // 입력한 금액
const moeimName = ref(""); // 모임 이름
const userBank = ref("신한"); // 사용자 은행 이름
const username = ref(""); // 사용자 이름
const userAccount = ref(""); // 사용자 계좌번호
const moeimBank = ref("토스"); // 모임 은행 이름
const moeimAccount = ref(""); // 모임 계좌번호


function showNextModal() {
  visible1.value = false;  // 첫 번째 모달 닫기
  visible2.value = true;   // 두 번째 모달 열기
}

function showNextModal3() {
  visible2.value = false;  // 두 번째 모달 닫기
  visible3.value = true;   // 세 번째 모달 열기
}

// 숫자 버튼 목록
const numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', 'x'];
const currentMonth = ref('');
const currentDate = ref(new Date()); // 현재 날짜를 기준으로 동작
const members = ref([]);
const columns = [
  { field: 'userName', header: '이름' },
  { field: 'amount', header: '금액' },
  { field: 'transactionAt', header: '납부일자' },
  { field: 'status', header: '상태' },
];
// Local Storage에서 accessToken 가져오기
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
}

// 현재 월의 첫 번째 날짜와 마지막 날짜 계산
const startOfMonth = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1);
const endOfMonth = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 0);

// 송금 내역 가져오기
async function fetchRemittanceList() {
  try {
    const response = await axios.get('/api/v1/transaction/remittance-list', {
      headers: { Authorization: accessToken.trim() },
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
        status: remittance.transactionType === 'REMITTANCE' ? '납부완료' : '회비미납', // 상태
        photo: remittance.photo || null, // 사진 (기본값 처리)
      }));
      console.log('송금 내역:', members.value);
    } else {
      members.value = [];
      console.warn('송금 내역이 없습니다.');
    }
  } catch (error) {
    console.error('송금 내역 조회 중 오류 발생:', error.response?.data || error.message);
    members.value = [];
  }
}

// 버튼 클릭 시 호출
function handleButtonClick(num) {
  if (num === 'x') {
    // 'x' 클릭 시 마지막 문자 삭제
    value.value = value.value.slice(0, -1);
  } else {
    // 숫자 또는 '.' 추가
    value.value += num;
  }
}

// 다음 버튼 클릭 -> 확인 모달 표시
function depositAmount() {
  visible1.value = false; // 금액 입력 모달 닫기
  visible2.value = true; // 확인 모달 열기
}

// 확인 버튼 클릭 -> 송금 처리
async function confirmDeposit() {
  console.log("확인 버튼 클릭"); // 호출 여부 확인
  try {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      console.error("Access token이 없습니다.");
      return;
    }

    const userRaw = localStorage.getItem("user");
    let userId = null;

    if (userRaw) {
      const user = JSON.parse(userRaw);
      userId = user?.userId || null;
    }

    if (!userId) {
      console.error("userId가 없습니다.");
      return;
    }

    const response = await axios.put(
      "/api/v1/transaction/remittance",
      {
        amount: Number(value.value),
      },
      {
        headers: {
          Authorization: accessToken.trim(),
        },
      }
    );
    console.log("요청 데이터:", {
      userId: userId,
      amount: Number(value.value),
    });

    console.log("송금 성공:", response.data); // 응답 확인
    visible2.value = false; // 확인 모달 닫기
    visible3.value = true; // 결과 모달 열기
  } catch (error) {
    console.error("송금 중 오류 발생:", error.response?.data || error.message);
  }
}


// 초기 데이터 로드
async function fetchInitialData() {

  // 서버에서 모임 데이터 가져오기
  const response = await axios.get("/api/v1/transaction/details", {
    headers: { Authorization: localStorage.getItem("accessToken") },
  });

  if (response.data) {
    moeimName.value = response.data.moeimName;
    moeimAccount.value = response.data.moeimAccount;
    username.value = response.data.userName;
    userAccount.value = response.data.userAccount;
  }
}

function resetValue() {
  value.value = ""; // 금액 초기화
}
// 상태에 따라 CSS 클래스를 반환하는 함수
function getStatusClass(status) {
  return status === '납부완료' ? 'text-green-500 font-bold' : 'text-red-500 font-bold';
}

// Mounted Hook에서 송금 내역 가져오기
onMounted(() => {
  const date = new Date();
  currentMonth.value = `${date.getMonth() + 1}월 회비 납부내역`; // 초기 월 설정
  fetchRemittanceList();
  fetchInitialData();
});

</script>

<style scoped>
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
  /* Flexbox 사용 */
  justify-content: space-between;
  /* 양쪽 정렬 */
  align-items: center;
  /* 세로 가운데 정렬 */
  margin-bottom: 1rem;
  /* 아래쪽 여백 */
  width: 100%;
  /* 부모의 전체 폭 사용 */
}

.icon-button-group {
  display: flex;
  /* 아이콘과 버튼을 가로로 배치 */
  align-items: center;
  /* 세로 가운데 정렬 */
  gap: 20px;
  /* 아이콘과 버튼 사이 여백 */
}

.number-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  /* 3개의 열로 배치 */
  gap: 10px;
  /* 버튼 사이의 간격 */
  margin-top: 20px;
}

.number-button {
  width: 70px;
  /* 버튼의 너비 */
  height: 70px;
  /* 버튼의 높이 */
  background-color: white;
  /* 버튼 배경색 */
  color: black;
  /* 버튼 텍스트 색상 */
  font-size: 1.2em;
  /* 텍스트 크기 */
  border: none;
  /* 테두리 제거 */
  border-radius: 10px;
  /* 둥근 모서리 */
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 20px;
}

.number-button:hover {
  background-color: #333333;
  /* 호버 시 색상 변경 */
}

.number-button:active {
  background-color: #555555;
  /* 클릭 시 색상 변경 */
}

.next-button {
  margin-top: 20px;
  width: 100%;
}

.card {
  width: 100%;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
}

.text-green-500 {
  color: #10b981;
}

.text-red-500 {
  color: #ef4444;
}

.text-header {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  /* 텍스트 가운데 정렬 */
  font-size: 2em;
  margin-top: 20px;
  margin-left: 150px;
  font-weight: bold;
  color: lightgrey;
}

.text-display {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 20;
  /* 위아래 간격 */
}

.text-input {
  font-size: 2rem;
  /* 텍스트 크기 */
  font-weight: bold;
  /* 텍스트 굵게 */
  text-align: center;
  /* 텍스트 가운데 정렬 */
  border: none;
  /* 테두리 제거 */
  outline: none;
  /* 포커스 시 외곽선 제거 */
  background: transparent;
  /* 배경 투명 */
  width: 100%;
  /* 너비를 부모에 맞춤 */
}

/* 버튼 스타일 */
:deep(.pay-button.p-button) {
  background-color: #7f56d9 !important;
  color: #ffffff !important;
  padding: 0.5rem 1rem !important;
  border-radius: 20px !important;
  font-size: 0.9rem !important;
  margin-right: 50px;
}

:deep(.pay-button.p-button:hover) {
  background-color: #6c44c3 !important;
}

:deep(.pay-button.p-button:active) {
  background-color: #5a37a7 !important;
}

/* 버튼 레이블 */
:deep(.pay-button .p-button-label) {
  font-weight: bold !important;
}
</style>
