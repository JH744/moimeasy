<template>
  <div class="list-container">
    <h2 class="title">초대 목록</h2>
    <table class="custom-table">
      <thead>
        <tr>
          <th>이메일</th>
          <th>상태</th>
          <th>초대 날짜</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="invitation in invitations" :key="invitation.id">
          <td>{{ invitation.email }}</td>
          <td>{{ invitation.status }}</td>
          <td>{{ invitation.createdAt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';
import { useAuthStore } from '@/stores/auth'; // 인증 저장소 import (토큰 저장됨)

export default {
  data() {
    return {
      invitations: [], // 초대 데이터를 저장
    };
  },
  methods: {
    async fetchInvitations() {
      const authStore = useAuthStore(); // 인증 저장소 사용
      const token = authStore.accessToken; // 토큰 가져오기

      if (!token) {
        console.warn('인증 토큰이 없습니다. 로그인 후 시도해주세요.');
        return;
      }

      try {
        const response = await axios.get('/api/v1/invitations', {
          headers: {
            Authorization: `Bearer ${token}`, // Authorization 헤더에 토큰 추가
          },
        });

        this.invitations = response.data.map((invitation) => ({
          id: invitation.id,
          email: invitation.email,
          status: invitation.status,
          createdAt: new Date(invitation.createdAt).toLocaleString(),
        }));
      } catch (error) {
        console.error('Error fetching invitations:', error);
        if (error.response?.status === 401) {
          alert('인증에 실패했습니다. 다시 로그인해 주세요.');
        }
      }
    },
  },
  mounted() {
    this.fetchInvitations();
  },
};
</script>

<style scoped>
/* 테이블 스타일 */
.list-container {
  width: 98%;
  min-width: 600px;
  margin: 10px;
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

.custom-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.custom-table th,
.custom-table td {
  padding: 10px;
  border: 1px solid #ddd;
  color: #414651;
}

.custom-table th {
  background-color: #f4f4f4;
  text-align: left;
}
</style>
