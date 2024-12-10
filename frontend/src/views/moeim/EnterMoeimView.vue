<template>
  <div class="join-moeim-container">
    <h2 class="title">모임 코드 입력</h2>
    <form @submit.prevent="joinMoeim">
      <div class="form-group">
        <label for="moeimCode">모임 코드</label>
        <input
          id="moeimCode"
          type="text"
          v-model="formData.moeimCode"
          placeholder="초대받은 모임 코드를 입력하세요"
          required
        />
      </div>
      <button type="submit" class="btn-primary">모임 가입</button>
    </form>

    <!-- 모달 -->
    <Modal
      :isVisible="isModalVisible"
      title="MoeimEasy"
      :modalMessage="modalMessage"
      @close="closeModal"
    >
      <template #footer>
        <button class="btn-primary" @click="closeModal">확인</button>
      </template>
    </Modal>
  </div>
</template>

<script>
import axios from 'axios';
import Modal from '@/components/Modal.vue';

axios.defaults.baseURL = 'http://localhost:8088';
axios.defaults.withCredentials = true;

export default {
  components: { Modal },
  data() {
    return {
      formData: {
        moeimCode: '',
      },
      isModalVisible: false,
      modalMessage: '',
    };
  },
  methods: {
    async joinMoeim() {
      try {
        const payload = { moeimCode: this.formData.moeimCode };

        const response = await axios.post('/api/v1/moeim/join', payload, {
          headers: { 'Content-Type': 'application/json' },
        });

        this.modalMessage = response.data || '모임 가입이 완료되었습니다.';
        this.isModalVisible = true;
        this.isSuccess = true;
      } catch (error) {
        console.error('Error joining moeim:', error.response || error.message);
        this.modalMessage = error.response?.data || '모임 가입에 실패했습니다.';
        this.isModalVisible = true;
        this.moeimCode = '';
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
.join-moeim-container {
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
