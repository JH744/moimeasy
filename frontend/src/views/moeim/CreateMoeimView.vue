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
import { useAuthStore } from '@/stores/auth';

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
        const authStore = useAuthStore();
        const token = authStore.accessToken;
        const payload = { moeimName: this.moeimName };

        const response = await axios.post('/api/v1/moeim/create', payload, {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`, // 인증 헤더 추가
          },
        });

        const { moeimId, moeimName, moeimCode } = response.data;

        if (!moeimId || !moeimName || !moeimCode) {
          throw new Error('서버 응답에 필요한 데이터가 없습니다.');
        }

        // authStore와 localStorage 업데이트
        authStore.user = { ...authStore.user, moeimId };
        localStorage.setItem('user', JSON.stringify(authStore.user));

        // 모달 메시지 설정
        this.modalMessage = `"${moeimName}" 모임이 생성되었습니다. 모임 코드: ${moeimCode}`;
        this.isModalVisible = true;
        this.isSuccess = true;

        // 입력 필드 초기화
        this.moeimName = '';
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
