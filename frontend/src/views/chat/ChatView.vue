<template>
  <div class="chat-container">
    <div class="chat-messages" ref="chatContainer">
      <div
        v-for="(message, index) in messages"
        :key="message.id"
        :class="['message', message.sender === loggedInUserId ? 'user' : 'bot']"
      >
        <Avatar
          :icon="
            message.sender === loggedInUserId ? 'pi pi-user' : 'pi pi-android'
          "
          :style="{
            backgroundColor:
              message.sender === loggedInUserId ? '#2196F3' : '#4CAF50',
          }"
          shape="circle"
        />
        <div class="message-content">
          <p>{{ message.content }}</p>
          <small>{{ message.timestamp }}</small>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <InputText
        v-model="newMessage"
        placeholder="Type a message..."
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

export default {
  props: {
    roomId: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const messages = ref([]);
    const newMessage = ref('');
    const showEmojiPicker = ref(false);
    const chatContainer = ref(null);
    const pollingInterval = ref(null);
    const lastMessageId = ref(0);

    const authStore = useAuthStore();
    const loggedInUserId = authStore.user?.userId;

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
      messages,
      newMessage,
      showEmojiPicker,
      chatContainer,
      sendMessage,
      toggleEmojiPicker,
      onSelectEmoji,
      loggedInUserId,
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
</style>
