<template>
  <div class="invite-container">
    <h2 class="title">회원 초대</h2>
    <form @submit.prevent="sendInvitation">
      <div class="form-group">
        <label for="email">이메일</label>
        <input
          id="email"
          type="email"
          v-model="formData.email"
          placeholder="초대할 이메일을 입력하세요"
          required
        />
      </div>
      <div class="form-group">
        <label for="message">메시지</label>
        <textarea
          id="message"
          v-model="formData.message"
          placeholder="초대 메시지를 작성하세요"
          required
        ></textarea>
      </div>
      <button type="submit" class="btn-primary">초대 보내기</button>
    </form>

    <!-- 공용 모달 -->
    <Modal
      v-if="isModalVisible"
      :isVisible="isModalVisible"
      title="초대 성공"
      @close="redirectToInvitationList"
    >
      <template #content>
        <p>초대가 성공적으로 전송되었습니다!</p>
      </template>
      <template #footer>
        <button class="btn-primary" @click="redirectToInvitationList">
          확인
        </button>
      </template>
    </Modal>
  </div>
</template>

<script>
import Modal from '@/components/Modal.vue';
import axios from 'axios';

export default {
  components: { Modal },
  data() {
    return {
      formData: {
        email: '',
        message: '',
      },
      isModalVisible: false, // 모달 초기 상태
    };
  },
  methods: {
    async sendInvitation() {
      // HTML 템플릿 구성
      const emailHtmlContent = `
    <!DOCTYPE html>
    <html lang="ko">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>MoeimEasy 초대장</title>
      <style>
        body {
          font-family: Arial, sans-serif;
          background-color: #f7f7f7;
          color: #333;
          margin: 0;
          padding: 0;
        }
        .invite-container {
          max-width: 600px;
          margin: 40px auto;
          padding: 20px;
          background: #fff;
          border-radius: 12px;
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
          text-align: center;
        }
        .invite-header h1 {
          margin: 0;
          font-size: 28px;
          background-color: #7f56d9;
          color: #fff;
          padding: 20px;
          border-top-left-radius: 12px;
          border-top-right-radius: 12px;
        }
        .invite-body {
          padding: 20px;
        }
        .footer {
          margin-top: 30px;
          font-size: 12px;
          color: #888;
        }
      </style>
    </head>
    <body>
      <div class="invite-container">
        <div class="invite-header">
          <h1>MoeimEasy에서 초대합니다!</h1>
        </div>
        <div class="invite-body">
          <p>${this.formData.message}</p>
        </div>
        <div class="footer">
          <p>이 이메일은 MoeimEasy의 초대 이메일입니다. 수신을 원하지 않는다면 무시하세요.</p>
        </div>
      </div>
    </body>
    </html>
  `;

      try {
        const token = localStorage.getItem('accessToken'); // 인증 토큰 가져오기
        if (!token) {
          throw new Error('인증 토큰이 없습니다.');
        }

        await axios.post(
          '/api/v1/invitations/send',
          {
            email: this.formData.email,
            message: this.formData.message,
            htmlContent: emailHtmlContent,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`, // 토큰 추가
            },
          }
        );
        this.isModalVisible = true; // 초대 성공 시 모달 활성화
      } catch (error) {
        console.error(
          'Error sending invitation:',
          error.response?.data || error.message
        );
        alert('초대 전송에 실패했습니다.');
      }
    },

    redirectToInvitationList() {
      this.isModalVisible = false; // 모달 닫기
      this.$router.push('/invitation-list'); // 초대 목록 페이지로 이동
    },
  },
};
</script>

<style scoped>
.invite-container {
  max-width: 600px;
  margin: 0 auto;
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

input,
textarea {
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
