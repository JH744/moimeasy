<template>
  <div class="chat-room-list">
    <div class="header">
      <h2>채팅방 목록</h2>
      <button @click="openCreateRoomModal" class="btn-primary">
        채팅방 생성
      </button>
    </div>
    <ul>
      <li
        v-for="room in rooms"
        :key="room.id"
        @click="enterRoom(room.id)"
        class="room-item"
      >
        <span class="room-name">{{ room.name }}</span>
        <hr />
        <p>
          참여자:
          {{
            room.members
              ? room.members.map((member) => member.userNickname).join(', ')
              : '참여자 없음'
          }}
        </p>
      </li>
    </ul>
    <!-- 채팅방 생성 모달 -->
    <div v-if="isCreateRoomModalOpen" class="modal">
      <div class="modal-content">
        <h2>채팅방 생성</h2>
        <h3>채팅방 이름</h3>
        <input v-model="newRoomName" id="roomName" placeholder="채팅방 이름" />

        <h4>참여자 선택</h4>
        <div class="members-list">
          <ul>
            <li v-for="user in members" :key="user.id">
              <label>
                <input
                  type="checkbox"
                  :value="user.id"
                  v-model="selectedMembers"
                />
                {{ user.nickname }}
              </label>
            </li>
          </ul>
        </div>

        <div class="selected-members">
          <h4>선택된 회원</h4>
          <ul>
            <li v-for="id in selectedMembers" :key="id">
              {{
                members.find((member) => member.id === id)?.nickname ||
                '알 수 없는 사용자'
              }}
            </li>
          </ul>
        </div>

        <div class="modal-buttons">
          <button @click="createRoom" class="btn-primary">생성</button>
          <button @click="closeCreateRoomModal" class="btn-secondary">
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';

export default {
  name: 'ChatRoomListView',
  data() {
    return {
      rooms: [],
      members: [],
      isCreateRoomModalOpen: false,
      newRoomName: '',
      selectedMembers: [],
    };
  },
  setup() {
    const authStore = useAuthStore();
    return { authStore };
  },
  methods: {
    async fetchRooms() {
      try {
        const token = this.authStore.accessToken;
        const userId = this.authStore.user?.userId;

        if (!userId) {
          console.error('User ID is not available in authStore.');
          return;
        }

        console.log('Fetching rooms for userId:', userId);

        const response = await axios.get(`/api/v1/chat/rooms`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: { userId }, // userId를 쿼리 파라미터로 전달
        });

        this.rooms = response.data;
      } catch (error) {
        if (error.response?.status === 401) {
          try {
            await this.authStore.refreshAccessToken();
            return this.fetchRooms(); // 토큰 갱신 후 다시 요청
          } catch (refreshError) {
            this.authStore.logout();
            return;
          }
        }
        console.error('Error fetching chat rooms:', error);
      }
    },
    async fetchMembers() {
      try {
        const token = this.authStore.accessToken;
        const moeimId = this.authStore.user?.moeimId;

        if (!moeimId) {
          console.error('moeimId is not available in authStore.');
          return;
        }

        console.log('Fetching members with moeimId:', moeimId);

        const response = await axios.get('/api/v1/users', {
          params: { moeimId },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.members = response.data;
      } catch (error) {
        console.error('Error fetching members:', error);
      }
    },
    openCreateRoomModal() {
      this.isCreateRoomModalOpen = true;
    },
    closeCreateRoomModal() {
      this.isCreateRoomModalOpen = false;
      this.newRoomName = '';
      this.selectedMembers = [];
    },
    async createRoom() {
      try {
        const token = this.authStore.token;
        const payload = {
          roomName: this.newRoomName || '',
          createdBy: this.authStore.user.id, // 현재 사용자 ID
          memberIds: this.selectedMembers,
        };
        const response = await axios.post('/api/v1/chat/room', payload, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        this.rooms.push(response.data);
        this.closeCreateRoomModal();
      } catch (error) {
        console.error('Error creating chat room:', error);
      }
    },
    enterRoom(roomId) {
      if (!roomId) {
        console.error('Room ID is not defined.');
        return;
      }
      console.log('Navigating to ChatView with roomId:', roomId);
      this.$router.push({
        name: 'ChatView',
        params: { roomId: String(roomId) },
      });
    },
  },
  mounted() {
    console.log('AuthStore user:', this.authStore.user);

    if (this.authStore.user?.moiemId) {
      this.fetchMembers();
    } else {
      console.error('moiemId is not available in authStore.');
    }

    if (this.authStore.user?.userId) {
      this.fetchRooms();
    } else {
      console.error('userId is not available in authStore.');
    }
  },
};
</script>

<style scoped>
.chat-room-list {
  max-width: 700px;
  margin: 50px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: #414651;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h2 {
  font-size: 24px;
  font-weight: bold;
}
h3,
h4 {
  font-size: 18px;
}

ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.room-item {
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
  cursor: pointer;
  background-color: #f9f9f9;
  text-align: left;
  border: 1px solid #ddd;
}

.room-item:hover {
  background-color: #f1f1f1;
}

.room-name {
  font-size: 18px;
  font-weight: bold;
  color: #414651;
}

.room-users {
  font-size: 14px;
  color: #777;
}

hr {
  border: none;
  border-top: 1px solid #ddd;
  margin: 8px 0;
}

.btn-primary {
  background-color: #7f56d9;
  color: white;
  padding: 10px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.btn-primary:hover {
  background-color: rgba(127, 86, 217, 0.8);
}

.btn-secondary {
  background-color: #ddd;
  color: #333;
  padding: 10px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.btn-secondary:hover {
  background-color: #bbb;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  max-width: 500px;
  width: 100%;
}

.members-list {
  max-height: 150px;
  overflow-y: scroll;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 5px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}
</style>
