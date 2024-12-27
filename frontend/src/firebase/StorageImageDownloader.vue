<template>
  <div id="app">
    <h1>Firebase 이미지 가져오기</h1>
    <input v-model="imageUrl" placeholder="파일경로 입력" />
    <br />
    <input v-model="fileName" placeholder="파일명 입력" />
    <br />
    <button @click="fetchImage">이미지 가져오기</button>
    <div v-if="imageUrl">
      <h2>불러온 이미지:</h2>
      <img :src="imageUrl" alt="Firebase Image" />
    </div>
  </div>
</template>

<script>
import { ref, getDownloadURL } from 'firebase/storage';
import { firebaseStorage } from '@/firebase/firebaseConfig';
export default {
  data() {
    return {
      imageUrl: '', // 경로명 (예시 uploads)
      fileName: '', // 불러올 파일명 (예시 outer.png)
    };
  },
  methods: {
    async fetchImage() {
      const storageRef = ref(
        firebaseStorage,
        `${this.imageUrl}/${this.fileName}`
      ); //
      // const storageRef = ref(firebaseStorage, 'uploads/outer.png');// 참고;
      try {
        // Firebase 스토리지에서 이미지 URL 가져오기
        this.imageUrl = await getDownloadURL(storageRef);
      } catch (error) {
        console.error('이미지 가져오기 실패:', error);
      }
    },
  },
};
</script>

<style scoped>
img {
  max-width: 100%;
  height: auto;
  margin-top: 10px;
}
</style>
