<template>
  <div class="card-container">
    <!-- 타이틀 -->
    <h1 class="title2">MoeimEasy</h1>
    <div class="title">회원가입</div>

    <!-- 오류 메시지 표시 -->
    <div v-if="errorMessage" class="result error">
      {{ errorMessage }}
    </div>

    <!-- 성공 메시지 표시 -->
    <div v-if="successMessage" class="result success">
      {{ successMessage }}
    </div>

    <!-- 입력 폼 -->
    <form @submit.prevent="handleRegister" class="form">
      <!-- 사용자 이름 -->
      <div class="input-container">
        <label for="username">이름</label>
        <input
          type="text"
          id="username"
          v-model="form.username"
          placeholder="이름을 입력해 주세요"
          required
        />
      </div>

      <!-- 이메일 -->
      <div class="input-container">
        <label for="email">이메일</label>
        <input
          type="email"
          id="email"
          v-model="form.email"
          placeholder="이메일을 입력해 주세요"
          required
        />
        <button
          type="button"
          class="check-button"
          @click="checkEmail"
          :disabled="isEmailChecked || isSubmitting"
        >
          이메일 중복 확인
        </button>
        <div :class="feedbackEmailClass">{{ feedbackEmail }}</div>
      </div>

      <!-- 닉네임 -->
      <div class="input-container">
        <label for="nickname">닉네임</label>
        <input
          type="text"
          id="nickname"
          v-model="form.nickname"
          placeholder="닉네임을 입력해 주세요"
          required
        />
        <button
          type="button"
          class="check-button"
          @click="checkNickname"
          :disabled="isNicknameChecked || isSubmitting"
        >
          닉네임 중복 확인
        </button>
        <div :class="feedbackNicknameClass">{{ feedbackNickname }}</div>
      </div>

      <!-- 연락처 -->
      <div class="input-container">
        <label for="phone">연락처</label>
        <input
          type="tel"
          id="phone"
          v-model="form.phone"
          placeholder="연락처를 입력해 주세요"
          required
        />
      </div>

      <!-- 비밀번호 -->
      <div class="input-container">
        <label for="password">비밀번호</label>
        <input
          type="password"
          id="password"
          v-model="form.password"
          placeholder="비밀번호를 입력해 주세요"
          required
        />
      </div>

      <!-- 비밀번호 확인 -->
      <div class="input-container">
        <label for="confirmPassword">비밀번호 확인</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="form.confirmPassword"
          placeholder="비밀번호를 확인해 주세요"
          @input="checkPasswordMatch"
          required
        />
        <div v-if="passwordMismatch" class="result error">
          비밀번호가 일치하지 않습니다.
        </div>
      </div>

      <!-- 약관 동의 -->
      <div class="form-group-checkbox">
        <input type="checkbox" id="terms" v-model="form.acceptTerms" required />
        <label for="terms">약관 및 개인정보 보호 정책에 동의합니다.</label>
      </div>

      <!-- 제출 버튼 -->
      <div class="submit-button">
        <button type="submit" :disabled="isSubmitting">
          <span v-if="isSubmitting">Loading...</span>
          <span v-else>회원가입</span>
        </button>
      </div>
    </form>

    <!-- 성공 모달 -->
    <Modal
      :isVisible="showSuccessModal"
      title="MoeimEasy"
      modalMessage="회원가입이 성공적으로 완료되었습니다!"
      @close="goToLogin"
    >
      <template #footer>
        <button @click="goToLogin" class="confirm-button">로그인하기</button>
      </template>
    </Modal>

    <!-- 로그인 페이지 이동 -->
    <p class="footer-text">
      이미 계정이 있으신가요?
      <a href="#" @click.prevent="goToLogin">Log in</a>
    </p>
  </div>
</template>

<script>
import { formatDate } from '@fullcalendar/core';
import Modal from '@/components/Modal.vue';
import api from '@/axios';

