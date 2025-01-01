<template>
  <div class="total-frame">
    <!-- 상단: 타이틀, 버튼 그룹, 아이콘 그룹 -->
    <div class="title-bar">
      <!-- 왼쪽: 현재 월 -->
      <div class="title">{{ currentMonth }}</div>
      <!-- 가운데: 아이콘 그룹 -->
      <div class="icon-button-group">
        <i class="pi pi-caret-left icon-hover" @click="updateMonth(-1)"></i>
        <i class="pi pi-calendar-times"></i>
        <i class="pi pi-caret-right icon-hover" @click="updateMonth(1)"></i>
      </div>
      <div class="button-group" style="gap: 1.2em;">
        <!-- 회비 납부 모달 -->
        <Button label="회비 납부" icon="pi pi-check" iconPos="right" rounded class="rimittance-button"
          @click="remittance = true" />
        <RemittanceModal v-model:visible="remittance" />

        <!-- 계좌 입금 모달-->
        <Button label="계좌 입금" icon="pi pi-dollar" iconPos="right" rounded class="deposit-button"
          @click="deposit = true" />
      </div>
      <UserDepositModal v-model:visible="deposit" />
    </div>
    <div class="card">
      <template v-if="members && members.length">
        <DataTable :value="members" paginator :rows="7" tableStyle="min-width: 60rem">
          <!-- 번호 -->
          <Column field="number" header="번호" style="width: 12%" />

          <Column header="사진" style="width: 12%">
            <template #body="slotProps">
              <img :src="slotProps.data.profileImage" alt="사진" style="width: 40px; height: 40px; border-radius: 50%;" />
            </template>
          </Column>

          <!-- 이름 -->
          <Column field="userName" header="이름" style="width: 12%" />

          <!-- 금액 -->
          <Column field="amount" header="금액" style="width: 12%; font-weight: bold;">
            <template #body="slotProps">
              {{ Number(slotProps.data.amount).toLocaleString() }}
            </template>
          </Column>
          <!-- 납부일자 -->
          <Column field="transactionAt" header="납부일자" style="width: 12%" />

          <!-- 상태 -->
          <Column header="상태" style="width: 12%">
            <template #body="slotProps">
              <Tag :severity="getSeverity(slotProps.data.status)" :value="slotProps.data.status" />
            </template>
          </Column>
        </DataTable>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import 'primeicons/primeicons.css';
import '@/views/transaction/style/Transaction.style.css';
import '@/views/transaction/style/Button.style.css';
import '@/views/transaction/style/Tag.style.css';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import axios from "axios";
import RemittanceModal from "./RemittanceModal.vue";
import UserDepositModal from "./UserDepositModal.vue";
import { useRouter } from 'vue-router';
import { useLoadingStore } from '@/stores/useLoadingStore';
import Tag from 'primevue/tag';
import { fetchImageUrl } from '@/utils/image-load-utils';
import { useConfirm } from "primevue/useconfirm"; // confirm 사용을 위해 추가
import { fetchAddNotification } from '@/utils/notification-add-utils'; // 알림 

const defaultProfileImage =
  'https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png';

const router = useRouter();
const loadingStore = useLoadingStore();
const currentMonth = ref('');
const currentDate = ref(new Date()); // 현재 날짜를 기준으로 동작
const members = ref([]);
const deposit = ref(false);
const remittance = ref(false);
const confirm = useConfirm(); // alert 창
const previousDate = ref(new Date()); // 이전 월
const isReverting = ref(false); // 되돌림 상태

// Local Storage에서 accessToken 가져오기
const accessToken = localStorage.getItem('accessToken');
const userRaw = localStorage.getItem('user');
let moeimId = null;
let userId = null;

if (userRaw) {
  const user = JSON.parse(userRaw); // JSON 파싱
  userId = user?.userId || null;
  moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
}

if (!accessToken || !moeimId) {
  console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
  router.push('/login'); // 로그인 화면으로 리디렉션
}

/**  파이어베이스 스토리지의 url로 변환*/
const changeImageUrl = async (imageUrl) => {
  return await fetchImageUrl(imageUrl);
};



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
  loadingStore.startLoading();  // 로딩 시작
  try {
    // 현재 월의 첫 번째 날짜과 마지막 날짜 계산
    const startOfMonth = new Date(Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1));
    const endOfMonth = new Date(Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 0));

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
      members.value = await Promise.all(
        response.data.remittanceList.map(async (remittance, index) => {
          let profileImage = null;
          // profileImage가 유효한 문자열인지 확인
          if (
            remittance.profileImage &&
            typeof remittance.profileImage === 'string' &&
            remittance.profileImage.trim() !== ''
          ) {
            try {
              // 파이어베이스 스토리지용 URL 변환
              profileImage = await changeImageUrl(remittance.profileImage);
            } catch (error) {
              console.error('이미지 변환 실패:', error);
              profileImage = defaultProfileImage;
            }
          } else {
            // 유효하지 않으면 기본 이미지로 설정
            profileImage = defaultProfileImage;
          }
          return {
            number: index + 1, // 번호 추가
            userName: remittance.userName, // 이름
            amount: remittance.amount, // 금액
            transactionAt: remittance.transactionAt, // 납부일자
            status: remittance.transactionType === 'REMITTANCE' ? '납부완료' : '회비미납', // 상태
            profileImage
          };
        })
      );
    } else {
      members.value = [];
    }
  } catch (error) {
    if (!isReverting.value) {
      isReverting.value = true;
      confirm1(error.response.data);
    }
    members.value = [];
  }
  finally {
    loadingStore.stopLoading(); // 로딩 중지
  }
}


// 월 변경 및 데이터 다시 가져오기
const updateMonth = (offset) => {
  if (!isReverting.value) {
    previousDate.value = new Date(currentDate.value); // 이전 날짜 저장
    currentDate.value.setMonth(currentDate.value.getMonth() + offset);
    const month = currentDate.value.getMonth() + 1;
    currentMonth.value = `${month}월 회비 납부내역`;
    fetchRemittanceList(); // 함수 호출
  };
}

// Mounted Hook에서 송금 내역 가져오기 및 초기 데이터 로드
onMounted(() => {
  const date = new Date();
  currentMonth.value = `${date.getMonth() + 1}월 회비 납부내역`; // 초기 월 설정
  fetchRemittanceList();
});

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
      fetchRemittanceList(); // 이전 월 데이터로 돌아가기
    },
    reject: () => {
    }
  });
};

</script>

<style scoped>
:deep(.p-datatable-tbody>tr>td) {
  vertical-align: middle;
}
</style>
