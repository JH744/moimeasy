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
      <Column field="createdAt" header="날짜">
        <template #body="slotProps">
          {{ formatDate(slotProps.data.createdAt) }}
        </template>
      </Column>
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
          <input
            type="file"
            id="file"
            accept="image/*"
            @change="onFileChange"
            class="file-input"
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
          :disabled="isSubmitDisabled"
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
        <h3><strong>제목:</strong>{{ selectedRequest.title }}</h3>
        <p><strong>작성자:</strong> {{ selectedRequest.userName }}</p>
        <p><strong>날짜:</strong> {{ selectedRequest.createdAt }}</p>
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
import { ref, onMounted, watch } from 'vue';
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
    const selectedFile = ref(null); // 선택된 파일
    const isSubmitDisabled = ref(true);
    const authStore = useAuthStore();

    // 날짜 포맷 함수
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };
    // 파일 선택 처리
    const onFileChange = async (event) => {
      const file = event.target.files[0];
      if (!file) return;

      // Firebase에 파일 업로드
      const filePath = `settlement_receipts/${authStore.user?.userId}/${Date.now()}_${file.name}`;
      const storageRef = firebaseRef(firebaseStorage, filePath);

      try {
        await uploadBytes(storageRef, file);
        const downloadUrl = await getDownloadURL(storageRef);
        imageUrl.value = downloadUrl; // Firebase URL 저장
        console.log('File uploaded successfully:', downloadUrl);
      } catch (error) {
        console.error('파일 업로드 실패:', error);
        alert('파일 업로드 중 오류가 발생했습니다.');
      }
    };

    // 제출 버튼 활성화 상태 감지
    watch([title, amount, imageUrl], () => {
      isSubmitDisabled.value = !title.value || !amount.value || !imageUrl.value;
    });

    // 정산 요청 제출
    const submitRequest = async () => {
      if (isSubmitDisabled.value) return;

      try {
        const payload = {
          title: title.value,
          amount: parseFloat(amount.value),
          imageUrl: imageUrl.value, // Firebase URL 전송
        };

        await axios.post('/api/v1/settlements/create', payload, {
          headers: { Authorization: `Bearer ${authStore.accessToken}` },
        });

        closeModal();
        await fetchRequests();
      } catch (error) {
        console.error('Error submitting request:', error);
        alert('정산 요청 중 오류가 발생했습니다.');
      }
    };

    // 모달 열기/닫기
    const openModal = () => {
      isModalVisible.value = true;
    };

    const closeModal = () => {
      isModalVisible.value = false;
      title.value = '';
      amount.value = '';
      imageUrl.value = '';
      selectedFile.value = null;
    };

    // 정산 요청 목록 불러오기
    const fetchRequests = async () => {
      try {
        const response = await axios.get('/api/v1/settlements/all', {
          headers: { Authorization: `Bearer ${authStore.accessToken}` },
        });
        settlementRequests.value = response.data;
      } catch (error) {
        console.error('Error fetching requests:', error);
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
      selectedFile,
      isSubmitDisabled,
      selectedRequest,
      openModal,
      closeModal,
      onFileChange,
      submitRequest,
      openDetailModal,
      closeDetailModal,
      formatDate,
    };
  },
};
</script>

<style scoped>
.settlement-container {
  width: 98%;
  margin: 10px;
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
