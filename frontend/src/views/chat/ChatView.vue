<template>
  <div class="chat-container">
    <div class="chat-messages" ref="chatContainer">
      <div
        v-for="(message, index) in messages"
        :key="message.id"
        :class="[
          'message',
          message.senderId === loggedInUserId ? 'user' : 'bot',
        ]"
      >
        <Avatar
          :icon="'pi pi-user'"
          :style="{
            backgroundColor: getAvatarColor(message.senderId),
          }"
          shape="circle"
        />
        <div class="message-content">
          <!-- 닉네임 표시 -->
          <strong>{{ message.senderNickname }}</strong>

          <!-- TEXT 메시지 -->
          <p v-if="message.messageType === 'TEXT' || !message.messageType">
            {{ message.content }}
          </p>

          <!-- IMAGE 메시지 -->
          <img
            v-else-if="message.messageType === 'IMAGE'"
            :src="message.fileUrl || message.content"
            alt="Image"
            class="chat-media"
          />

          <!-- VIDEO 메시지 -->
          <video
            v-else-if="message.messageType === 'VIDEO'"
            :src="message.fileUrl || message.content"
            controls
            class="chat-media"
          ></video>

          <!-- 알 수 없는 메시지 타입 -->
          <p v-else>Unsupported message type: {{ message.messageType }}</p>

          <small>{{ formatTimestamp(message.timestamp) }}</small>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <input
        type="file"
        ref="fileInput"
        @change="handleFileSelect"
        style="display: none"
      />
      <Button
        icon="pi pi-plus"
        class="btn-add-file"
        @click="triggerFileUpload"
      />
      <InputText
        v-model="newMessage"
        placeholder="메세지를 작성하세요"
        aria-label="Message Input"
        @keyup.enter="sendMessage"
      />
      <Button
        icon="pi pi-send"
        aria-label="Send Message"
        @click="sendMessage"
      />
      <Button
        aria-label="Open Emoji Picker"
        class="emoji-button"
        @click="toggleEmojiPicker"
        >😉</Button
      >
      <div v-if="showEmojiPicker" class="emoji-picker-container">
        <EmojiPicker :native="true" @select="onSelectEmoji" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import Avatar from 'primevue/avatar';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import EmojiPicker from 'vue3-emoji-picker';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';
import 'vue3-emoji-picker/css';
import {
  ref as firebaseRef,
  uploadBytes,
  getDownloadURL,
} from 'firebase/storage';
import { firebaseStorage } from '@/firebase/firebaseConfig';

