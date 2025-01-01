<template>
  <div class="gallery-list-container" ref="galleryRef">
    <div class="masonry-grid">
      <div v-for="item in galleryList" :key="item.id" class="masonry-item">
        <!-- 로딩 중 -> Skeleton UI 표시 -->
        <div v-if="isLoading(item.id)" class="skeleton-wrapper">
          <Skeleton shape="rectangle" width="100%" height="200px" />
        </div>
        <!-- 이미지로드완료 -> 이미지 표시 -->
        <img
          :src="item.imageUrl"
          :alt="`Image ${item.id}`"
          :class="{ 'image-loaded': !isLoading(item.id) }"
          loading="lazy"
          @load="onImageLoad(item)"
        />
      </div>
    </div>

    <!-- 스크롤 내려 아래 트리거가 화면에 보이면 이벤트호출 -->
    <div ref="loadMoreTrigger" class="load-more-trigger"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import Skeleton from 'primevue/skeleton';
import Masonry from 'masonry-layout';
import imagesLoaded from 'imagesloaded';
//  ref 변수
const galleryRef = ref(null); // 갤러리 컨테이너 참조
const galleryList = ref([]); // 갤러리 아이템 목록
const page = ref(0); // 현재 페이지 번호
const totalPages = ref(1); // 전체 페이지 수
const loadingMap = ref({}); // 아이템 별 로딩 상태 관리
const loadMoreTrigger = ref(null); // 무한 스크롤 트리거 참조
let msnry = null; // Masonry 인스턴스

// Masonry 초기화 함수
function initMasonry() {
  nextTick(() => {
    const grid = galleryRef.value?.querySelector('.masonry-grid');
    if (!grid) return;

    // 이전 인스턴스가 있으면 제거 -> 이때는 완전 재정렬함
    // if (msnry) {
    //   msnry.destroy();
    // }

    // 이미지 로드 후 Masonry 레이아웃 적용
    imagesLoaded(grid, () => {
      msnry = new Masonry(grid, {
        itemSelector: '.masonry-item', // 아이템 선택자
        columnWidth: '.masonry-item', // 열 너비
        percentPosition: true, // 백분율 기반 위치 지정
        gutter: 16, // 아이템 간격
        transitionDuration: '0.3s', // 애니메이션 속도
      });
    });
  });
}

// 특정 아이템이 로딩 중인지 확인
function isLoading(id) {
  return loadingMap.value[id] !== false;
}

// 데이터를 API로부터 로드하는 함수
async function loadData() {
  if (page.value >= totalPages.value) return; // 모든 페이지를 로드했으면 종료

  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const moeimId = user?.moeimId; // 사용자 정보에서 moeimId 가져오기
    const res = await axios.get(
      `/api/v1/gallery?moeimId=${moeimId}&page=${page.value}&size=12`
    );

    const { content, totalPages: tPages } = res.data;
    totalPages.value = tPages;

    // 새로운 데이터 추가 및 로딩 상태 초기화
    content.forEach((item) => {
      loadingMap.value[item.id] = true;
    });

    galleryList.value.push(...content); // 갤러리 목록에 데이터 추가
    page.value += 1;

    await nextTick();
    initMasonry(); // Masonry 재적용
  } catch (error) {
    console.error('Failed to load gallery data:', error);
  }
}

// 이미지 로드 완료 시 호출
function onImageLoad(item) {
  loadingMap.value[item.id] = false; // 로딩 상태를 false로 변경
  nextTick(() => {
    if (msnry) {
      msnry.layout(); // Masonry 레이아웃 갱신
    }
  });
}

// 무한 스크롤을 위한 IntersectionObserver 초기화
function initIntersectionObserver() {
  const observer = new IntersectionObserver(
    (entries) => {
      const [entry] = entries;
      if (entry.isIntersecting) {
        loadData(); // 데이터 로드 호출
      }
    },
    {
      root: null, // 뷰포트를 기준으로 감지
      rootMargin: '100px', // 뷰포트 주변 여백
      threshold: 0.1, // 10%가 보일 때 감지
    }
  );

  if (loadMoreTrigger.value) {
    observer.observe(loadMoreTrigger.value); // 트리거 요소 감지
  }
}

// 컴포넌트가 마운트될 때 초기화 작업
onMounted(async () => {
  await loadData(); // 첫 데이터 로드
  initIntersectionObserver(); // 무한 스크롤 초기화
});
</script>

<style scoped>
.gallery-list-container {
  width: 100%;
  padding: 16px;
}

.masonry-grid {
  width: 100%;
}

.masonry-item {
  width: calc(25% - 16px); /* 4개의 이미지가 한 행에 표시되도록 설정 */
  margin-bottom: 16px;
  break-inside: avoid;
  position: relative;
}

.skeleton-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.masonry-item img {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.masonry-item img.image-loaded {
  opacity: 1;
}

/* 반응형 디자인 */
/* @media screen and (max-width: 1490px) {
  .masonry-item {
    width: calc(20% - 16px);
  }
} */
/* 테블릿 화면에서 3개 */
@media screen and (max-width: 1490px) {
  .masonry-item {
    width: calc(25% - 16px);
  }
}

@media screen and (max-width: 1280px) {
  .masonry-item {
    width: calc(33.333% - 16px);
  }
}

@media screen and (max-width: 1024px) {
  .masonry-item {
    width: calc(50% - 16px); /* 중간화면에서 2개 */
  }
}

@media screen and (max-width: 640px) {
  .masonry-item {
    width: 100%; /* 작은 화면에서 1개 */
  }
}

.load-more-trigger {
  height: 20px;
  margin-top: 20px;
}
</style>
