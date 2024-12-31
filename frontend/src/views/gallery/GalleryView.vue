<template>
  <div class="gallery-container" style="min-height: 500px">
    <!-- 이미지업로더 토글버튼 -->
    <Button
      label="이미지업로드"
      icon="pi pi-cloud-upload"
      @click="toggleShowUploader"
      class="uploader-toggle-button"
    />
    <!-- 이미지 파일 업로더 -->
    <GalleryFileUploader v-if="isUploaderVisible" />
    <!-- 이미지 리스트-->
    <div class="gallery-list-container" ref="galleryRef">
      <GalleryList />
    </div>
  </div>
</template>

<script setup>
import GalleryFileUploader from './components/GalleryFileUploader.vue';
import GalleryList from './components/GalleryList.vue';
import { ref } from 'vue';
// 이미지 업로더 토글
const isUploaderVisible = ref(false);
const toggleShowUploader = () => {
  isUploaderVisible.value = !isUploaderVisible.value;
};
</script>

<style scoped>
.gallery-container {
  width: 98%;
  margin: 10px;
}
.gallery-list-container {
  width: 100%;
  margin-left: 6px;
}

.uploader-toggle-button {
  display: block;
  margin-left: auto; /* 우측 정렬 */
}
</style>
