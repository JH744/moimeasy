<template>
    <Toast position="top-right" group="t1" />
    <!-- 회비 사용 모달창 -->
    <Dialog :visible="visible" @update:visible="handleClose" modal :style="{ width: '30rem', height: '40rem' }"
        @show="resetValue">
        <template #header>
            <div class="text-header">지출 금액</div>
        </template>
        <div class="text-display">
            <InputText type="text" :value="`${value}원`" v-model="value" readonly class="text-input" />
        </div>
        <div class="number-buttons">
            <Button v-for="num in numbers" :key="num" rounded class="number-button" @click="handleButtonClick(num)">
                <template v-if="num === 'x'">
                    <i class="pi pi-times-circle"></i>
                </template>
                <template v-else>
                    {{ num }}
                </template>
            </Button>
        </div>
        <Button label="다음" rounded class="next-button" @click="expenseAmount" />
    </Dialog>

    <!-- 지출 항목 입력 모달창 -->
    <Dialog v-model:visible="visible2" modal :style="{ width: '30rem', height: '30rem' }">
        <template #header>
            <div class="sortButton">
                <SplitButton :model="sortOptions">
                    {{ selectedExpense }}
                </SplitButton>
            </div>
        </template>
        <div class="text-display">
            <InputText type="text" :value="`${value}원`" v-model="value" readonly class="text-input" />
        </div>
        <Button label="다음" rounded class="next-button" @click="selectOption" />
    </Dialog>

    <!-- 회비 사용 확인 모달 -->
    <Dialog v-model:visible="visible3" modal :style="{ width: '30rem', height: '30rem' }">
        <template #header>
            <div style="font-size: 1.2em; margin: auto;  margin-top: 30px; color: black;">
                <span style="font-weight: bold; color: blue;">{{ selectedExpense }}</span> 항목에
                <span style="font-weight: bold; color: blue;"> {{ value }}</span>원 사용하시겠습니까?
                <p>출금계좌 : {{ moeimBank }}({{ moeimAccount }})</p>
                <p>모임명 : {{ moeimName }}</p>
            </div>
        </template>
        <div class=" button-group">
            <Button label="확인" rounded class="next-button" @click="confirmExpense" />
            <Button label="취소" rounded class="next-button" @click="closeModal" />
        </div>
    </Dialog>

    <!-- 회비 사용 결과 모달 -->
    <Dialog v-model:visible="visible4" modal :style="{ width: '27rem', height: '359rem' }" :closable="false">
        <template #header>
            <div
                style="text-align: center; font-size: 3em; margin:auto; margin-top: 50px; font-weight: bold; color: #7f56d9;">
                <i class="pi pi-minus-circle" style="font-size: 8rem; margin-bottom: 20px;"></i>
                <div>모임비 지출</div>
            </div>
        </template>
        <div style="display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100%;">
            <p style="margin-top: 20px; margin-bottom: 20px;">
                <span style="font-weight: bold; color: blue;">{{ selectedExpense }}</span> 항목에
                <span style="font-weight: bold; color: blue;"> {{ value }}</span>원 사용하였습니다.
            </p>
            <Button label="닫기" rounded @click="dataReload" />
        </div>
    </Dialog>
</template>

<script setup>
import { ref, watch } from "vue";
import '@/views/transaction/style/transaction.style.css';
import '@/views/transaction/style/Modal.style.css';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import SplitButton from 'primevue/splitbutton';
import { defineProps, defineEmits } from 'vue';
import Toast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import axios from "axios";
import { useLoadingStore } from '@/stores/useLoadingStore';


const loadingStore = useLoadingStore();

// 부모로부터 visible 상태를 prop으로 받음
const props = defineProps({
    visible: {
        type: Boolean,
        required: true,
    },
    onFetch: {
        type: Function, // 부모로부터 전달된 메서드 타입 지정
        required: true,
    },
});

const emit = defineEmits(["update:visible"]);

function dataReload() {
    visible4.value = false;
    if (props.onFetch) {
        props.onFetch(); // 부모의 메서드 호출
    }
    emit('update:visible', false); // visible 상태를 false로 업데이트 (다이얼로그 닫기)
}


// Dialog 닫힐 때 실행
function handleClose() {
    emit('update:visible', false); // 부모 컴포넌트와 상태 동기화
}

const visible2 = ref(false); // 지출 항목 선택 모달
const visible3 = ref(false); // 지출 확인 모달
const visible4 = ref(false); // 결과 모달


// 지출 카테고리 데이터
const value = ref(""); // 입력한 금액
const moeimName = ref(""); // 모임 이름
const moeimBank = ref("카카오뱅크"); // 모임 은행 이름
const moeimAccount = ref(""); // 모임 계좌번호
const selectedExpense = ref("지출항목"); // 기본값 설정
const userName = ref("");
const toast = useToast();

