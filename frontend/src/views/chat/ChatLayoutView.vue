<template>
  <div class="chat-layout">
    <div class="chat-room-list">
      <ChatRoomList @selectRoom="handleSelectRoom" />
    </div>
    <div class="chat-view">
      <ChatView :roomId="selectedRoomId" />
    </div>
  </div>
</template>

<script>
import ChatRoomList from './ChatRoomListView.vue';
import ChatView from './ChatView.vue';

export default {
  name: 'ChatLayout',
  data() {
    return {
      selectedRoomId: null, // 선택된 채팅방 ID
    };
  },
  methods: {
    handleSelectRoom(roomId) {
      this.selectedRoomId = roomId; // 채팅방 ID 변경
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
  flex: 1; /* 왼쪽 목록 */
  border-right: 1px solid #ddd;
  overflow-y: auto;
}

.chat-view {
  flex: 2; /* 오른쪽 대화창 */
  overflow-y: auto;
}
</style>
