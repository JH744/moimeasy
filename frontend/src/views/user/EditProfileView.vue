<template>
    <div class="card-container">
      <!-- 타이틀 -->
      <div class="title">회원 정보 수정</div>
  
      <!-- 오류 메시지 표시 -->
      <div v-if="errorMessage" class="result error">
        {{ errorMessage }}
      </div>
  
      <!-- 성공 메시지 표시 -->
      <div v-if="successMessage" class="result success">
        {{ successMessage }}
      </div>
  
      <!-- 입력 폼 -->
      <form v-if="user" @submit.prevent="updateUserInfo" class="form">
        <!-- 닉네임 -->
        <div class="input-container">
          <label for="nickname">닉네임</label>
          <input
            type="text"
            id="nickname"
            v-model="nickname"
            placeholder="닉네임을 입력해 주세요"
            required
          />
          <button
            type="button"
            class="check-button"
            @click="checkNickname"
            :disabled="isNicknameChecked || isLoading"
          >
            닉네임 중복 확인
          </button>
          <div :class="feedbackNicknameClass">{{ feedbackNickname }}</div>
        </div>
  
        <!-- 전화번호 -->
        <div class="input-container">
          <label for="phone">전화번호</label>
          <input
            type="tel"
            id="phone"
            v-model="user.phone"
            placeholder="전화번호를 입력해 주세요"
            required
          />
        </div>
  
        <!-- 현재 비밀번호 -->
        <div class="input-container">
          <label for="currentPassword">현재 비밀번호</label>
          <input
            type="password"
            id="currentPassword"
            v-model="password.currentPassword"
            placeholder="현재 비밀번호를 입력해 주세요"
            required
          />
        </div>
  
        <!-- 제출 버튼 -->
        <div class="submit-button">
          <button type="submit" :disabled="!isFormValid || isLoading">
            <span v-if="isLoading">처리 중...</span>
            <span v-else>회원 정보 수정</span>
          </button>
        </div>
      </form>
  
      <!-- 사용자 정보를 로드 중일 때 -->
      <div v-else>사용자 정보를 불러오는 중입니다...</div>
    </div>
  </template>
  
  <script>
  import { useToast } from 'primevue/usetoast';
  import axios from '@/axios';
  
  export default {
    data() {
      return {
        user: null,
        nickname: '',
        password: { currentPassword: '' },
        feedbackNickname: '',
        feedbackNicknameClass: '',
        errorMessage: '',
        successMessage: '',
        isLoading: false,
        isNicknameChecked: false,
      };
    },
    computed: {
      isFormValid() {
        return (
          this.isNicknameChecked &&
          this.password.currentPassword.length > 0
        );
      },
    },
    mounted() {
      const userId = this.$route.params.userId;
      this.loadUserData(userId);
    },
    methods: {
      async loadUserData(userId) {
        try {
          const response = await axios.get(`/api/v1/users/${userId}`);
          this.user = response.data;
          this.nickname = response.data.nickname;
        } catch (error) {
          console.error('사용자 정보를 불러오는 중 오류 발생:', error);
          this.errorMessage = '사용자 정보를 불러오는 중 오류가 발생했습니다.';
        }
      },
      async checkNickname() {
        const nickname = this.nickname.trim();
        if (nickname === '') {
          this.feedbackNicknameClass = 'feedback-message error';
          this.feedbackNickname = '닉네임을 입력해 주세요.';
          this.isNicknameChecked = false;
          return;
        }
  
        try {
          const response = await axios.get(
            `/api/v1/users/check-editnickname?nickname=${nickname}`
          );
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
        }
      },
      async updateUserInfo() {
        if (!this.password.currentPassword) {
          this.errorMessage = '현재 비밀번호를 입력해 주세요.';
          return;
        }
  
        this.isLoading = true;
  
        const payload = {
          currentPassword: this.password.currentPassword,
          nickname: this.nickname,
          phone: this.user ? this.user.phone : '',
        };
  
        try {
          await axios.put(`/api/v1/users/update`, payload);
          this.successMessage = '회원 정보가 성공적으로 수정되었습니다.';
        } catch (error) {
          this.errorMessage = '회원 정보 수정 중 오류가 발생했습니다.';
        } finally {
          this.isLoading = false;
        }
      },
    },
  };
  </script>
  

  <style scoped>
  /* 카드 컨테이너 */
  .card-container {
    width: 400px;
    margin: 50px auto;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 20px;
    background-color: #fff;
  }
  
  /* 타이틀 스타일 */
  .title {
    font-size: 1.3rem;
    font-weight: bold;
    text-align: center;
    margin-bottom: 1.5rem;
    color: #414651;
  }
  
  /* 폼 스타일 */
  .form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  
  /* 입력 필드 */
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
  
  /* 중복 확인 버튼 */
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
  
  /* 제출 버튼 */
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
  
  /* 결과 메시지 */
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
  </style>
  