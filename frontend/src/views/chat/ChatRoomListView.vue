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
        :key="room.id || room.roomId"
        @click="enterRoom(room.id || room.roomId)"
        class="room-item"
      >
        <div class="room-header">
          <span v-if="room.roomName" class="room-name">{{
            room.roomName
          }}</span>
          <span v-else class="room-name">
            {{
              Array.isArray(room.memberNicknames) &&
              room.memberNicknames.length > 0
                ? room.memberNicknames.join(', ')
                : '참여자 없음'
            }}
          </span>

          <div class="dropdown">
            <button @click="toggleDropdown(room.id)" class="dropdown-btn">
              +
            </button>
            <div
              class="dropdown-menu"
              :class="{ show: dropdownOpen === room.id }"
            >
              <button @click="openInviteModal(room.id)">회원 초대</button>
              <button @click="leaveRoom(room.id)">나가기</button>
            </div>
          </div>
        </div>
        <hr />
        <p class="room-participants">
          참여자:
          {{
            Array.isArray(room.memberNicknames) &&
            room.memberNicknames.length > 0
              ? room.memberNicknames.join(', ')
              : '참여자 없음'
          }}
        </p>
      </li>
    </ul>

    <!-- 초대 모달 -->
    <Dialog
      header="회원 초대"
      :visible="isInviteModalOpen"
      style="width: 50vw"
      :modal="true"
      :closable="false"
      @hide="closeInviteModal"
    >
      <div class="modal-content">
        <h4>초대 가능한 회원</h4>
        <div class="members-list">
          <ul>
            <li
              v-for="member in availableMembers"
              :key="member.userId"
              class="member-item"
            >
              <Checkbox
                :value="member.userId"
                v-model="selectedMembers"
                class="member-checkbox"
              />
              <label>{{ member.nickname }}</label>
            </li>
          </ul>
        </div>

        <div class="modal-buttons">
          <Button
            @click="sendInvitations"
            label="초대하기"
            class="p-button-success"
          />
          <Button
            @click="closeInviteModal"
            label="취소"
            class="p-button-secondary"
          />
        </div>
      </div>
    </Dialog>
    <!-- 채팅방 생성 모달 -->
    <Dialog
      header="채팅방 생성"
      :visible="isCreateRoomModalOpen"
      style="width: 50vw"
      :modal="true"
      :closable="false"
      @hide="closeCreateRoomModal"
    >
      <div class="modal-content">
        <div class="form-group">
          <label for="roomName">채팅방 이름</label>
          <InputText
            v-model="newRoomName"
            id="roomName"
            placeholder="채팅방 이름을 입력하세요"
            class="p-inputtext"
            style="width: 100%; margin-bottom: 10px"
          />
        </div>

        <h4>참여자 선택</h4>
        <div class="members-list">
          <ul>
            <li v-for="user in members" :key="user.userId" class="member-item">
              <Checkbox
                :value="user.userId"
                v-model="selectedMembers"
                class="member-checkbox"
              />
              <label>{{ user.nickname }}</label>
            </li>
          </ul>
        </div>

        <div class="selected-members">
          <h4>선택된 회원</h4>
          <p v-if="selectedMembers.length === 0">선택된 회원이 없습니다.</p>
          <ul v-else>
            <li v-for="id in selectedMembers" :key="id">
              {{
                members.find((member) => member.userId === id)?.nickname ||
                '알 수 없는 사용자'
              }}
            </li>
          </ul>
        </div>

        <div class="modal-buttons">
          <Button @click="createRoom" label="생성" class="p-button-success" />
          <Button
            @click="closeCreateRoomModal"
            label="취소"
            class="p-button-secondary"
          />
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';
import InputText from 'primevue/inputtext';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Checkbox from 'primevue/checkbox';

