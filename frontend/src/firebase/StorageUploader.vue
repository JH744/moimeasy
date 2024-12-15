<template>
  <div id="app">
    <h1>파일 업로드</h1>
    <input type="file" @change="handleFileUpload" />
    <progress :value="uploadProgress" max="100"></progress>
    <p>현재 파일: {{ currentFile }}</p>
  </div>
</template>

<script>
import { ref, uploadBytes } from "firebase/storage";
import { firebaseStorage } from "@/firebase/firebaseConfig";
///////////////////

export default {
  data() {
    return {
      uploadProgress: 0,
      currentFile: "",
    };
  },
  methods: {
    async handleFileUpload(event) {
      const file = event.target.files[0]; // 선택한 첫 번째 파일
      if (!file) return;

      this.currentFile = file.name;
      // Firebase 스토리지 경로 설정
      const storageRef = ref(firebaseStorage, `uploads/${file.name}`);

      // 파일 업로드
      try {
        await uploadBytes(storageRef, file);
        this.uploadProgress = 100;
        this.currentFile = "업로드 완료: " + file.name;
      } catch (error) {
        console.error("파일 업로드 실패", error);
        this.currentFile = "업로드 실패";
      }
    },
  },
};
</script>

<style scoped>
progress {
  width: 100%;
  margin-top: 10px;
}
</style>