const sortOptions = [
    { label: "식비", icon: "pi pi-apple", command: () => selectExpense("식비") },
    { label: "음료", icon: "pi pi-bolt", command: () => selectExpense("음료") },
    { label: "운영비", icon: "pi pi-shopping-cart", command: () => selectExpense("운영비") },
    { label: "시설대여", icon: "pi pi-briefcase", command: () => selectExpense("시설대여") },
    { label: "기타", icon: "pi pi-gift", command: () => selectExpense("기타") },
];

// 숫자 버튼 목록
const numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', 'x'];


// 버튼 클릭 시 호출
function handleButtonClick(num) {
    if (num === 'x') {
        value.value = value.value.slice(0, -1);
    } else {
        value.value += num;
    }
}

// 다음 버튼 클릭 -> 확인 모달 표시
function expenseAmount() {
    localStorage.setItem('value', JSON.stringify(value.value));
    if (value.value == "") {
        toast.add({
            severity: 'error',
            summary: '오류',
            detail: '송금할 금액을 입력하여 주시기 바랍니다.',
            group: 't1',
            life: 3000,
        });
    }
    else if (value.value.charAt(0) == 0 || value.value.charAt(0) == ".") {
        toast.add({
            severity: 'error',
            summary: '오류',
            detail: '첫 글자는 0이나 .이 될 수 없습니다.',
            group: 't1',
            life: 3000,
        });
    }
    else {
        handleClose();
        visible2.value = true;
    }
}


// 항목 선택
async function selectOption() {
    try {
        // Local Storage에서 accessToken, user 정보 가져오기
        const accessToken = localStorage.getItem('accessToken');
        const userRaw = localStorage.getItem('user');
        let userId = null;
        let moeimId = null;

        if (userRaw) {
            const user = JSON.parse(userRaw); // JSON 파싱
            userId = user?.userId || null; // 'userId' 키로 가져오기
            moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
        }

        if (!accessToken || !moeimId) {
            console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
            router.push('/login'); // 로그인 화면으로 리디렉션
            return;
        }

        localStorage.setItem('value', JSON.stringify(value.value));
        if (selectedExpense.value == "지출항목") {
            toast.add({
                severity: 'error',
                summary: '오류',
                detail: '항목을 선택하여 주시기 바랍니다.',
                group: 't1',
                life: 3000,
            });
        }
        else {
            const response = await axios.get(
                "/api/v1/transaction/details",
                {
                    headers: { Authorization: `Bearer ${accessToken}` },
                    params: { userId, moeimId },
                });
            if (response.data) {
                userName.value = response.data.userName;
                moeimAccount.value = response.data.moeimAccount;
                moeimName.value = response.data.moeimName;
            } else {
                console.error("모임 계좌 조회 불가");
            };
            visible2.value = false;
            visible3.value = true;
        }
    } catch (error) {
        console.error("API 요청 중 오류 발생:", error.response?.data || error.message);
    }
}

// 카테고리 선택
function selectExpense(option) {
    selectedExpense.value = option;
}

watch(value, (newVal) => {
    localStorage.setItem('value', newVal);
});

// 지출 확인 버튼 클릭 -> 지출 처리
async function confirmExpense() {
    try {
        const userRaw = localStorage.getItem('user');
        let userId = null;
        let moeimId = null;

        if (userRaw) {
            const user = JSON.parse(userRaw); // JSON 파싱
            userId = user?.userId || null; // 'userId' 키로 가져오기
            moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
        }

        const response = await axios.put(
            "/api/v1/transaction/withdraw",
            {
                userId: userId,
                accountNumber: moeimAccount.value,
                withdrawName: userName.value,
                amount: Number(value.value),
                categoryName: selectedExpense.value,
            },
            {
                headers: { Authorization: `Bearer ${accessToken}` },
            }
        );
        console.log("요청 데이터:", {
            userId: userId,
            amount: Number(value.value),
        });
        console.log("지출 성공:", response.data);
        visible3.value = false;
        visible4.value = true;

    } catch (error) {
        console.error("모임비 지출 중 오류 발생:", error.response?.data || error.message);
    }
}

// Reset 함수
function resetValue() {
    localStorage.removeItem('value');
    value.value = "";
    selectedExpense.value = "지출항목";
    visible3.value = false;
}

function closeModal() {
    toast.add({
        severity: 'warn',
        summary: '취소',
        detail: '작업이 취소되었습니다.',
        group: 't1',
        life: 3000,
    });
    visible3.value = false;
}

</script>

<style scoped>
.sortButton {
    margin-top: 10px;
    margin-bottom: 50px
}

:deep(.p-button.p-splitbutton-button) {
    background-color: #8b5cf6 !important;
    border: none;
}

:deep(.p-button.p-splitbutton-button:hover) {
    border: inherit !important;
    /* 테두리 크기 유지 */
    box-shadow: none !important;
    /* 그림자 제거 */
}

:deep(.p-button.p-splitbutton-dropdown) {
    background-color: #8b5cf6 !important;
    border: none;
    font-weight: bold;
}

:deep(.p-button.p-splitbutton-dropdown:hover) {
    background-color: #6326f3 !important;
}
</style>