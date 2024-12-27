<template>
    <div class="card-container">
      <!-- 타이틀 -->
      <div class="title">회원 정보 조회</div>
  
      <!-- 입력 폼 -->
      <form @submit.prevent="handleFindUser" class="form">
        <div class="input-container">
          <label for="nickname">닉네임</label>
          <input
            type="text"
            id="nickname"
            v-model="form.nickname"
            placeholder="닉네임을 입력해 주세요"
            required
          />
        </div>
  
        <div class="input-container">
          <label for="phone">연락처</label>
          <input
            type="text"
            id="phone"
            v-model="form.phone"
            placeholder="연락처를 입력해 주세요"
            required
          />
        </div>
  
        <div class="input-container">
          <label for="email">이메일</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            placeholder="이메일을 입력해 주세요"
            required
          />
        </div>
  
        <div class="submit-button">
          <button type="submit" :disabled="loading">
            <span v-if="loading">Loading...</span>
            <span v-else>회원 정보 확인</span>
          </button>
          <p class="footer-text">
      로그인 페이지로 가시겠습니까? <a href="#" @click.prevent="goToLogin">Log in</a>
    </p>
        </div>
      </form>
  
      <!-- 성공 메시지 -->
      <div v-if="success" class="result success">
        회원 정보가 확인되었습니다. 비밀번호를 재설정해 주세요.
      </div>
  
      <!-- 에러 메시지 -->
      <div v-if="error" class="result error">{{ error }}</div>
    </div>
  </template>
  
  <script>
  import axios from "axios";

export default {
  name: "FindUserInfoView",
  data() {
    return {
      form: {
        nickname: "",
        phone: "",
        email: "",
      },
      error: "",
      success: false,
      loading: false,
    };
  },
  methods: {
    async handleFindUser() {
  this.error = "";
  this.success = false;
  this.loading = true;

  try {
    // 백엔드에 회원정보 일치 여부 확인 요청
    const response = await axios.post("/api/v1/find/user", this.form);

    // 서버에서 exists가 true인 경우
    if (response.data.exists) {
      this.success = true;
      setTimeout(() => {
        this.$router.push({
          name: "ResetPassword",
          query: this.form,
        });
      }, 2000);
    } else {
      throw new Error(response.data.message || "회원 정보가 일치하지 않습니다.");
    }
  } catch (err) {
    // 에러 메시지 처리
    this.error = err.response?.data?.message || err.message || "서버 오류가 발생했습니다.";
  } finally {
    this.loading = false;
  }
    },
    goToLogin() {
      // 로그인 페이지로 이동
      this.$router.push("/login");
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
  
  .title {
    font-size: 1.3rem;
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
    text-align: center;
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
  