export default {
  name: 'ChatRoomListView',
  components: { InputText, Button, Checkbox, Dialog },
  data() {
    return {
      rooms: [],
      members: [],
      isCreateRoomModalOpen: false,
      newRoomName: '',
      selectedMembers: [],
      dropdownOpen: null,
      availableMembers: [],
      isInviteModalOpen: false,
      currentRoomId: null,
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

        const response = await axios.get(`/api/v1/chat/rooms`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: { userId },
        });

        console.log('Rooms API response:', response.data);
        this.rooms = response.data.map((room) => ({
          ...room,
          members: room.memberNicknames || [],
        }));
      } catch (error) {
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

        const response = await axios.get('/api/v1/users/members', {
          params: { moeimId },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const currentUserId = this.authStore.user.userId;
        this.members = response.data.filter(
          (member) => member.userId !== currentUserId
        );
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
        const token = this.authStore.accessToken;
        const payload = {
          roomName: this.newRoomName || '',
          memberIds: this.selectedMembers,
        };

        const response = await axios.post('/api/v1/chat/room', payload, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.rooms.push(response.data);
        await this.fetchRooms();
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
      this.$emit('selectRoom', roomId);
    },
    toggleDropdown(roomId) {
      // 클릭한 드롭다운이 열리거나 닫힙니다.
      this.dropdownOpen = this.dropdownOpen === roomId ? null : roomId;
    },
    async fetchAvailableMembers(roomId) {
      try {
        const token = this.authStore.accessToken;
        const moeimId = this.authStore.user?.moeimId;

        if (!moeimId) {
          console.error('moeimId is not available in authStore.');
          return;
        }

        const response = await axios.get('/api/v1/users/members', {
          params: { moeimId },
          headers: { Authorization: `Bearer ${token}` },
        });

        const room = this.rooms.find((room) => room.id === roomId);

        // 채팅방에 없는 멤버 필터링
        this.availableMembers = response.data.filter(
          (member) => !room.members.includes(member.nickname)
        );
      } catch (error) {
        console.error('Error fetching available members:', error);
      }
    },
    openInviteModal(roomId) {
      this.currentRoomId = roomId;
      this.fetchAvailableMembers(roomId);
      this.isInviteModalOpen = true;
    },
    closeInviteModal() {
      this.isInviteModalOpen = false;
      this.selectedMembers = [];
      this.currentRoomId = null;
    },
    async sendInvitations() {
      try {
        const token = this.authStore.accessToken;
        const roomId = this.currentRoomId;

        const payload = this.selectedMembers; // 선택된 사용자 ID 배열
        await axios.post(`/api/v1/chat/room/${roomId}/invite`, payload, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        this.closeInviteModal();
        this.dropdownOpen = null;
        await this.fetchRooms(); // 채팅방 목록 갱신
      } catch (error) {
        console.error('Error sending invitations:', error);
        alert('회원 초대 중 오류가 발생했습니다.');
      }
    },
    async leaveRoom(roomId) {
      try {
        const token = this.authStore.accessToken;
        await axios.delete(`/api/v1/chat/room/${roomId}/leave`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        await this.fetchRooms(); // 채팅방 목록 갱신
      } catch (error) {
        console.error('Error leaving room:', error);
        alert('채팅방 나가기 중 오류가 발생했습니다.');
      }
    },
  },
  mounted() {
    if (this.authStore.user?.moeimId) {
      this.fetchMembers();
    } else {
      console.error('moeimId is not available in authStore.');
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
.room-header {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
}

.room-participants {
  font-size: 14px;
  color: #777;
}
.options-dropdown-container {
  position: absolute;
}
.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.dropdown-menu {
  display: none;
  position: absolute;
  right: 0;
  top: 100%;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 150px;
}

.dropdown-menu.show {
  display: block;
}

.dropdown-menu button {
  display: block;
  width: 100%;
  background: none;
  border: none;
  text-align: left;
  padding: 10px;
  cursor: pointer;
}

.dropdown-menu button:hover {
  background-color: #f1f1f1;
  color: #7f56d9;
}
</style>