export default {
  name: 'Signup',
  components: { Modal },
  data() {
    return {
      form: {
        username: '',
        email: '',
        nickname: '',
        phone: '',
        password: '',
        confirmPassword: '',
        acceptTerms: false,
      },
      errors: {},
      errorMessage: '',
      successMessage: '',
      feedbackNickname: '',
      feedbackEmail: '',
      feedbackNicknameClass: '',
      feedbackEmailClass: '',
      isSubmitting: false,
      passwordMismatch: false,
      isNicknameChecked: false,
      isEmailChecked: false,
      showSuccessModal: false,
    };
  },
  mounted() {
    const isAuthenticated = localStorage.getItem('accessToken');
    if (isAuthenticated) {
      this.$router.push('/main'); // 로그인된 사용자는 메인 페이지로 리디렉션
    }
  },
  methods: {
    /**
     * 회원가입 처리
     */
    async handleRegister() {
      this.errorMessage = '';
      this.successMessage = '';
      this.errors = {};
      this.passwordMismatch = false;

      // 비밀번호 일치 여부 확인
      if (this.form.password !== this.form.confirmPassword) {
        this.passwordMismatch = true;
        return;
      }
      // 중복 확인 여부 확인
      if (!this.isNicknameChecked || !this.isEmailChecked) {
        this.errorMessage = '닉네임과 이메일 중복 확인을 완료해 주세요.';
        return;
      }

      this.isSubmitting = true;

      try {
        // 회원가입 API 호출: POST /api/v1/signup
        const response = await api.post('/api/v1/signup', {
          username: this.form.username,
          nickname: this.form.nickname,
          email: this.form.email,
          phone: this.form.phone,
          password: this.form.password,
        });

        if (response.status === 201) {
          this.successMessage = response.data.message;
          this.resetForm(true); // 성공 시 폼 초기화 유지
          this.showSuccessModal = true;
        } else {
          this.errorMessage = '회원가입에 실패했습니다. 다시 시도해 주세요.';
        }
      } catch (error) {
        if (error.response) {
          if (error.response.status === 400) {
            // Validation 에러
            this.errors = error.response.data;
          } else if (error.response.status === 409) {
            // 중복된 이메일 또는 닉네임
            this.errorMessage = error.response.data.error;
          } else {
            // 기타 에러
            this.errorMessage =
              error.response.data.error || '회원가입 중 오류가 발생했습니다.';
          }
        } else {
          this.errorMessage = '서버와의 연결이 원활하지 않습니다.';
        }
        console.error('회원가입 오류:', error);
      } finally {
        this.isSubmitting = false;
      }
    },

    /**
     * 로그인 페이지로 이동
     */
    goToLogin() {
      this.$router.push('/login');
    },
    /**
     * 폼 초기화
     * @param {boolean} keepSuccess - 성공 메시지를 유지할지 여부
     */
    resetForm(keepSuccess = false) {
      this.form = {
        name: '',
        nickname: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
      };
      this.errors = {};
      if (!keepSuccess) {
        this.errorMessage = '';
        this.successMessage = '';
      }
      this.feedbackNickname = '';
      this.feedbackEmail = '';
      this.feedbackNicknameClass = '';
      this.feedbackEmailClass = '';
      this.passwordMismatch = false;
      this.isNicknameChecked = false;
      this.isEmailChecked = false;
      // 리디렉션: 로그인 페이지로 이동 (필요 시 수정)
      // this.$router.push('/login')
    },

    /**
     * 닉네임 중복 확인
     */
    async checkNickname() {
      const nickname = this.form.nickname.trim();
      if (nickname === '') {
        this.feedbackNicknameClass = 'feedback-message error';
        this.feedbackNickname = '닉네임을 입력해 주세요.';
        this.isNicknameChecked = false;
        return;
      }

      try {
        const response = await api.get('/api/v1/signup/check-nickname', {
          params: { nickname },
        });
        const data = response.data;

        if (data.exists) {
          this.feedbackNicknameClass = 'feedback-message error';
          this.feedbackNickname = '이미 사용 중인 닉네임입니다.';
          this.isNicknameChecked = false;
        } else {
          this.feedbackNicknameClass = 'feedback-message success';
          this.feedbackNickname = '사용 가능한 닉네임입니다.';
          this.isNicknameChecked = true;
        }
      } catch (error) {
        this.feedbackNicknameClass = 'feedback-message error';
        this.feedbackNickname = '닉네임 중복 확인 중 오류가 발생했습니다.';
        this.isNicknameChecked = false;
        console.error('닉네임 중복 확인 오류:', error);
      }
    },

    /**
     * 이메일 중복 확인
     */
    async checkEmail() {
      const email = this.form.email.trim();
      if (email === '') {
        this.feedbackEmailClass = 'feedback-message error';
        this.feedbackEmail = '이메일을 입력해 주세요.';
        this.isEmailChecked = false;
        return;
      }

      try {
        const response = await api.get('/api/v1/signup/check-email', {
          params: { email },
        });
        const data = response.data;

        if (data.exists) {
          this.feedbackEmailClass = 'feedback-message error';
          this.feedbackEmail = '이미 사용 중인 이메일입니다.';
          this.isEmailChecked = false;
        } else {
          this.feedbackEmailClass = 'feedback-message success';
          this.feedbackEmail = '사용 가능한 이메일입니다.';
          this.isEmailChecked = true;
        }
      } catch (error) {
        this.feedbackEmailClass = 'feedback-message error';
        this.feedbackEmail = '이메일 중복 확인 중 오류가 발생했습니다.';
        this.isEmailChecked = false;
        console.error('이메일 중복 확인 오류:', error);
      }
    },

    /**
     * 닉네임 입력 시 중복 확인 상태 리셋
     */
    resetNicknameCheck() {
      if (this.isNicknameChecked) {
        this.isNicknameChecked = false;
        this.feedbackNickname = '닉네임 중복 확인을 다시 해주세요.';
        this.feedbackNicknameClass = 'feedback-message warning';
      }
    },

    /**
     * 이메일 입력 시 중복 확인 상태 리셋
     */
    resetEmailCheck() {
      if (this.isEmailChecked) {
        this.isEmailChecked = false;
        this.feedbackEmail = '이메일 중복 확인을 다시 해주세요.';
        this.feedbackEmailClass = 'feedback-message warning';
      }
    },
    //   console.log("회원가입 성공:", this.form.username);
    //   // 회원가입 완료 후 로그인 페이지로 이동
    //   this.$router.push("/login");
    // },
    /**
     * 비밀번호 일치 여부 확인
     */
    checkPasswordMatch() {
      if (this.form.password !== this.form.confirmPassword) {
        this.passwordMismatch = true;
      } else {
        this.passwordMismatch = false;
      }
    },
  },
};
</script>

