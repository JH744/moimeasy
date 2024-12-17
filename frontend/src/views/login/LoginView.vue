<template>
  <div class="login-container">
    <h2 class="title">로그인</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>Email</label>
        <input
          type="email"
          v-model="form.email"
          placeholder="Enter your email"
          required
        />
      </div>
      <div class="form-group">
        <label>Password</label>
        <input
          type="password"
          v-model="form.password"
          placeholder="Enter your password"
          required
        />
      </div>
      <div class="form-options">
    <div>
      <a href="#" class="forgot-password" @click.prevent="forgotId"
          >아이디 찾기</a
        >
    </div>
        <a href="#" class="forgot-password" @click.prevent="forgotPassword"
          >비밀번호 찾기</a
        >
      </div>
      <button type="submit" class="btn-primary">로그인</button>
    </form>
    <!-- 회원가입 버튼 -->
    <button class="btn-secondary" @click="goToSignup">회원가입</button>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';

export default {
  name: 'login',
  data() {
    return {
      form: {
        email: '',
        password: '',
      },
      errorMessage: '',
    };
  },
  setup() {
    const authStore = useAuthStore();
    return { authStore };
  },
  methods: {
    async handleLogin() {
      // 로그인 처리 코드
      if (this.form.email.trim() === '' || this.form.password.trim() === '') {
        alert('모든 필드를 채워주세요.');
        return;
      }
      try {
        await this.authStore.login(this.form.email, this.form.password);
        console.log('로그인 성공'); // 로그인 성공 시 필요한 로직 추가 가능

        // 로그인 후 moiemId 확인
        if (this.authStore.user.moiemId === null) {
          // moiemId가 null이면 MoeimSelectView로 이동
          this.$router.push('/moeim-select');
        } else {
          // moiemId가 있으면 메인 페이지로 이동
          this.$router.push('/main');
        }
      } catch (error) {
        this.errorMessage = error.message;
      }
    },
    resetForm() {
      this.form = {
        email: '',
        password: '',
        rememberMe: false,
      };
      this.errorMessage = '';
      // 리디렉션: 홈으로 이동 (필요 시)
      this.$router.push('/main');
    },
    forgotId() {
      alert('아이디 찾기 기능은 아직 준비 중입니다.');
    },
    forgotPassword() {
      alert('비밀번호 찾기 기능은 아직 준비 중입니다.');
    },
    goToSignup() {
      console.log('회원가입 버튼 클릭');
      this.$router.push('/signup'); // 회원가입 페이지로 이동
    },
  },
};
</script>

<style scoped>
/* 로그인 스타일 */
.login-container {
  width: 400px;
  min-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  color: #414651;
  font-weight: bold;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  color: #414651;
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-password {
  color: #7f56d9;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password:hover {
  text-decoration: underline;
}

.btn-primary {
  display: block;
  width: 100%;
  background-color: #7f56d9;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
}

.btn-primary:hover {
  background-color: #6a48b0;
}

.btn-secondary {
  display: block;
  width: 100%;
  background-color: white;
  color: #414651;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
}

.btn-secondary:hover {
  background-color: #f7f5fc;
}
</style>
