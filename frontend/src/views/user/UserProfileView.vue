<template>
  <div class="profile-container">
    
    <form v-if="user">
      <div class="form-group">
        <label for="userProfile">프로필이미지</label>
        <div class="profile-box">
          <Avatar
            :icon="src ? undefined : 'pi pi-user'"
            :image="src || undefined"
            class="mr-2"
            size="xlarge"
            shape="circle"
          />
          <FileUpload
            mode="basic"
            @select="onFileSelect"
            customUpload
            auto
            severity="secondary"
            class="p-button-outlined"
          />
        </div>

        <!-- <input id="username" v-model="user.userName" type="text" readonly /> -->
      </div>
      <div class="form-group">
        <label for="username">유저명</label>
        <input id="username" v-model="user.userName" type="text" readonly />
      </div>
      <div class="form-group">
        <label for="email">이메일</label>
        <input id="email" v-model="user.email" type="email" readonly />
      </div>
      <div class="form-group">
        <label for="phone">전화번호</label>
        <input id="phone" v-model="user.phone" type="text" readonly />
      </div>
      <div class="form-group">
        <label for="address">주소</label>
        <input id="address" v-model="user.address" type="text" readonly />
      </div>
      <div class="form-group">
        <label for="createAt">가입 날짜</label>
        <input id="createAt" v-model="user.createAt" type="text" readonly />
      </div>
    </form>
    <div v-else>사용자 정보를 불러오는 중입니다...</div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      user: null,
      src: null,
    };
  },
  mounted() {
    const userId = this.$route.params.userId; // URL에서 userId 가져오기
    this.loadUserData(userId);
  },
  methods: {
    async loadUserData(userId) {
      try {
        const response = await axios.get(`/api/v1/users/${userId}`); // API 호출
        this.user = response.data; // 응답 데이터를 user에 저장
      } catch (error) {
        console.error('사용자 정보를 불러오는데 실패했습니다:', error);
        alert('사용자 정보를 불러오는데 실패했습니다.');
      }
    },
    onFileSelect(event) {
      const file = event.files[0];
      const reader = new FileReader();
      reader.onload = async (e) => {
        this.src = e.target.result;
      };
      reader.readAsDataURL(file);
    },
  },
};
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #414651; /* 폰트 색상 */
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #414651; /* 폰트 색상 */
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  color: #414651; /* 폰트 색상 */
}

input[readonly] {
  background-color: #f9f9f9;
}

.p-avatar {
  width: 100px;
  height: 100px;
}
.profile-box {
  display: flex;
  flex-direction: column;
}
</style>
