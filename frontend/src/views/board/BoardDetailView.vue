<template>
  <div class="board-detail" v-if="board">
    <h1 class="title" :aria-label="board.title">{{ board.title }}</h1>

    <!-- 작성자 -->
    <div class="author">
      <!-- Avatar를 사용하여 이미지 표시 -->
      <Avatar
        :image="profileImage || defaultProfileImage"
        shape="circle"
        size="large"
        style="margin-right: 8px"
      />
      {{ board.writerName || '익명' }}
    </div>
    <!-- 태그 -->
    <div class="tags" v-if="board.tag">
      <Tag :value="board.tag"></Tag>
    </div>
    <!-- 내용 -->
    <div class="content" v-html="board.content || '내용이 없습니다.'"></div>
    <!-- 수정&삭제 버튼 (작성자에게만 표시)-->
    <div class="board-detail-btn-box" v-if="nickname == board.writerName">
      <Toast />
      <ConfirmPopup></ConfirmPopup>
      <Button
        label="수정"
        variant="outlined"
        icon="pi pi-pen-to-square"
        @click="linkToUpdate"
      />
      <Button
        label="삭제"
        variant="outlined"
        icon="pi pi-trash"
        @click="deleteConfirm($event)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import Avatar from 'primevue/avatar'; // Avatar 컴포넌트 import
import 'primeicons/primeicons.css';
import Tag from 'primevue/tag';
import api from '@/axios';
import 'quill/dist/quill.snow.css';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';
import { useConfirm } from 'primevue/useconfirm';
import { useToast } from 'primevue/usetoast';
import ConfirmPopup from 'primevue/confirmpopup';
import { useRouter } from 'vue-router';
import { useLoadingStore } from '@/stores/useLoadingStore';
import { fetchImageUrl } from '@/utils/image-load-utils';
const loadingStore = useLoadingStore();

const router = useRouter();
const toast = useToast();
const confirm = useConfirm();
const auth = useAuthStore();
const route = useRoute();

const nickname = auth.user.nickname;
const profileImage = ref('');
const boardId = route.params.id; // 라우터의 id 파라미터
const board = ref(null);
const defaultProfileImage =
  'https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png';

// 게시글 데이터를 불러오는 함수
const fetchBoardData = async () => {
  loadingStore.startLoading(); // 로딩 시작
  try {
    const response = await api.get(`/api/v1/boards/${boardId}`);
    board.value = response.data; // API 결과 저장
    profileImage.value = await fetchImageUrl(response.data.profileImage); //파베에서 읽어올 수 있도록 url 변환
    console.log('변환된 프로필url:', profileImage.value);
  } catch (error) {
    console.error('게시글 데이터를 불러오지 못했습니다.', error);
  } finally {
    loadingStore.stopLoading(); // 로딩 중지
  }
};

const linkToUpdate = () => {
  router.push(`/schedule/board-update/${boardId}`);
};

//삭제 api
const fetchDelete = async () => {
  try {
    const response = await axios.delete(`/api/v1/boards/${boardId}`);
    router.push('/schedule/board');
    toast.add({
      severity: 'info',
      summary: '삭제 완료',
      detail: '게시글을 삭제했습니다.',
      life: 3000,
    });
  } catch (error) {
    console.error('게시글 삭제 실패', error);
    toast.add({
      severity: 'error',
      summary: '삭제 실패',
      detail: '게시글 삭제에 실패했습니다.',
      life: 3000,
    });
  }
};

//삭제 확인창
const deleteConfirm = (event) => {
  confirm.require({
    target: event.currentTarget,
    message: '이 게시물을 삭제하시겠습니까?',
    icon: 'pi pi-info-circle',
    rejectProps: {
      label: '취소',
      severity: 'secondary',
      outlined: true,
    },
    acceptProps: {
      label: '삭제',
      severity: 'danger',
    },
    accept: () => {
      fetchDelete(); // 확인 시 삭제 요청
    },
    reject: () => {},
  });
};

onMounted(() => {
  fetchBoardData();
});
</script>

<style>
.board-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.title {
  font-size: 24px;
  color: #333;
  margin-bottom: 16px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.content-card {
  margin-bottom: 24px;
}

.content-header {
  display: flex;
  align-items: center;
  color: #2c3e50;
}

.content-card .p-card-content {
  padding-top: 1rem;
}

.board-detail .content {
  line-height: 1.8;
  color: #34495e;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.board-detail .content h1,
.board-detail .content h2,
.board-detail .content h3 {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  color: #2c3e50;
}
/* 제목 스타일 */
.board-detail .content h1 {
  font-size: 2em;
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: bold;
  line-height: 1.2;
}

.board-detail .content h2 {
  font-size: 1.75em;
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: bold;
  line-height: 1.3;
}

.board-detail .content h3 {
  font-size: 1.5em;
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: bold;
  line-height: 1.4;
}

/* 단락 스타일 */
.board-detail .content p {
  font-size: 1.125em; /* 대략 18px 정도. 필요하면 조정 가능 */
  margin-bottom: 1em;
  line-height: 1.7;
  color: #2c3e50;
}
.board-detail .content strong {
  font-weight: bolder;
}

.board-detail .content ol {
  margin-bottom: 1em;
  padding-left: 2em;
  list-style: decimal;
}
.board-detail .content ul {
  margin-bottom: 1em;
  padding-left: 2em;
  list-style: circle;
}

.board-detail .content em {
  font-style: italic;
}
.board-detail .content blockquote {
  border-left: 4px solid #3498db;
  padding-left: 1em;
  margin-left: 0;
  color: #7f8c8d;
}

.board-detail .content pre {
  background-color: #2c3e50;
  color: #ecf0f1;
  padding: 1em;
  border-radius: 4px;
  overflow-x: auto;
}
.ql-align-center {
  text-align: center;
}
.ql-align-right {
  text-align: right;
}
.board-detail .content code {
  background-color: #34495e;
  color: #ecf0f1;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-family: monospace;
}

.author {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
}

.author i {
  margin-right: 8px;
}

.board-detail-btn-box {
  width: 100%;
  height: auto;
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 10px;
}
</style>