export default {
  props: {
    components: {
      EmojiPicker, // 로컬 등록
    },
    roomId: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const fileInput = ref(null);
    const selectedFile = ref(null);
    const messages = ref([]);
    const newMessage = ref('');
    const showEmojiPicker = ref(false);
    const chatContainer = ref(null);
    const pollingInterval = ref(null);
    const lastMessageId = ref(0);

    const authStore = useAuthStore();
    const loggedInUserId = authStore.user?.userId;

    //이미지, 비디오 업로드 const selectedFile = ref(null);

    const triggerFileUpload = () => {
      fileInput.value.click();
    };

    const handleFileSelect = async (event) => {
      selectedFile.value = event.target.files[0];
      if (!selectedFile.value) return;

      // 파일 크기 제한 (1MB 이하)
      const maxFileSize = 1 * 1024 * 1024; // 1MB
      if (selectedFile.value.size > maxFileSize) {
        alert('파일 크기는 1MB를 초과할 수 없습니다.');
        return;
      }

      // Firebase에 파일 업로드
      const file = selectedFile.value;
      const storagePath = `chatFiles/${file.name}`;
      const storageRef = firebaseRef(firebaseStorage, storagePath);

      try {
        await uploadBytes(storageRef, file);
        const fileUrl = await getDownloadURL(storageRef);
        console.log('File uploaded to:', fileUrl);

        // 파일 URL을 메시지로 전송
        await sendFileMessage(
          fileUrl,
          file.type.includes('video') ? 'VIDEO' : 'IMAGE'
        );
      } catch (error) {
        console.error('File upload failed:', error);
      }
    };

    const sendFileMessage = async (fileUrl, messageType) => {
      const payload = {
        chatRoomId: props.roomId,
        senderId: loggedInUserId,
        content: fileUrl,
        fileUrl: fileUrl,
        messageType,
      };

      try {
        const token = localStorage.getItem('accessToken');
        await axios.post('/api/v1/chat/message', payload, {
          headers: { Authorization: `Bearer ${token}` },
        });
        fetchMessages(); // 메시지 목록 갱신
      } catch (error) {
        console.error('Error sending file message:', error);
      }
    };

    //채팅아바타
    const getAvatarColor = (userId) => {
      const colors = [
        '#6b48c6',
        '#c3aff8',
        '#7350cd',
        '#b9a0f4',
        '#7f56d9',
        '#af91f0',
        '#8c67e0',
        '#a483eb',
        '#9875e6',
        '#d1c2fb',
      ];
      const index = Math.abs(hashCode(userId)) % colors.length;
      return colors[index];
    };

    // 해시 함수: 사용자 ID를 정수값으로 변환
    const hashCode = (str) => {
      let hash = 0;
      const stringified = String(str); // 문자열로 변환
      for (let i = 0; i < stringified.length; i++) {
        const char = stringified.charCodeAt(i);
        hash = (hash << 5) - hash + char;
        hash &= hash; // 정수값 유지
      }
      return hash;
    };
    //채팅 날짜
    const formatTimestamp = (timestamp) => {
      const date = new Date(timestamp);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${month}.${day} ${hours}:${minutes}`;
    };

    const fetchMessages = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const response = await axios.get(
          `/api/v1/chat/rooms/${props.roomId}/poll-messages`,
          {
            params: { lastMessageId: lastMessageId.value },
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        if (response.data.length > 0) {
          messages.value.push(...response.data);
          lastMessageId.value = response.data[response.data.length - 1].id;
          scrollToBottom();
        }
      } catch (error) {
        console.error('Error fetching messages:', error);
      }
    };

    const sendMessage = async () => {
      if (!newMessage.value.trim()) {
        newMessage.value = '';
        return;
      }

      const payload = {
        chatRoomId: props.roomId,
        senderId: loggedInUserId,
        content: newMessage.value,
        messageType: 'TEXT',
      };

      try {
        const token = localStorage.getItem('accessToken');
        await axios.post('/api/v1/chat/message', payload, {
          headers: { Authorization: `Bearer ${token}` },
        });
        newMessage.value = '';
        fetchMessages();
      } catch (error) {
        console.error('Error sending message:', error);
      }
    };

    const toggleEmojiPicker = () => {
      showEmojiPicker.value = !showEmojiPicker.value;
    };

    const onSelectEmoji = (emoji) => {
      newMessage.value += emoji.i;
      showEmojiPicker.value = false;
    };

    const scrollToBottom = () => {
      nextTick(() => {
        if (chatContainer.value) {
          chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
        }
      });
    };

    const startPolling = () => {
      pollingInterval.value = setInterval(fetchMessages, 1000);
    };

    const stopPolling = () => {
      clearInterval(pollingInterval.value);
    };

    // Lifecycle hooks
    onMounted(() => {
      console.log('ChatView loaded with roomId:', props.roomId);
      fetchMessages();
      startPolling();
      scrollToBottom();
    });

    onBeforeUnmount(() => {
      stopPolling();
    });

    watch(
      () => props.roomId,
      (newRoomId) => {
        console.log('Room changed:', newRoomId);
        fetchMessages();
      }
    );

    return {
      formatTimestamp,
      getAvatarColor,
      messages,
      newMessage,
      showEmojiPicker,
      chatContainer,
      sendMessage,
      toggleEmojiPicker,
      onSelectEmoji,
      loggedInUserId,
      triggerFileUpload,
      handleFileSelect,
      fileInput,
    };
  },
};
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;
  width: 60%;
  margin: 35px auto;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f9f9f9;
}

.chat-messages {
  flex: 1;
  overflow: auto;
  padding: 20px;
  background-color: #f0f0f0;
}

.message {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
  padding: 10px;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin: 0 10px;
}

.user .message-content {
  background-color: #e3f2fd;
}

.message-content p {
  margin: 0 0 5px 0;
}

.message-content small {
  font-size: 0.8em;
  color: #888;
}

.chat-input {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #fff;
  border-top: 1px solid #ccc;
}

.chat-input .p-inputtext {
  flex: 1;
  margin-right: 10px;
}

.p-avatar {
  width: 32px;
  height: 32px;
  color: #ccc;
}
.btn-add-file {
  margin-right: 10px;
}
.emoji-button {
  margin-left: 10px;
}

.emoji-picker-container {
  position: absolute;
  bottom: 60px;
  right: 10px;
  z-index: 10;
  background-color: white;
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.15);
  border-radius: 10px;
  padding: 10px;
}
.chat-media {
  max-width: 300px;
  border-radius: 8px;
  margin-top: 5px;
}
</style>
