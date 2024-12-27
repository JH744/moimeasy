<template>
    <!-- 계좌 입금 -->
    <Toast position="top-right" group="t1" />

    <!-- 금액 입력 모달창 -->
    <Dialog :visible="visible" @update:visible="handleClose" modal :style="{ width: '30rem', height: '40rem' }"
        @show="resetValue">
        <template #header>
            <div class="text-header">입금 금액</div>
        </template>
        <div class="text-display">
            <InputText type="text" :value="`${value}원`" v-model="value" readonly class="text-input" />
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
    <Dialog v-model:visible="visible2" modal :style="{ width: '30rem', height: '30rem' }">
        <template #header>
            <div style="text-align: center; font-size: 1.2em; margin-top: 30px; color: black;">
                <span style="font-weight: bold; color:blue;">{{ userAccount }}</span> 계좌에
                <span style="font-weight: bold; color:blue;">{{ value }}</span>원 입금하시겠습니까?
                <p>입금계좌 : {{ userBank }}({{ userName }}) {{ userAccount }}</p>
            </div>
        </template>
        <div class="button-group">
            <Button label="확인" rounded class="next-button" @click="confirmDeposit" />
            <Button label="취소" rounded class="next-button" @click="closeModal" />
        </div>
    </Dialog>

    <!-- 결과 모달 -->
    <Dialog v-model:visible="visible3" modal :style="{ width: '30rem', height: '30rem' }" :closable="false">
        <template #header>
            <div
                style="display: flex; justify-content: center; align-items: center; font-size: 4em; margin-top: 50px; font-weight: bold; color: #7f56d9;">
                <i class="pi pi-check-circle" style="font-size: 8rem; margin-right: 20px;"></i>
                <div style="margin-top: 30px;">입금</div>
            </div>
        </template>

        <div style="display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100%;">
            <p style="margin-bottom: 50px;"><span style="font-weight: bold; color: blue;">{{ userName }}</span> 님의 <span
                    style="font-weight: bold; color: blue;">{{ userAccount }} </span> 계좌에<br>
                <span style="font-weight: bold; color: blue;">{{ value }} </span>원을 입금하였습니다.
            </p>
            <Button label="닫기" rounded @click="visible3 = false" />
        </div>
    </Dialog>
</template>

<script setup>
import { ref, watch } from "vue";
import '@/views/transaction/style/transaction.style.css';
import '@/views/transaction/style/Modal.style.css';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import Toast from 'primevue/toast';
import { defineProps, defineEmits } from 'vue';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from "primevue/useconfirm";
import axios from "axios";


// 부모로부터 visible 상태를 prop으로 받음
defineProps({
    visible: {
        type: Boolean,
        required: true,
    },
});

const emit = defineEmits(["update:visible"]);

// Dialog 닫힐 때 실행
function handleClose() {
    emit('update:visible', false); // 부모 컴포넌트와 상태 동기화
}

const confirm = useConfirm();
const toast = useToast();

// 모달 제어
const value = ref(""); // 입력한 금액
const visible2 = ref(false); // 확인 모달
const visible3 = ref(false); // 결과 모달

// 입금 데이터
const userBank = ref("신한"); // 사용자 은행 이름
const userName = ref(""); // 사용자 이름
const userAccount = ref(""); // 사용자 계좌번호

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
async function depositAmount() {
    // 현재 value 값을 depositValue에 할당
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
        try {
            const accessToken = localStorage.getItem('accessToken');
            const userRaw = localStorage.getItem('user');
            let userId = null;
            let moeimId = null;

            if (userRaw) {
                const user = JSON.parse(userRaw); // JSON 파싱
                userId = user?.userId || null; // 'userId' 키로 가져오기
                moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
            }

            localStorage.setItem('value', JSON.stringify(value.value));
            const response = await axios.get(
                "/api/v1/transaction/details",
                {
                    headers: { Authorization: `Bearer ${accessToken}` },
                    params: { userId, moeimId },
                });
            if (response.data) {
                userName.value = response.data.userName;
                userAccount.value = response.data.userAccount;
            } else {
                console.error("조회 불가");
            };
            handleClose()
            visible2.value = true;
        } catch (error) {
            console.error("API 요청 중 오류 발생:", error.response?.data || error.message);
        }
    }
}

// 확인 버튼 클릭 -> 입금 처리
async function confirmDeposit() {
    try {
        const accessToken = localStorage.getItem('accessToken');
        const userRaw = localStorage.getItem('user');
        let userId = null;
        let moeimId = null;

        if (userRaw) {
            const user = JSON.parse(userRaw); // JSON 파싱
            userId = user?.userId || null; // 'userId' 키로 가져오기
            moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
        }
        const response = await axios.put(
            "/api/v1/transaction/userDeposit",
            {
                userId: userId,
                amount: Number(value.value), // depositValue 대신 value 사용
            },
            {
                headers: { Authorization: `Bearer ${accessToken}` },
            }
        );
        console.log("요청 데이터:", {
            userId: userId,
            amount: Number(value.value),
        });

        console.log("입금 성공:", response.data); // 응답 확인
        visible2.value = false; // 확인 모달 닫기
        visible3.value = true; // 결과 모달 열기
    } catch (error) {
        console.error("입금 중 오류 발생:", error.response?.data || error.message);
    }
}

watch(value, (newVal) => {
    localStorage.setItem('value', newVal);
});

// Reset 함수
function resetValue() {
    value.value = "";
    localStorage.removeItem('value');
}

// Reset 함수
function closeModal() {
    toast.add({
        severity: 'warn',
        summary: '취소',
        detail: '작업이 취소되었습니다.',
        group: 't1',
        life: 3000,
    });
    visible2.value = false;
}

</script>

<style></style>