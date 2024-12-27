<template>
  <div class="gallery-list-container" ref="galleryRef">
    <div v-for="item in galleryList" :key="item.id" class="masonry-item">
      <Skeleton
        v-if="isLoading(item.id)"
        shape="rectangle"
        width="100%"
        height="200px"
      />

      <img
        :src="item.imageUrl"
        :alt="`Image ${item.id}`"
        loading="lazy"
        @load="onImageLoad(item.id)"
      />
    </div>
    <div ref="loadMoreTrigger" class="load-more-trigger"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Skeleton from 'primevue/skeleton';

const galleryList = ref([]);
const page = ref(0);
const totalPages = ref(1);
const loadingMap = ref({});
const loadMoreTrigger = ref(null);

// 로딩 상태 체크 함수 추가
function isLoading(id) {
  return loadingMap.value[id] === undefined ? true : loadingMap.value[id];
}

async function loadData() {
  if (page.value < totalPages.value) {
    try {
      const res = await axios.get(
        `/api/v1/gallery?moeimId=1&page=${page.value}&size=12`
      );
      const { content, totalPages: tPages } = res.data;
      totalPages.value = tPages;

      // 새로운 아이템들의 초기 로딩 상태를 true로 설정
      content.forEach((item) => {
        loadingMap.value[item.id] = true;
      });

      // 기존 목록에 추가
      galleryList.value.push(...content);
      page.value += 1;
    } catch (error) {
      console.error(error);
    }
  }
}

function onImageLoad(id) {
  // 이미지 로드 완료 시 로딩 상태를 false로 변경
  loadingMap.value[id] = false;
}

function initIntersectionObserver() {
  const observer = new IntersectionObserver(
    (entries) => {
      const [entry] = entries;
      if (entry.isIntersecting) {
        loadData();
      }
    },
    {
      root: null,
      rootMargin: '0px',
      threshold: 0.1,
    }
  );
  if (loadMoreTrigger.value) {
    observer.observe(loadMoreTrigger.value);
  }
}

onMounted(() => {
  loadData();
  initIntersectionObserver();
});
</script>

<style scoped>
/* .gallery-list-container {
  column-count: 4;
  column-gap: 1rem;
}
.masonry-item {
  break-inside: avoid;
  margin-bottom: 1rem;
  display: block;
  width: 100%;
}
.masonry-item img {
  width: 100%;
  height: auto;
  display: block;
}
.load-more-trigger {
  height: 1px;
} */

/* .gallery-list-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.masonry-item {
  margin-bottom: 1rem;
  width: 100%;
}

.masonry-item img {
  width: 100%;
  height: auto;
  display: block;
}

.load-more-trigger {
  height: 1px;
}

/* 반응형 그리드 레이아웃 */
/* @media (min-width: 768px) {
  .gallery-list-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
  }
} */

.gallery-list-container {
  /* column-count 대신 column-width 사용 */
  columns: 250px auto;
  column-gap: 1rem;
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 1rem;
  display: inline-block;
  width: 100%;
}

.masonry-item img {
  width: 100%;
  height: auto;
  display: block;
}

.load-more-trigger {
  height: 1px;
}
</style>
