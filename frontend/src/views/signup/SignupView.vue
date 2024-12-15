<template>
  <div class="signup-container">
    <h2 class="title">회원가입</h2>

        <!-- 오류 메시지 표시 -->
        <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>

    <!-- 성공 메시지 표시 -->
    <div v-if="successMessage" class="success-message">
      <p>{{ successMessage }}</p>
    </div>

    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <!-- 이름 -->
        <label for="username">Username</label>
        <input
          type="text" id="username"
          v-model="form.username"
          placeholder="Your username"
          required/>
          <div v-if="errors.name" class="error-message">
            <p>{{ errors.name }}</p>
          </div>
      </div>

      <!-- 이메일 -->
      <div class="form-group">
        <label for="email">Email</label>
        <input
          type="email"
          id="email"
          v-model="form.email"
          @input="resetEmailCheck"
          placeholder="Your email"
          required/>
          <button type="button" class="check-button" @click="checkEmail" :disabled="isEmailChecked || isSubmitting">
            중복 확인
          </button>
          <div :class="feedbackEmailClass">{{ feedbackEmail }}</div>
          <div v-if="errors.email" class="error-message">
            <p>{{ errors.email }}</p>
          </div>
      </div>

      <!-- 닉네임 -->
      <div class="form-group">
        <label for="nickname">Nickname</label>
        <input
          type="nickname"
          v-model="form.nickname"
          @input="resetNicknameCheck"
          placeholder="Your nickname"
          required/>
          <button type="button" class="check-button" @click="checkNickname" :disabled="isNicknameChecked || isSubmitting">
            중복 확인
          </button>
          <div :class="feedbackNicknameClass">{{ feedbackNickname }}</div>
          <div v-if="errors.nickname" class="error-message">
            <p>{{ errors.nickname }}</p>
          </div>
      </div>

      <!-- 연락처 -->
      <div class="form-group">
        <label for="phone">Phone</label>
        <input
          type="tel"
          v-model="form.phone"
          placeholder="Your phone"
          required
          pattern="^[0-9\+\-\(\)\s]+$"
          title="유효한 연락처를 입력해 주세요."
          />
          <div v-if="errors.phone_number" class="error-message">
            <p>{{ errors.phone_number }}</p>
          </div>
        </div>

      <!-- 비밀번호 -->
      <div class="form-group">
        <label>Password</label>
        <input
          type="password"
          v-model="form.password"
          placeholder="Password"
          required
        />
        <div v-if="errors.password" class="error-message">
            <p>{{ errors.password }}</p>
          </div>
      </div>

      <!-- 비밀번호 확인 -->
      <div class="form-group">
        <label>Confirm Password</label>
        <input
          type="password"
          v-model="form.confirmPassword"
          placeholder="Confirm password"
          required
          @input="checkPasswordMatch"
        />
        <div v-if="passwordMismatch" class="error-message">
            <p>비밀번호가 일치하지 않습니다.</p>
          </div>
      </div>

       <!-- 제출 버튼 -->
      <div class="form-group-checkbox">
        <input
          type="checkbox"
          id="terms"
          v-model="form.acceptTerms"
          required
        />

        <label for="terms">I accept the terms and privacy policy</label>
      </div>
      <button type="submit" class="btn-primary">회원가입</button>
    </form>
    <div class="alternative">
      <!-- <p>Or Register with</p> -->
      <!-- <div class="social-buttons">
        <button class="social-btn">Facebook</button>
        <button class="social-btn">Google</button>
        <button class="social-btn">Apple</button>
      </div> -->
    </div>
    <p class="footer-text">
      Already have an account? <a href="#" @click.prevent="goToLogin">Log in</a>
    </p>
  </div>
</template>

<script>

import { formatDate } from '@fullcalendar/core';
import api from '@/axios'


