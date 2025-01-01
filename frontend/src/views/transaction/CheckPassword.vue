<template>
    <!-- 비밀번호 검증 모달 -->
    <Dialog :visible="visible" @update:visible="handleClose" modal :style="{ width: '25rem', height: '25rem' }">
        <template #header>
            <div class="password-text">
                <p>비밀번호를 입력해주세요</p>
            </div>
        </template>
        <div class="card flex justify-center">
            <Password v-model="password" :feedback="false" />
        </div>
        <div class="button-group">
            <Button label="확인" rounded class="next-button" @click="CheckPassword" />
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
import Password from 'primevue/password';

const password = ref("");
const confirm = useConfirm();
const toast = useToast();

// 부모로부터 visible 상태를 prop으로 받음
const props = defineProps({
    visible: {
        type: Boolean,
        required: true,
    },
});

const emit = defineEmits(["update:visible", "passwordVerified"]);

// visible prop을 감시하여 모달이 닫힐 때 비밀번호 초기화
watch(() => props.visible, (newVal) => {
    if (!newVal) {
        password.value = ""; // 비밀번호 초기화
    }
});

// Dialog 닫힐 때 실행
function handleClose() {
    emit('update:visible', false); // 부모 컴포넌트와 상태 동기화
}

async function CheckPassword() {
    if (password.value == "") {
        toast.add({
            severity: 'error',
            summary: '오류',
            detail: '비밀번호는 공백이 될 수 없습니다.',
            group: 't1',
            life: 3000,
        });
    } else {
        try {
            const accessToken = localStorage.getItem('accessToken');
            const userRaw = localStorage.getItem('user');
            let userId = null;

            if (userRaw) {
                const user = JSON.parse(userRaw); // JSON 파싱
                userId = user?.userId || null; // 'userId' 키로 가져오기
            }
            const response = await axios.post( // Post 전송 시 (URL, Data, Header 순으로 전송해야 한다.)
                "/api/v1/transaction/password",
                {
                    userId: userId,
                    password: password.value
                },
                {
                    headers: { Authorization: `Bearer ${accessToken}` },
                },
            );
            console.log(response.data);
            if (response.data.valid === false) {
                toast.add({
                    severity: 'error',
                    summary: '인증 실패',
                    detail: `${response.data.message} 남은 시도 횟수: ${response.data.remainingAttempts}번`,
                    group: 't1',
                    life: 3000,
                });
            }
            else if (response.data.valid === true) {
                emit("passwordVerified", true); // 부모에게 검증 성공 전달
                emit("update:visible", false) // 모달 닫기
                toast.add({
                    severity: 'success',
                    summary: '성공',
                    detail: response.data.message,
                    group: 't1',
                    life: 3000,
                });
            }
        } catch (error) {
            console.error("비밀번호 검증 중 오류 발생:", error.response?.data || error.message);
            confirm1(error.response.data);
            emit("passwordVerified", false);
        }
    }
}

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
            emit("update:visible", false);
        },
    });
};
</script>

<style scoped>
.password-text {
    text-align: center;
    font-size: 1.2em;
    margin-top: 30px;
    color: black;
}
</style>