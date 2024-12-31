<template>
  <div class="login-container">
    <Toast />
    <ConfirmDialog></ConfirmDialog>
    <h1 class="title2">MoeimEasy</h1>
    <h2 class="title">로그인</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>아이디</label>
        <input
          type="email"
          v-model="form.email"
          placeholder="이메일을 입력해 주세요"
          required
        />
      </div>
      <div class="form-group">
        <label>패스워드</label>
        <input
          type="password"
          v-model="form.password"
          placeholder="패스워드를 입력해 주세요"
          required
        />
      </div>
      <div class="form-options">
        <div>
          <a href="#" class="forgot-password" @click.prevent="goToFindEmail"
            >아이디 찾기</a
          >
        </div>
        <a href="#" class="forgot-password" @click.prevent="goToFindPassword"
          >비밀번호 초기화</a
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
import { useToast } from 'primevue/usetoast'; // Toast 사용

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
    const toast = useToast(); // Toast 인스턴스
    return { authStore, toast };
  },
  methods: {
    async handleLogin() {
      // 프론트단에서 간단한 유효성 검증
      if (this.form.email.trim() === '' || this.form.password.trim() === '') {
        alert('모든 필드를 채워주세요.');
        return;
      }
      try {
        // Auth Store의 login() 으로 백엔드 /login API 호출
        await this.authStore.login(this.form.email, this.form.password);

        // 로그인 성공 시 Toast 알림
        this.toast.add({
          severity: 'success',
          summary: '로그인 성공',
          detail: '로그인을 성공하셨습니다.',
          life: 3000,
        });
        console.log('로그인 성공'); // 로그인 성공 시 필요한 로직 추가 가능

        // 로그인 후 moiemId 확인
        if (this.authStore.user.moeimId === null) {
          // moiemId가 null이면 MoeimSelectView로 이동
          this.$router.push('/moeim-select');
        } else {
          // moiemId가 있으면 메인 페이지로 이동
          this.$router.push('/main');
        }
      } catch (error) {
        // ----- 여기서 백엔드로부터 받은 에러 메시지를 확인 -----
        // 일반적으로 error.response.data 에 문자열이 들어있다고 가정
        let detailMessage = '로그인을 실패했습니다.';

        if (error.response && error.response.data) {
          const serverMessage = error.response.data;
          // 백엔드에서 body(e.getMessage())로 문자열만 넘기므로, 그대로 사용
          if (serverMessage === '가입되지 않은 아이디입니다.') {
            detailMessage = '가입되지 않은 아이디입니다.';
          } else if (serverMessage === '비밀번호가 일치하지 않습니다.') {
            detailMessage = '비밀번호가 일치하지 않습니다.';
          } else {
            // 그 외 메시지라면 그대로 노출
            detailMessage = serverMessage;
          }
        }

        // Toast로 에러 출력
        this.toast.add({
          severity: 'error',
          summary: '로그인 실패',
          detail: detailMessage,
          life: 3000,
        });
        // 화면에 표시할 수 있도록 data 속성에 저장 (옵션)
        this.errorMessage = detailMessage;
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
    goToFindEmail() {
      console.log('아이디 찾기 클릭'); // 로그로 이벤트 확인
      this.$router.push('/find-email'); // FindEmailView로 이동
    },
    goToFindPassword() {
      console.log('비밀번호 초기화 클릭'); // 로그로 이벤트 확인
      this.$router.push('/finduser'); // FindUserView로 이동;
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
.title2 {
  text-align: center;
  font-size: 1.6rem;
  color: #7f56d9;
  font-weight: bold;
  margin-bottom: 20px;
}

.title {
  text-align: center;
  font-size: 1.3rem;
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
.status-pending {
  color: orange;
}
.status-accepted {
  color: green;
}
.status-rejected {
  color: red;
}
</style>
