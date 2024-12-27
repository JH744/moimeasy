<template>
  <div class="gallery-container">
    <!-- 이미지 파일 업로더 -->
    <GalleryFileUploader />
    <!-- 이미지 리스트-->
    <GalleryList />
  </div>
</template>

<script setup>
import GalleryFileUploader from './components/GalleryFileUploader.vue';
import GalleryList from './components/GalleryList.vue';
</script>

<style scoped>
.gallery-container {
  width: 98%;
  margin: 10px;
  height: auto;
}
</style>
