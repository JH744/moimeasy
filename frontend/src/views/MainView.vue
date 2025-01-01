<template>
  <div class="main-page-container">
    <div class="main-page-wrapper">
      <div class="main-page-header">
        <h1>{{ user.nickname }}님 환영합니다. 즐거운 하루보내세요 😃</h1>
      </div>
      <!-- 상단 섹션: 프로필, 달력, 채팅 -->
      <div class="top-section">
        <div class="calendar-section scrollable wide">
          <div class="main-scetion-title">
            <h2>일정캘린더</h2>
            <router-link to="/schedule/calendar">
              <i class="pi pi-ellipsis-v" style="font-size: 1rem"></i>
            </router-link>
          </div>
          <ScheduleCalendarMini />
        </div>
        <div class="chat-section scrollable narrow">
          <div class="main-scetion-title">
            <h2>모임채팅</h2>
            <router-link to="/chat-layout">
              <i class="pi pi-ellipsis-v" style="font-size: 1rem"></i>
            </router-link>
          </div>
          <ChatRoomList @selectRoom="openChatRoom" />
        </div>
      </div>

      <!-- 하단 섹션: 게시판, 카테고리, 정산 -->
      <div class="bottom-section">
        <div class="board-section wide">
          <div class="main-scetion-title">
            <h2>게시판</h2>
            <router-link to="/schedule/board">
              <i class="pi pi-ellipsis-v" style="font-size: 1rem"></i>
            </router-link>
          </div>
          <BoardViewMini />
        </div>
        <div class="category-section">
          <div class="main-scetion-title">
            <h2>모임지출</h2>
            <router-link to="/category">
              <i class="pi pi-ellipsis-v" style="font-size: 1rem"></i>
            </router-link>
          </div>
          <CategoryChart />
        </div>
      </div>
    </div>
    <!-- 채팅방 모달 -->
    <div v-if="isChatModalVisible" class="chat-modal">
      <div class="chat-modal-content">
        <button class="modal-close-btn" @click="closeChatModal">×</button>
        <ChatView :roomId="selectedRoomId" />
      </div>
    </div>
  </div>
</template>

<script>
import UserProfile from '@/views/user/UserProfileView.vue';
import BoardViewMini from '@/views/board/components/BoardViewMini.vue';
import ScheduleCalendarMini from '@/views/schedule/components/ScheduleCalendarMini.vue';
import SettlementView from '@/views/moeim/SettlementView.vue';
import ChatRoomList from '@/views/chat/ChatRoomListView.vue';
import ChatView from '@/views/chat/ChatView.vue';
import { useAuthStore } from '@/stores/auth';
import CategoryChart from './transaction/CategoryChart.vue';
import router from '@/router';

export default {
  name: 'MainPage',
  components: {
    UserProfile,
    BoardViewMini,
    ScheduleCalendarMini,
    SettlementView,
    ChatRoomList,
    ChatView,
    CategoryChart,
  },
  data() {
    return {
      isChatModalVisible: false, // 모달 표시 여부
      selectedRoomId: null, // 선택된 채팅방 ID
    };
  },
  methods: {
    openChatRoom(roomId) {
      this.selectedRoomId = roomId;
      this.isChatModalVisible = true; // 모달 표시
    },
    closeChatModal() {
      this.isChatModalVisible = false;
      this.selectedRoomId = null;
    },
  },
  setup() {
    const authStore = useAuthStore();
    const accessToken = authStore.accessToken;
    const user = authStore.user;

    console.log('Access Token:', accessToken);
    console.log('user :', user);

    return {
      accessToken,
      user,
    };
  },
};
</script>

<style scoped>
.main-page-container {
  background-image: url('../assets/images/pexels-photo-7130555.png');
  background-size: cover;
  height: 100vh;
  width: 100;
  /* 전체 화면 높이 */
}
.main-page-wrapper {
  margin: 0 auto;
  display: flex;
  gap: 10px;
  flex-direction: column;
  padding: 10px;
  height: 100vh;
  width: 1100px;
  /* 전체 화면 높이 */
}
/* 메인 헤더 인삿말 */
.main-page-header {
  padding: 18px 10px;
  background-color: white;
  border-radius: 10px;
  color: rgb(7, 7, 7);
  box-shadow:
    0px 4px 6px rgba(0, 0, 0, 0.1),
    0px 1px 3px rgba(0, 0, 0, 0.06);
  font-size: 1.3rem;
  font-weight: 500;

  /* 헤더 */
}

/* 상단 섹션 */
.top-section,
.bottom-section {
  display: flex;
  gap: 10px;
  flex: 1;
  /* 동일한 높이 */
}

/* 가로 비율 조정 */
.chat-section {
  flex: 1;
  /* 좁은 너비 */
  background-color: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  /* 스크롤 활성화 */
  max-height: 45vh;
}

.calendar-section {
  /* 넓은 너비 */
  background-color: white;
  padding: 15px 15px 5px;
  border-radius: 10px;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  max-height: 45vh;
}

/* 하단 섹션 */
.board-section,
.category-section,
.settlement-section {
  flex: 1;
  background-color: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  max-height: 45vh;
}

/* 스크롤 활성화 */
.scrollable {
  overflow-y: auto;
  height: 100%;
}

/* 섹션 너비 조정 */
.narrow {
  flex: 1;
  /* 좁은 섹션 */
}

.wide {
  flex: 2.5;
  /* 넓은 섹션 */
}

.main-scetion-title {
  display: flex;
  color: rgb(0, 0, 0);
  justify-content: space-between;
  align-items: center;

  h2 {
    font-size: 16px;
    font-weight: 700;
  }
  i {
    color: rgb(120, 120, 120);
    cursor: pointer;
  }
}

.chat-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.chat-modal-content {
  position: relative;
  background: white;
  width: 60%;

  border-radius: 10px;
  padding: 10px;
}

.modal-close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  font-weight: bold;
  color: #aaa;
  cursor: pointer;
}

.modal-close-btn:hover {
  color: #333;
}
</style>
