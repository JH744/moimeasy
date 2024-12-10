<template>
  <div class="chat-room-list">
    <h2>채팅방 목록</h2>
    <ul>
      <li
        v-for="room in rooms"
        :key="room.id"
        @click="enterRoom(room.id)"
        class="room-item"
      >
        {{ room.name }}
      </li>
    </ul>
    <button @click="createRoom" class="btn-primary">채팅방 생성</button>
  </div>
</template>

<script>
import axios from 'axios';

axios.defaults.baseURL = "http://localhost:8088";
axios.defaults.withCredentials = true;

export default {
  data() {
    return {
      rooms: [],
    };
  },
  methods: {
    async fetchRooms() {
      try {
        const response = await axios.get('/api/v1/chat/rooms');
        this.rooms = response.data;
      } catch (error) {
        console.error('Error fetching chat rooms:', error);
      }
    },
    async createRoom() {
      try {
        const roomName = prompt('채팅방 이름을 입력하세요:');
        if (!roomName) return;
        const response = await axios.post('/api/v1/chat/rooms', {
          name: roomName,
        });
        this.rooms.push(response.data);
      } catch (error) {
        console.error('Error creating chat room:', error);
      }
    },
    enterRoom(roomId) {
      this.$router.push({ name: 'ChatView', params: { roomId } });
    },
  },
  mounted() {
    this.fetchRooms();
  },
};
</script>

<style scoped>
.chat-room-list {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: #414651;
}

h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
  margin: 0 0 20px 0;
}

.room-item {
  padding: 10px;
  margin-bottom: 5px;
  border-radius: 5px;
  cursor: pointer;
  background-color: #7f56d9;
  color: white;
  text-align: center;
}

.room-item:hover {
  background-color: rgba(127, 86, 217, 0.8);
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
  background-color: rgba(127, 86, 217, 0.8);
}
</style>
