<template>
  <div class="chat-container">
    <div class="chat-messages" ref="chatContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message', message.sender]"
      >
        <Avatar
          :icon="message.sender === 'user' ? 'pi pi-user' : 'pi pi-android'"
          :style="{
            backgroundColor: message.sender === 'user' ? '#2196F3' : '#4CAF50',
          }"
          shape="circle"
        />
        <div class="message-content">
          <p>{{ message.text }}</p>
          <small>{{ message.time }}</small>
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

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import Avatar from 'primevue/avatar';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import EmojiPicker from 'vue3-emoji-picker';
import 'vue3-emoji-picker/css';

// Messages list
const messages = ref([
  { sender: 'bot', text: 'Hello! How can I help you today?', time: '10:00 AM' },
  {
    sender: 'user',
    text: 'Hi! I have a question about your services.',
    time: '10:01 AM',
  },
  {
    sender: 'bot',
    text: "Sure, I'd be happy to help. What would you like to know?",
    time: '10:02 AM',
  },
]);

// Input and emoji picker states
const newMessage = ref('');
const showEmojiPicker = ref(false);

// Chat container reference for scrolling
const chatContainer = ref(null);

const sendMessage = () => {
  if (!newMessage.value.trim()) {
    newMessage.value = '';
    return;
  }

  messages.value.push({
    sender: 'user',
    text: newMessage.value,
    time: new Date().toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
      hourCycle: 'h23',
    }),
  });

  newMessage.value = '';
  scrollToBottom();

  // Simulate bot response
  setTimeout(() => {
    messages.value.push({
      sender: 'bot',
      text: 'Thank you for your message. How else can I assist you?',
      time: new Date().toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
        hourCycle: 'h23',
      }),
    });
    scrollToBottom();
  }, 1000);
};

const toggleEmojiPicker = (event) => {
  showEmojiPicker.value = !showEmojiPicker.value;
};

function onSelectEmoji(emoji) {
  console.log(emoji);
  newMessage.value += emoji.i;
  showEmojiPicker.value = false; // 이모지 선택 후 닫기
  /*
    // result
    { 
        i: "😚", 
        n: ["kissing face"], 
        r: "1f61a", // with skin tone
        t: "neutral", // skin tone
        u: "1f61a" // without tone
    }
    */
}

const stopPropagation = (event) => {
  event.stopPropagation();
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
  });
};

onMounted(() => {
  scrollToBottom();
});
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
