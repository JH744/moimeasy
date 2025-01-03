<template>
    <!-- 회비 납부-->
    <!-- 금액 입력 모달창 -->
    <Dialog :visible="visible" @update:visible="handleClose" modal :style="{ width: '30rem', height: '40rem' }"
        @show="resetValue">
        <template #header>
            <div class="text-header">보낼 금액</div>
        </template>
        <div class="text-display">
            <InputText type="text" :value="formattedValue + '원'" readonly class="text-input" />
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
        <Button label="다음" rounded class="next-button" @click="remittanceAmount" />
    </Dialog>

    <!-- 확인 모달 -->
    <Dialog v-model:visible="visible2" modal :style="{ width: '32rem', height: '20rem' }">
        <template #header>
            <div class="confirm-modal-text">
                <span>{{ moeimName }}</span> 님에게 <span>{{ formattedValue }}</span> 원 이체하시겠습니까?
                <div>
                    <p>출금계좌 : {{ userBank }}({{ userName }}) {{ userAccount }}</p>
                    <p>입금계좌 : {{ moeimBank }}({{ moeimName }}) {{ moeimAccount }}</p>
                </div>
            </div>
        </template>
        <div class="button-group">
            <Button label="취소" rounded class="next-button" @click="closeModal" />
            <Button label="확인" rounded class="next-button" @click="openCheckPassword" />
        </div>
    </Dialog>

    <!-- 비밀번호 확인 모달-->
    <checkPassword v-model:visible="checkPassword" @passwordVerified="handlePasswordVerified" />

    <!--확인 모달-->
    <ConfirmDialog />

    <!-- 결과 모달 -->
    <Dialog v-model:visible="visible3" modal :style="{ width: '27rem', height: '30rem' }" :closable="false">
        <template #header>
            <div class="modal-result">
                <i class="pi pi-check-circle" />
                <div>입금</div>
            </div>
        </template>
        <div
            style="display: flex; flex-direction: column; justify-content: center; align-items: center; height: 100%; margin:auto">
            <p style="margin-bottom: 50px;"><span style="font-weight: bold; color: blue;">{{ userName }}</span> 님의 계좌에서
                <span style="font-weight: bold; color: blue;"> {{ moeimName }} </span> 계좌에<br>
                <span style="font-weight: bold; color: blue;">{{ formattedValue }} </span>원을 입금하였습니다.
            </p>
            <Button label="닫기" rounded @click="visible3 = false" />
        </div>
    </Dialog>
</template>
<script setup>
import { ref, computed, watch } from "vue";
import '@/views/transaction/style/Transaction.style.css';
import '@/views/transaction/style/Modal.style.css';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import axios from "axios";
import { defineProps, defineEmits } from 'vue';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from "primevue/useconfirm";
import CheckPassword from "./CheckPassword.vue";


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

// 송금 데이터
const value = ref(""); // 입력한 금액
const moeimName = ref(""); // 모임 이름
const userBank = ref("신한"); // 사용자 은행 이름
const userName = ref(""); // 사용자 이름
const userAccount = ref(""); // 사용자 계좌번호
const moeimBank = ref("토스"); // 모임 은행 이름
const moeimAccount = ref(""); // 모임 계좌번호
const checkPassword = ref(false); // 비밀번호 입력력

const visible2 = ref(false); // 확인 모달
const visible3 = ref(false); // 결과 모달


// 숫자 버튼 목록
const numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', 'x'];

// 세 자리마다 콤마가 들어간 표시용 값
const formattedValue = computed(() => {
    if (!value.value) {
        return "";
    }
    const unformatted = value.value.replace(/,/g, "");
    const numericVal = parseFloat(unformatted);

    // 소수점만 입력 등으로 parseFloat가 NaN이면
    if (isNaN(numericVal)) {
        return value.value;
    }

    const parts = unformatted.split(".");
    let intPart = parts[0];
    const decimalPart = parts[1] || "";

    intPart = parseInt(intPart, 10).toLocaleString();
    return decimalPart.length > 0
        ? `${intPart}.${decimalPart}`
        : intPart;
});


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

// 다음 버튼 클릭 -> 확인 버튼으로 이동
// 유저 -> 계좌 입금을 위한 데이터
async function remittanceAmount() {
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
                moeimName.value = response.data.moeimName;
                moeimAccount.value = response.data.moeimAccount;
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

// 비밀번호가 일치하면 입금 처리
const handlePasswordVerified = (isValid) => {
    if (isValid) {
        confirmremittance();
    }
}


// 확인 버튼 클릭 -> 송금 처리
async function confirmremittance() {
    try {
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            console.error("Access token이 없습니다.");
            return;
        }

        const userRaw = localStorage.getItem("user");
        let userId = null;
        let moeimId = null;
        let userName = null; // 사용자 이름

        if (userRaw) {
            const user = JSON.parse(userRaw);
            userId = user?.userId || null;
            moeimId = user?.moeimId || null;
        }

        if (!userId) {
            console.error("userId가 없습니다.");
            return;
        }

        const response = await axios.put(
            "/api/v1/transaction/remittance",
            {
                userId: userId,
                moeimId: moeimId,
                amount: Number(value.value), // 요청 본문 데이터
            },
            {
                headers: { Authorization: `Bearer ${accessToken}` }, // 헤더는 세 번째 인자로 전달
            }
        );

        console.log("송금 성공:", response.data); // 응답 확인
        visible2.value = false; // 확인 모달 닫기
        visible3.value = true; // 결과 모달 열기
    } catch (error) {
        console.error("송금 중 오류 발생:", error.response?.data);
        confirm1(error.response?.data);
    }
}

const confirm1 = (message) => {
    confirm.require({
        message: message,
        header: 'Confirmation',
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
            label: '취소',
            severity: 'secondary',
            outlined: true
        },
        acceptProps: {
            label: '확인'
        },
        accept: () => {
            visible2.value = false;
        },
        reject: () => {
            toast.add({ severity: 'error', summary: '이체취소', detail: '이체가 취소되었습니다.', life: 3000 });
            visible2.value = false;
        }
    });
};

// Reset 함수
function resetValue() {
    value.value = "";
    localStorage.removeItem('value');
    visible2.value = false;
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

// 확인 버튼 클릭 시 호출되는 함수
function openCheckPassword() {
    console.log("openCheckPassword 호출됨");
    visible2.value = false;
    checkPassword.value = true;
    console.log("visible2:", visible2.value, "checkPassword:", checkPassword.value);
}
</script>

<style></style>