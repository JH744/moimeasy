<template>
  <div class="board-write-container">
    <h1 class="board-write-title">게시글 작성</h1>
    <form @submit.prevent="submitPost">
      <!-- 제목 입력 -->
      <div class="form-group">
        <label for="title">제목</label>
        <InputText
          id="title"
          v-model="post.title"
          placeholder="제목을 입력하세요."
        />
      </div>

      <!-- 태그 입력 -->
      <div class="form-group">
        <label for="tag">태그</label>
        <InputText
          id="tag"
          v-model="post.tag"
          placeholder="태그를 입력하세요."
        />
      </div>

      <!-- 내용 입력 -->
      <div class="form-group">
        <label for="content">내용</label>
        <div class="card">
          <Editor v-model="post.content" editorStyle="height: 320px" />
        </div>
      </div>

      <!-- 제출 버튼 -->
      <div class="form-group">
        <Button
          type="submit"
          label="게시글 작성"
          icon="pi pi-check"
          :disabled="!post.title || !post.tag || !post.content"
        />
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Editor from 'primevue/editor';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import api from '@/axios';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import { fetchAddNotification } from '@/utils/notification-add-utils';
const router = useRouter();
const toast = useToast();
const auth = useAuthStore();
// 게시글 데이터
const post = ref({
  title: '',
  tag: '',
  content: '',
});

// 게시글 제출 핸들러
const submitPost = async () => {
  console.log('ssss');
  try {
    const requestData = {
      title: post.value.title,
      tag: post.value.tag,
      content: post.value.content,
      isNotice: false, // 기본값 설정
    };

    // API 요청 (POST)
    const response = await api.post(
      `/api/v1/boards/${auth.user.userId}`,
      requestData
    );
    // 알림 추가
    fetchAddNotification(
      'new 게시글',
      '우리 모임의 새 게시물이 등록되었습니다.',
      auth?.user?.moeimId
    );

    console.log('API 응답:', response.data);
    toast.add({
      severity: 'info',
      summary: '게시글 등록 완료',
      detail: '게시글이 성공적으로 작성되었습니다.',
      life: 3000,
    });

    // 폼 초기화
    post.value = {
      title: '',
      tag: '',
      content: '',
    };
  } catch (error) {
    console.error('게시글 작성 실패:', error);
    toast.add({
      severity: 'error',
      summary: '게시글 등록 실패',
      detail: '게시글 작성 중 오류가 발생했습니다.',
      life: 3000,
    });
  } finally {
    await router.push('/schedule/board');
  }
};
</script>

<style scoped>
.board-write-container {
  max-width: 65%;
  margin: 10px auto;
  padding: 20px 40px;
  background-color: #ffffff;
  box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.2);
  border-radius: 10px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.p-inputtext,
.textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
}

.textarea {
  height: 150px;
  resize: none;
}

.p-button {
  margin-top: 20px;
}

.board-write-title {
  font-size: x-large;
  font-weight: 600;
  margin: 30px 0;
}
</style>
