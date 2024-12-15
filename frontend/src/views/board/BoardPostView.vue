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
          placeholder="제목을 입력하세요"
        />
      </div>

      <!-- 카테고리 선택 -->
      <div class="form-group">
        <label for="category">카테고리</label>
        <Dropdown
          id="category"
          v-model="post.category"
          :options="categories"
          optionLabel="name"
          placeholder="카테고리 선택"
        />
      </div>

      <!-- 내용 입력 -->
      <div class="form-group">
        <label for="content">내용</label>
        <div class="card">
          <Editor v-model="value" editorStyle="height: 320px" />
        </div>
      </div>

      <!-- 첨부 파일 -->
      <div class="form-group">
        <label for="file">첨부 파일</label>
        <input type="file" id="file" @change="handleFileUpload" />
        <small v-if="post.file">업로드된 파일: {{ post.file.name }}</small>
      </div>

      <!-- 제출 버튼 -->
      <div class="form-group">
        <Button
          type="submit"
          label="게시글 작성"
          icon="pi pi-check"
          :disabled="!post.title || !post.category"
        />
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import InputText from 'primevue/inputtext';
import Dropdown from 'primevue/dropdown';
import Button from 'primevue/button';
import Editor from 'primevue/editor';

// 게시글 데이터
const post = ref({
  title: '',
  category: null,
  content: '',
  file: null,
});

// 카테고리 옵션
const categories = ref([
  { name: '일반', code: 'GENERAL' },
  { name: '공지사항', code: 'NOTICE' },
  { name: '질문', code: 'QUESTION' },
  { name: '기술', code: 'TECH' },
]);

// 파일 업로드 처리
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    post.value.file = file;
    console.log('업로드된 파일:', file);
  }
};

// 게시글 제출 핸들러
const submitPost = () => {
  console.log('제출된 게시글:', post.value);

  // 제출 후 폼 초기화
  alert('게시글이 성공적으로 작성되었습니다.');
  post.value = {
    title: '',
    category: null,
    content: '',
    file: null,
  };
};
</script>

<style scoped>
.board-write-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
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
.p-dropdown,
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
