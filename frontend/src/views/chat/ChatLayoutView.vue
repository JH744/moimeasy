<template>
  <div class="chat-layout">
    <!-- 채팅방 목록 -->
    <div class="chat-room-list">
      <ChatRoomList @selectRoom="handleSelectRoom" />
    </div>

    <!-- 채팅방 뷰 -->
    <div class="chat-view">
      <div v-if="selectedRoomId">
        <ChatView :key="selectedRoomId" :roomId="selectedRoomId" />
      </div>
      <div v-else class="no-chat-selected">채팅방을 선택하세요.</div>
    </div>
  </div>
</template>

<script>
import ChatRoomList from './ChatRoomListView.vue';
import ChatView from './ChatView.vue';

export default {
  name: 'ChatLayoutView',
  components: { ChatRoomList, ChatView },
  data() {
    return {
      selectedRoomId: null, // 선택된 채팅방 ID
    };
  },
  methods: {
    handleSelectRoom(roomId) {
      this.selectedRoomId = roomId; // 선택된 채팅방 ID 업데이트
    },
  },
};
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 100vh;
}

.chat-room-list {
  flex: 1;
  border-right: 1px solid #ddd;
  overflow-y: auto;
}

.chat-view {
  flex: 2;
  overflow-y: auto;
}

.no-chat-selected {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: 18px;
  color: #888;
}
</style>