export default {
  name: "Signup",
  data() {
    return {
      form: {
        username: "",
        email: "",
        nickname:"",
        phone:"",
        password: "",
        confirmPassword: "",
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
      isEmailChecked: false
    }
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
      this.errorMessage = ''
      this.successMessage = ''
      this.errors = {}
      this.passwordMismatch = false

      // 비밀번호 일치 여부 확인
      if (this.form.password !== this.form.confirmPassword) {
        this.passwordMismatch = true
        return;
      }
      // 중복 확인 여부 확인
      if (!this.isNicknameChecked || !this.isEmailChecked) {
        this.errorMessage = '닉네임과 이메일 중복 확인을 완료해 주세요.'
        return
      }

      this.isSubmitting = true

      try {
        // 회원가입 API 호출: POST /api/v1/signup
        const response = await api.post('/api/v1/signup', {
          username: this.form.username,
          nickname: this.form.nickname,
          email: this.form.email,
          phone: this.form.phone,
          password: this.form.password
        })

        if (response.status === 201) {
          this.successMessage = response.data.message
          this.resetForm(true) // 성공 시 폼 초기화 유지
        } else {
          this.errorMessage = '회원가입에 실패했습니다. 다시 시도해 주세요.'
        }
      } catch (error) {
        if (error.response) {
          if (error.response.status === 400) {
            // Validation 에러
            this.errors = error.response.data
          } else if (error.response.status === 409) {
            // 중복된 이메일 또는 닉네임
            this.errorMessage = error.response.data.error
          } else {
            // 기타 에러
            this.errorMessage = error.response.data.error || '회원가입 중 오류가 발생했습니다.'
          }
        } else {
          this.errorMessage = '서버와의 연결이 원활하지 않습니다.'
        }
        console.error('회원가입 오류:', error)
      } finally {
        this.isSubmitting = false
      }
    },

    /**
     * 로그인 페이지로 이동
     */
     goToLogin() {
      this.$router.push("/login");
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
        confirmPassword: ''
      }
      this.errors = {}
      if (!keepSuccess) {
        this.errorMessage = ''
        this.successMessage = ''
      }
      this.feedbackNickname = ''
      this.feedbackEmail = ''
      this.feedbackNicknameClass = ''
      this.feedbackEmailClass = ''
      this.passwordMismatch = false
      this.isNicknameChecked = false
      this.isEmailChecked = false
      // 리디렉션: 로그인 페이지로 이동 (필요 시 수정)
      // this.$router.push('/login')
    },

      /**
     * 닉네임 중복 확인
     */
     async checkNickname() {
      const nickname = this.form.nickname.trim()
      if (nickname === '') {
        this.feedbackNicknameClass = 'feedback-message error'
        this.feedbackNickname = '닉네임을 입력해 주세요.'
        this.isNicknameChecked = false
        return
      }

      try {
        const response = await api.get('/api/v1/signup/check-nickname', {
          params: { nickname }
        })
        const data = response.data

        if (data.exists) {
          this.feedbackNicknameClass = 'feedback-message error'
          this.feedbackNickname = '이미 사용 중인 닉네임입니다.'
          this.isNicknameChecked = false
        } else {
          this.feedbackNicknameClass = 'feedback-message success'
          this.feedbackNickname = '사용 가능한 닉네임입니다.'
          this.isNicknameChecked = true
        }
      } catch (error) {
        this.feedbackNicknameClass = 'feedback-message error'
        this.feedbackNickname = '닉네임 중복 확인 중 오류가 발생했습니다.'
        this.isNicknameChecked = false
        console.error('닉네임 중복 확인 오류:', error)
      }
    },

    /**
     * 이메일 중복 확인
     */
     async checkEmail() {
      const email = this.form.email.trim()
      if (email === '') {
        this.feedbackEmailClass = 'feedback-message error'
        this.feedbackEmail = '이메일을 입력해 주세요.'
        this.isEmailChecked = false
        return
      }

      try {
        const response = await api.get('/api/v1/signup/check-email', {
          params: { email }
        })
        const data = response.data

        if (data.exists) {
          this.feedbackEmailClass = 'feedback-message error'
          this.feedbackEmail = '이미 사용 중인 이메일입니다.'
          this.isEmailChecked = false
        } else {
          this.feedbackEmailClass = 'feedback-message success'
          this.feedbackEmail = '사용 가능한 이메일입니다.'
          this.isEmailChecked = true
        }
      } catch (error) {
        this.feedbackEmailClass = 'feedback-message error'
        this.feedbackEmail = '이메일 중복 확인 중 오류가 발생했습니다.'
        this.isEmailChecked = false
        console.error('이메일 중복 확인 오류:', error)
      }
    },

    /**
     * 닉네임 입력 시 중복 확인 상태 리셋
     */
     resetNicknameCheck() {
      if (this.isNicknameChecked) {
        this.isNicknameChecked = false
        this.feedbackNickname = '닉네임 중복 확인을 다시 해주세요.'
        this.feedbackNicknameClass = 'feedback-message warning'
      }
    },

    /**
     * 이메일 입력 시 중복 확인 상태 리셋
     */
     resetEmailCheck() {
      if (this.isEmailChecked) {
        this.isEmailChecked = false
        this.feedbackEmail = '이메일 중복 확인을 다시 해주세요.'
        this.feedbackEmailClass = 'feedback-message warning'
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
        this.passwordMismatch = true
      } else {
        this.passwordMismatch = false
      }
    }
  }
}
</script>

<style scoped>
/* 스타일 추가 */
.signup-container {
  min-width: 500px;
  width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.title {
  font-weight: bold;
  text-align: center;
  color: #414651;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
  position: relative; /* position을 relative로 설정 */
}


label {
  color: #414651;
  display: block;

  margin-bottom: 5px;
}

input[type="text"],
input[type="email"],
input[type="password"], 
input[type="nickname"], 
input[type="tel"]{
  width: 100%; /* 모든 입력란 길이 동일하게 설정 */
  padding: 10px 40px 10px 10px; /* 오른쪽에 버튼이 들어갈 공간 추가 */
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  box-sizing: border-box; /* 패딩 포함한 크기 계산 */
  height: 40px; /* 입력란 높이 설정 */
  /* width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px; */
}
.form-group-checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.form-group-checkbox input {
  margin-right: 10px;
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
.alternative {
  color: #414651;
  text-align: center;
  margin-top: 20px;
}

.social-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.social-btn {
  background-color: #f1f1f1;
  border: none;
  padding: 8px 12px;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
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

.check-button {
  position: absolute; /* 입력란 안쪽에 위치하도록 설정 */
  top: 50%; /* 입력란의 중앙에 정렬 */
  right: 10px; /* 입력란 오른쪽 끝에서 10px 떨어짐 */
  transform: translateY(-50%); /* 수직 중앙 정렬 */
  height: 28px; /* 입력란 높이에 맞게 버튼 높이 설정 */
  padding: 0 11px; /* 버튼 크기 조정 */
  background-color: #7f56d9;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
  line-height: 30px; /* 버튼 텍스트 수직 정렬 */
  transition: background-color 0.3s;
  box-sizing: border-box; /* 패딩 포함 크기 계산 */
  /* position: absolute;
  right: 10px;
  top: 43px;
  padding: 6px 12px;
  background-color: #7f56d9;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.3s; */
}
</style>
