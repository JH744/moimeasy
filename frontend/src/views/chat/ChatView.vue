<template>
  <div class="chat-view">
    <h2>{{ roomName }}</h2>
    <div class="messages">
      <div v-for="message in messages" :key="message.id" class="message-item">
        <strong>{{ message.sender }}:</strong> {{ message.content }}
      </div>
    </div>
    <div class="input-container">
      <input
        v-model="newMessage"
        placeholder="메시지를 입력하세요"
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage" class="btn-primary">전송</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      roomName: '',
      messages: [],
      newMessage: '',
    };
  },
  methods: {
    async fetchMessages() {
      try {
        const response = await axios.get(
          `/api/v1/chat/rooms/${this.$route.params.roomId}/messages`
        );
        this.messages = response.data;
      } catch (error) {
        console.error('Error fetching messages:', error);
      }
    },
    async sendMessage() {
      try {
        const payload = { content: this.newMessage };
        await axios.post(
          `/api/v1/chat/rooms/${this.$route.params.roomId}/messages`,
          payload
        );
        this.messages.push({ sender: 'You', content: this.newMessage });
        this.newMessage = '';
      } catch (error) {
        console.error('Error sending message:', error);
      }
    },
  },
  mounted() {
    this.roomName = `채팅방 #${this.$route.params.roomId}`;
    this.fetchMessages();
  },
};
</script>

<style scoped>
.chat-view {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  color: #414651;
}

.messages {
  margin-bottom: 20px;
  max-height: 400px;
  overflow-y: auto;
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ddd;
}

.message-item {
  margin-bottom: 10px;
}

.input-container {
  display: flex;
  gap: 10px;
}

input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.btn-primary {
  background-color: #7f56d9;
  color: white;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  border: none;
}

.btn-primary:hover {
  background-color: rgba(127, 86, 217, 0.8);
}
</style>
