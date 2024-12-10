<template>
  <div class="chat-container">
    <h2 class="chat-header">WebSocket 채팅</h2>
    <div class="messages">
      <div v-for="message in messages" :key="message.id" class="message">
        <span class="message-text">{{ message.text }}</span>
      </div>
    </div>
    <div class="input-container">
      <input
        v-model="newMessage"
        @keyup.enter="sendMessage"
        class="message-input"
        placeholder="메시지를 입력하세요"
      />
      <button @click="sendMessage" class="send-button">전송</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

const messages = ref([]);
const newMessage = ref('');
let stompClient;

const connectWebSocket = () => {
  // Spring Boot의 WebSocket 엔드포인트로 연결 설정
  const socket = new SockJS('http://localhost:8088/ws-connect');
  stompClient = Stomp.over(socket);
  stompClient.debug = (str) => {
    console.log('STOMP debug: ', str); // 디버깅용 로그
  };

  stompClient.connect(
    {},
    (frame) => {
      console.log('Connected: ' + frame);
      // 백엔드에서 브로커로부터 메시지를 구독
      stompClient.subscribe('/subscribe/chat', (message) => {
        const receivedMessage = JSON.parse(message.body);
        messages.value.push({ id: Date.now(), text: receivedMessage.content });
      });
    },
    (error) => {
      console.error('WebSocket 연결 실패: ', error);
    }
  );
};

const sendMessage = () => {
  if (newMessage.value.trim() !== '' && stompClient && stompClient.connected) {
    const messagePayload = {
      chatRoomId: 1,
      senderId: 1,
      content: newMessage.value,
      messageType: 'CHAT',
    };
    // 메시지 전송 경로에 맞춰 메시지를 전송
    stompClient.send(
      '/publish/chat.sendMessage',
      JSON.stringify(messagePayload),
      {}
    );
    newMessage.value = '';
  } else {
    console.error(
      'WebSocket 연결이 유효하지 않습니다. 메시지를 전송할 수 없습니다.'
    );
  }
};

onMounted(() => {
  connectWebSocket();
});

onUnmounted(() => {
  if (stompClient) {
    stompClient.disconnect();
  }
});
</script>

<style scoped>
.chat-container {
  width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 2px solid #7f56d9;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.chat-header {
  font-size: 1.5em;
  color: #414651;
  text-align: center;
  margin-bottom: 15px;
}

.messages {
  height: 300px;
  overflow-y: auto;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #7f56d9;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.message {
  margin-bottom: 10px;
}

.message-text {
  color: #414651;
}

.input-container {
  display: flex;
}

.message-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #7f56d9;
  border-radius: 5px;
  margin-right: 10px;
  outline: none;
}

.send-button {
  padding: 10px;
  background-color: #7f56d9;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.send-button:hover {
  background-color: #6b46c1;
}
</style>