<style scoped>
/* 전체 카드 컨테이너 스타일 */
.card-container {
  width: 400px;
  margin: 50px auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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

/* 타이틀 스타일 */
.title {
  font-size: 1.3rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 1.5rem;
  color: #414651;
}

/* 폼 전체 스타일 */
.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 입력 필드 스타일 */
.input-container {
  display: flex;
  flex-direction: column;
}

.input-container label {
  margin-bottom: 5px;
  font-size: 0.9rem;
  color: #555;
}

.input-container input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
}

.input-container input:focus {
  border-color: #8a4af3;
  outline: none;
  box-shadow: 0 0 5px rgba(138, 74, 243, 0.3);
}

/* 중복 확인 버튼 스타일 */
.check-button {
  margin-top: 10px;
  background-color: #8a4af3;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9rem;
}

.check-button:hover {
  background-color: #6c38cc;
}

/* 약관 동의 체크박스 스타일 */
.form-group-checkbox {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
  color: #555;
}

.form-group-checkbox input {
  width: 16px;
  height: 16px;
}

/* 제출 버튼 스타일 */
.submit-button button {
  width: 100%;
  padding: 10px;
  font-size: 1rem;
  font-weight: bold;
  background-color: #8a4af3;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-button button:hover {
  background-color: #6c38cc;
}

.submit-button button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 결과 메시지 스타일 */
.result {
  text-align: center;
  font-size: 0.9rem;
}

.result.success {
  color: green;
}

.result.error {
  color: red;
}

/* 로그인 페이지 이동 링크 */
.footer-text {
  color: #555;
  text-align: center;
  margin-top: 20px;
}

.footer-text a {
  color: #8a4af3;
  text-decoration: none;
  font-weight: bold;
}

.footer-text a:hover {
  text-decoration: underline;
}
.confirm-button {
  background-color: #8a4af3;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
}

.confirm-button:hover {
  background-color: #6c38cc;
}
</style>
