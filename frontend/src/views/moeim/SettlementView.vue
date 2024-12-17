<template>
  <div class="settlement-container">
    <!-- 헤더 -->
    <div class="header">
      <h2>정산 요청 목록</h2>
      <Button
        label="정산 요청"
        icon="pi pi-plus"
        class="p-button-rounded p-button-primary"
        @click="openModal"
      />
    </div>

    <!-- 정산 요청 목록 -->
    <DataTable
      :value="settlementRequests"
      class="p-datatable-striped"
      responsiveLayout="scroll"
    >
      <Column field="title" header="제목">
        <template #body="slotProps">
          <span class="clickable-text" @click="openDetailModal(slotProps.data)">
            {{ slotProps.data.title }}
          </span>
        </template>
      </Column>
      <Column field="userName" header="작성자"></Column>
      <Column field="amount" header="금액"></Column>
      <Column field="date" header="날짜"></Column>
      <Column header="영수증">
        <template #body="slotProps">
          <img
            :src="slotProps.data.imageUrl"
            alt="영수증"
            class="receipt-image clickable-image"
            @click="openDetailModal(slotProps.data)"
          />
        </template>
      </Column>
    </DataTable>

    <!-- 정산 요청 모달 -->
    <Dialog
      header="정산 요청하기"
      v-model:visible="isModalVisible"
      :style="{ width: '30vw' }"
      modal
    >
      <div class="form">
        <div class="p-field">
          <label for="title">제목</label>
          <InputText
            id="title"
            v-model="title"
            placeholder="제목을 입력하세요"
            class="p-inputtext"
          />
        </div>
        <div class="p-field">
          <label for="amount">요청 금액</label>
          <InputText
            id="amount"
            v-model="amount"
            placeholder="금액을 입력하세요"
            class="p-inputtext"
          />
        </div>
        <div class="p-field">
          <label for="file">영수증 첨부</label>
          <FileUpload
            mode="basic"
            name="file"
            accept="image/*"
            customUpload
            @uploader="onFileUpload"
            chooseLabel="파일 선택"
          />
        </div>
      </div>
      <template #footer>
        <Button
          label="취소"
          icon="pi pi-times"
          class="p-button-text"
          @click="closeModal"
        />
        <Button
          label="요청 제출"
          icon="pi pi-check"
          class="p-button-primary"
          @click="submitRequest"
          :disabled="!imageUrl"
        />
      </template>
    </Dialog>

    <!-- 세부 정보 모달 -->
    <Dialog
      header="정산 요청 상세"
      v-model:visible="isDetailModalVisible"
      :style="{ width: '40vw' }"
      modal
    >
      <div class="detail-view">
        <h3>{{ selectedRequest.title }}</h3>
        <p><strong>작성자:</strong> {{ selectedRequest.userName }}</p>
        <p><strong>날짜:</strong> {{ selectedRequest.date }}</p>
        <img
          :src="selectedRequest.imageUrl"
          alt="영수증"
          class="receipt-image-large"
        />
      </div>
      <template #footer>
        <Button
          label="닫기"
          icon="pi pi-times"
          class="p-button-text"
          @click="closeDetailModal"
        />
      </template>
    </Dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { firebaseStorage } from '@/firebase/firebaseConfig';
import {
  ref as firebaseRef,
  uploadBytes,
  getDownloadURL,
} from 'firebase/storage';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import FileUpload from 'primevue/fileupload';

export default {
  components: {
    DataTable,
    Column,
    Dialog,
    Button,
    InputText,
    FileUpload,
  },
  setup() {
    const settlementRequests = ref([]);
    const isModalVisible = ref(false);
    const isDetailModalVisible = ref(false);
    const selectedRequest = ref({});
    const title = ref('');
    const amount = ref('');
    const imageUrl = ref('');
    const uploadProgress = ref(0);
    const authStore = useAuthStore();

    // 모달 열기
    const openModal = () => {
      isModalVisible.value = true;
    };

    // 모달 닫기
    const closeModal = () => {
      isModalVisible.value = false;
      title.value = '';
      amount.value = '';
      imageUrl.value = '';
      uploadProgress.value = 0;
    };

    // 파일 업로드
    const onFileUpload = async (event) => {
      const file = event.files[0];
      if (!file) {
        console.warn('파일이 선택되지 않았습니다.');
        return;
      }

      const filePath = `settlement_receipts/${authStore.user?.userId}/${file.name}`;
      const storageRef = firebaseRef(firebaseStorage, filePath);

      try {
        console.log('Uploading file to Firebase:', file.name);
        const snapshot = await uploadBytes(storageRef, file);
        console.log('File uploaded successfully:', snapshot);

        const downloadUrl = await getDownloadURL(snapshot.ref);
        console.log('File download URL:', downloadUrl);

        imageUrl.value = downloadUrl; // 이미지 URL 저장
      } catch (error) {
        console.error('파일 업로드 실패:', error);
        alert('파일 업로드 중 오류가 발생했습니다. 다시 시도해주세요.');
      }
    };

    // 정산 요청 제출
    const submitRequest = async () => {
      const token = authStore.token; // 토큰 가져오기
      if (!token) {
        alert('인증이 필요합니다. 다시 로그인해 주세요.');
        return;
      }

      try {
        const payload = {
          title: title.value,
          imageUrl: imageUrl.value,
          amount: parseFloat(amount.value),
        };

        await axios.post('/api/v1/settlements/create', payload, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        closeModal();
        fetchRequests();
      } catch (error) {
        console.error('Error submitting request:', error);
      }
    };

    // 정산 요청 목록 불러오기
    const fetchRequests = async () => {
      const token = authStore.accessToken; // 토큰 가져오기
      console.log('Access Token:', token);

      if (!token) {
        console.warn('인증이 필요합니다.');
        return;
      }

      try {
        const response = await axios.get('/api/v1/settlements/all', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        settlementRequests.value = response.data.map((req) => ({
          title: req.title,
          userName: req.userName,
          amount: req.amount, // 금액 추가
          date: req.createdAt, // 날짜
          imageUrl: req.imageUrl, // 영수증 이미지
        }));
      } catch (error) {
        console.error('Error fetching invitations:', error);
        if (error.response?.status === 401) {
          alert('인증이 만료되었습니다. 다시 로그인해 주세요.');
        }
      }
    };

    // 세부 정보 모달 열기
    const openDetailModal = (request) => {
      selectedRequest.value = request;
      isDetailModalVisible.value = true;
    };

    // 세부 정보 모달 닫기
    const closeDetailModal = () => {
      isDetailModalVisible.value = false;
      selectedRequest.value = {};
    };

    onMounted(fetchRequests);

    return {
      settlementRequests,
      isModalVisible,
      isDetailModalVisible,
      title,
      amount,
      imageUrl,
      uploadProgress,
      selectedRequest,
      openModal,
      closeModal,
      onFileUpload,
      submitRequest,
      openDetailModal,
      closeDetailModal,
    };
  },
};
</script>

<style scoped>
.settlement-container {
  width: 600px;
  min-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.receipt-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 5px;
  cursor: pointer;
}

.clickable-text {
  color: #7f56d9;
  text-decoration: underline;
  cursor: pointer;
}

.receipt-image-large {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  margin-top: 10px;
}

.p-field {
  margin-bottom: 1.5rem;
}

h2 {
  color: #414651;
  font-size: 24px;
  font-weight: bold;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #414651;
}

.p-inputtext,
.p-fileupload {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
}

.p-button-primary {
  background-color: #7f56d9;
  border-color: #7f56d9;
}

.p-button-primary:hover {
  background-color: #6b49c1;
}
</style>
