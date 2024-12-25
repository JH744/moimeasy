<template>
  <div class="invite-container">
    <template v-if="!isSuccess">
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
    </template>

    <template v-if="isLoading">
      <div class="spinner-container">
        <div class="spinner"></div>
      </div>
    </template>

    <template v-else-if="isSuccess">
      <h2 class="title">초대 성공</h2>
      <p>초대가 성공적으로 전송되었습니다!</p>
      <br />
      <button class="btn-primary" @click="redirectToInvitationList">
        확인
      </button>
    </template>
  </div>
</template>

<script>
import axios from 'axios';
import { useLoadingStore } from '@/stores/useLoadingStore';
import { useRouter } from 'vue-router';

export default {
  data() {
    return {
      formData: {
        email: '',
        message: '',
      },
      isSuccess: false, // 모달 초기 상태
    };
  },
  setup() {
    const loadingStore = useLoadingStore();
    const router = useRouter();
    return { loadingStore, router };
  },
  computed: {
    isLoading() {
      return this.loadingStore.isLoading;
    },
  },
  methods: {
    //     async sendInvitation() {
    //       // HTML 템플릿 구성
    //       const emailHtmlContent = `
    //   <!DOCTYPE html>
    //   <html lang="ko">
    //   <head>
    //     <meta charset="UTF-8">
    //     <meta name="viewport" content="width=device-width, initial-scale=1.0">
    //     <title>MoeimEasy 초대장</title>
    //   </head>
    //   <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f7f7f7; color: #333;">
    //     <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center;">
    //       <!-- 헤더 -->
    //       <div style="background-color: #7f56d9; color: #fff; padding: 20px;">
    //         <h1 style="margin: 0; font-size: 24px;">MoeimEasy에서 초대합니다!</h1>
    //       </div>

    //       <!-- 본문 -->
    //       <div style="padding: 20px;">
    //         <p style="font-size: 16px; line-height: 1.5; color: #414651;">${this.formData.message}</p>
    //       </div>

    //       <!-- 푸터 -->
    //       <div style="padding: 10px; background-color: #f1f1f1; font-size: 12px; color: #888;">
    //         <p style="margin: 0;">이 이메일은 MoeimEasy의 초대 이메일입니다. 수신을 원하지 않는다면 무시하세요.</p>
    //       </div>
    //     </div>
    //   </body>
    //   </html>
    // `;

    //       try {
    //         const token = localStorage.getItem('accessToken'); // 인증 토큰 가져오기
    //         if (!token) {
    //           throw new Error('인증 토큰이 없습니다.');
    //         }

    //         await axios.post(
    //           '/api/v1/invitations/send',
    //           {
    //             email: this.formData.email,
    //             message: this.formData.message,
    //             htmlContent: emailHtmlContent,
    //           },
    //           {
    //             headers: {
    //               'Content-Type': 'application/json',
    //               Authorization: `Bearer ${token}`, // 토큰 추가
    //             },
    //           }
    //         );
    //         this.isModalVisible = true; // 초대 성공 시 모달 활성화
    //       } catch (error) {
    //         console.error(
    //           'Error sending invitation:',
    //           error.response?.data || error.message
    //         );
    //         alert('초대 전송에 실패했습니다.');
    //       }
    //     },

    async sendInvitation() {
      try {
        this.loadingStore.startLoading();
        const token = localStorage.getItem('accessToken');
        if (!token) {
          throw new Error('인증 토큰이 없습니다.');
        }

        // 서버에 이메일 초대 요청
        const response = await axios.post(
          '/api/v1/invitations/send',
          {
            email: this.formData.email,
            message: this.formData.message,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          }
        );

        this.isSuccess = true;
      } catch (error) {
        console.error('초대 실패:', error);
      } finally {
        this.loadingStore.stopLoading();
      }
    },

    redirectToInvitationList() {
      this.$router.push('/invitation-list');
    },
  },
};
</script>

<style scoped>
.invite-container {
  max-width: 90%; /* 화면의 90%를 넘지 않음 */
  width: 100%;
  height: auto; /* 내용에 따라 높이 조절 */
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex; /* flexbox 적용 */
  flex-direction: column; /* 세로 정렬 */
  align-items: center; /* 중앙 정렬 */
  justify-content: center; /* 중앙 정렬 */
}
.spinner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}
.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #7f56d9;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
@media (min-width: 768px) {
  .invite-container {
    max-width: 600px; /* 데스크톱에서는 고정된 최대 너비 */
  }
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #414651;
  margin-bottom: 20px;
  text-align: center; /* 중앙 정렬 */
}

.form-group {
  width: 100%; /* 부모 요소 너비에 맞게 확장 */
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
  color: #414651;
}

input,
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  color: #414651;
  resize: none; /* textarea 크기 고정 */
}

textarea {
  min-height: 100px; /* 최소 높이 설정 */
}

.btn-primary {
  width: 100%;
  max-width: 300px; /* 버튼 최대 너비 */
  background-color: #7f56d9;
  color: white;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  text-align: center;
}

.btn-primary:hover {
  background-color: #6a48b0;
}
</style>
