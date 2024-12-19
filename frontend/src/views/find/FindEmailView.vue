<template>
  <Card class="card-container">
    <template #title>
      <div class="title">아이디 찾기</div>
    </template>
    <template #content>
      <!-- 입력 폼 -->
      <form @submit.prevent="submitForm" class="form">
        <div class="input-container">
          <label for="nickname">닉네임</label>
          <FloatLabel>
            <InputText
              id="nickname"
              v-model="nickname"
              placeholder="가입 시 입력한 닉네임을 입력해 주세요."
              required
            />
          </FloatLabel>
        </div>

        <div class="input-container">
          <label for="phone">연락처</label>
          <FloatLabel>
            <InputText
              id="phone"
              v-model="phone"
              placeholder="'-'를 포함한 연락처를 입력해 주세요."
              required
            />
          </FloatLabel>
        </div>

        <div class="submit-button">
          <Button
            label="아이디 찾기"
            type="submit"
            icon="pi pi-search"
            :loading="loading"
          />
          <p class="footer-text">
      로그인 페이지로 가시겠습니까? <a href="#" @click.prevent="goToLogin">Log in</a>
    </p>
        </div>
      </form>

      <!-- 결과 메시지 -->
      <div v-if="email" class="result">
        <Message severity="success">
          Your Email: <strong>{{ email }}</strong>
        </Message>
      </div>

      <div v-if="error" class="error">
        <Message severity="error">{{ error }}</Message>
      </div>
    </template>
  </Card>
</template>

  
  <script>
  import api from '@/axios';
import { defineComponent, ref } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'FindEmailView',
  setup() {
    const nickname = ref('');
    const phone = ref('');
    const email = ref('');
    const error = ref('');
    const loading = ref(false);

    const router = useRouter(); // Vue Router 인스턴스 가져오기

    const submitForm = async () => {
      error.value = '';
      email.value = '';
      loading.value = true;

      try {
        const response = await api.post('/api/v1/find/email', {
          nickname: nickname.value,
          phone: phone.value,
        });
        email.value = response.data.email;
      } catch (err) {
        if (err.response?.status === 404) {
          error.value = '일치하는 이메일을 찾을 수 없습니다.';
        } else {
          error.value = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
        }
      } finally {
        loading.value = false;
      }
    };

    // 로그인 페이지로 이동
    const goToLogin = () => {
      router.push('/login');
    };

    return { nickname, phone, email, error, loading, submitForm, goToLogin };
  },
});

  </script>
  
  <style scoped>
.card-container {
  width: 400px;
  border: 1px solid #ddd; /* 테두리 색상 */
  min-width: 400px;
  margin: 50px auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 20px; /* 내부 여백 */
  background-color: #fff; /* 배경색 */
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
  width: 100%;
}

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
  width: 100%; /* 모든 input 너비를 부모 컨테이너에 맞춤 */
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box; /* padding과 border 포함 */
}

.input-container input:focus {
  border-color: #8a4af3; /* 포커스 시 테두리 강조 */
  outline: none;
  box-shadow: 0 0 5px rgba(138, 74, 243, 0.3);
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

.result, .error {
  margin-top: 1rem;
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
  