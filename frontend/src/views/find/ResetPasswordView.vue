<template>
  <div class="card-container">
    <!-- 타이틀 -->
    <h1 class="title2">MoeimEasy</h1>
    <div class="title">비밀번호 재설정</div>

    <!-- 입력 폼 -->
    <form @submit.prevent="handleResetPassword" class="form">
      <div class="input-container">
        <label for="newPassword">새 비밀번호</label>
        <input
          type="password"
          id="newPassword"
          v-model="form.newPassword"
          placeholder="새 비밀번호를 입력해 주세요"
          required
        />
      </div>

      <div class="input-container">
        <label for="confirmPassword">비밀번호 확인</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="form.confirmPassword"
          placeholder="비밀번호를 확인해 주세요"
          required
        />
      </div>

      <div class="submit-button">
        <button type="submit" :disabled="loading">
          <span v-if="loading">Loading...</span>
          <span v-else>비밀번호 재설정</span>
        </button>
        <p class="footer-text">
          로그인 페이지로 가시겠습니까?
          <a href="#" @click.prevent="goToLogin">Log in</a>
        </p>
      </div>
    </form>

    <!-- 결과 메시지 -->
    <div v-if="error" class="result error">{{ error }}</div>
    <div v-if="success" class="result success">{{ success }}</div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ResetPasswordView',
  data() {
    return {
      form: {
        nickname: this.$route.query.nickname || '',
        phone: this.$route.query.phone || '',
        email: this.$route.query.email || '',
        newPassword: '',
        confirmPassword: '',
      },
      error: '',
      success: '',
      loading: false,
    };
  },
  methods: {
    async handleResetPassword() {
      this.error = '';
      this.success = '';
      this.loading = true;

      // 비밀번호 확인
      if (this.form.newPassword !== this.form.confirmPassword) {
        this.error = '비밀번호가 일치하지 않습니다.';
        this.loading = false;
        return;
      }

      try {
        const response = await axios.post('/api/v1/reset/password', this.form);
        if (response.status === 200) {
          this.success = '비밀번호가 성공적으로 재설정되었습니다!';
          setTimeout(() => this.$router.push('/login'), 3000);
        }
      } catch (err) {
        this.error = '비밀번호 재설정에 실패했습니다. 다시 시도해 주세요.';
      } finally {
        this.loading = false;
      }
    },

    goToLogin() {
      // 로그인 페이지로 이동
      this.$router.push('/login');
    },
  },
};
</script>

<style scoped>
.card-container {
  width: 400px;
  margin: 50px auto;
  border: 1px solid #ddd;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 20px;
  background-color: #fff;
}
.title2 {
  text-align: center;
  font-size: 1.6rem;
  color: #7f56d9;
  font-weight: bold;
  margin-bottom: 20px;
}

.title {
  font-size: 1.3rem;
  color: #414651;
  font-weight: bold;
  text-align: center;
  margin-bottom: 1.5rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-container {
  display: flex;
  flex-direction: column;
}

.input-container label {
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #414651;
}

.input-container input {
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
}

.submit-button button {
  background-color: #8a4af3;
  color: white;
  padding: 0.75rem;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.submit-button button:hover {
  background-color: #6c38cc;
}

.result {
  margin-top: 1rem;
  font-size: 0.9rem;
  text-align: center;
}

.success {
  color: green;
}

.error {
  color: red;
}

.footer-text {
  color: #414651;
  text-align: center;
  margin-top: 20px;
}

.footer-text a {
  color: #7f56d9;
  text-decoration: none;
  font-weight: bold;
}

.footer-text a:hover {
  text-decoration: underline;
}
</style>
