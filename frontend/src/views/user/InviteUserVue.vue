<template>
  <div class="invite-container">
    <h2>회원 초대</h2>
    <form @submit.prevent="sendInvitation">
      <div class="form-group">
        <label for="moeimCode">모임 코드</label>
        <input
          id="moeimCode"
          v-model="form.moeimId"
          type="Long"
          placeholder="모임 코드를 입력하세요"
          required
        />
      </div>
      <div class="form-group">
        <label for="email">이메일</label>
        <input
          id="email"
          v-model="form.email"
          type="email"
          placeholder="초대할 이메일을 입력하세요"
          required
        />
      </div>
      <div class="form-group">
        <label for="message">초대 메시지</label>
        <textarea
          id="message"
          v-model="form.message"
          placeholder="초대 메시지를 입력하세요"
          required
        ></textarea>
      </div>
      <button type="submit" class="btn-submit">초대 전송</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      form: {
        moeimId: 1,
        email: "example@mail.com",
        message: "모임에 초대합니다!",
      },
    };
  },
  methods: {
    async sendInvitation() {
      try {
        const response = await axios.post("/api/v1/invitations/send", this.form);
        alert(response.data);
        this.$router.push({ name: "UserManage" }); // 초대 후 회원 관리 페이지로 이동
      } catch (error) {
        console.error(error);
        alert("초대 전송에 실패했습니다.");
      }
    },
  },
};
</script>

<style scoped>
.invite-container {
  width: 400px;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
h2 {
  text-align: center;
  color: #414651;
  font-weight: bold;
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
input,
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.btn-submit {
  width: 100%;
  padding: 10px;
  background-color: #7f56d9;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.btn-submit:hover {
  background-color: #6a48b0;
}
</style>
