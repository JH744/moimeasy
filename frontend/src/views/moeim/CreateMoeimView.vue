
<template>
  <div class="moeim-create-container">
    <h2 class="title">모임 생성</h2>
    <form @submit.prevent="createMoeim">
      <div class="form-group">
        <label for="moeimName">모임 이름</label>
        <input
          id="moeimName"
          type="text"
          v-model="moeimName"
          placeholder="모임 이름을 입력하세요"
          required
        />
      </div>
      <button type="submit" class="btn-primary">모임 생성</button>
    </form>

    <!-- 모달 -->
    <Modal
      :isVisible="isModalVisible"
      title="MoeimEasy"
      :modalMessage="modalMessage"
      @close="closeModal"
    >
      <template #footer>
        <button class="btn-primary" @click="closeModal">입장하기</button>
      </template>
    </Modal>
  </div>
</template>

<script>
import axios from 'axios';
import Modal from '@/components/Modal.vue';

export default {
  components: { Modal },
  data() {
    return {
      moeimName: '',
      isModalVisible: false,
      modalMessage: '',
    };
  },
  methods: {
    async createMoeim() {
      try {
        const token = localStorage.getItem('accessToken'); // 저장된 토큰 가져오기
        const payload = { moeimName: this.moeimName };

        // API 호출
        const response = await axios.post('/api/v1/moeim/create', payload, {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`, // 인증 헤더 추가
          },
        });

        // 성공 시 모달 표시
        this.modalMessage = `"${response.data.moeimName}" 모임이 생성되었습니다. 모임 코드: ${response.data.moeimCode}`;
        this.isModalVisible = true;
        this.isSuccess = true;
        this.moeimName = ''; // 입력 필드 초기화
      } catch (error) {
        console.error('Error creating moeim:', error.response || error.message);
        this.modalMessage = '모임 생성에 실패했습니다. 다시 시도해주세요.';
        this.isModalVisible = true;
        this.isSuccess = false;
      }
    },
    closeModal() {
      this.isModalVisible = false;
      // 성공 시 페이지 이동
      if (this.isSuccess) {
        this.$router.push('/main');
      }
    },
  },
};
</script>

<style scoped>
.moeim-create-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #414651;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  color: #414651;
}

.btn-primary {
  width: 100%;
  background-color: #7f56d9;
  color: white;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.btn-primary:hover {
  background-color: #6a48b0;
}
</style>
