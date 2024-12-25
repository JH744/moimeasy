<template>
  <div class="home-container">
    <!-- 소개 텍스트 -->
    <div class="intro">
      <h1 class="title">MoeimEasy</h1>
      <p class="subtitle">쉽고 간편한 모임 관리를 시작하세요!</p>
      <button class="btn-primary" @click="goToLogin">시작하기</button><br />
      <button class="btn-secondary" @click="toggleGuide">
        서비스 가이드 보기
      </button>
    </div>

    <!-- 슬라이더 영역 -->
    <div class="slider-container">
      <div
        class="slider-wrapper"
        :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }"
      >
        <div
          class="slider-image"
          v-for="(image, index) in images"
          :key="index"
          :style="{ backgroundImage: `url(${image})` }"
        ></div>
      </div>
    </div>

    <!-- 서비스 가이드 확장 영역 -->
    <div v-if="isGuideVisible" class="guide-slider">
      <div
        class="guide-slide"
        v-for="(guide, index) in guideImages"
        :key="index"
      >
        <img :src="guide" alt="Guide Image" class="guide-image" />
      </div>
      <div class="guide-button-container">
        <button class="btn-primary" @click="goToLogin">시작하기</button>
      </div>
    </div>

    <!-- 맨 위로 가기 버튼 -->
    <button
      v-if="isScrollTopVisible"
      class="scroll-top-btn"
      @click="scrollToTop"
    >
      ↑
    </button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      images: [
        '/intro/intro1.png',
        '/intro/intro2.png',
        '/intro/intro3.png',
        '/intro/intro4.png',
        '/intro/intro5.png',
        '/intro/intro6.png',
        '/intro/intro7.png',
      ],
      guideImages: [
        '/guide/user.png',
        '/intro/intro2.png',
        '/intro/intro3.png',
        '/intro/intro4.png',
        '/intro/intro5.png',
        '/intro/intro6.png',
        '/intro/intro7.png',
      ],
      currentImageIndex: 0,
      isGuideVisible: false, // 가이드 표시 여부
      isScrollTopVisible: false, // 맨 위로 버튼 표시 여부
    };
  },
  methods: {
    goToLogin() {
      this.$router.push('/login');
    },
    toggleGuide() {
      this.isGuideVisible = !this.isGuideVisible; // 가이드 보이기/숨기기 토글
    },
    startSlider() {
      setInterval(() => {
        this.currentImageIndex =
          (this.currentImageIndex + 1) % this.images.length;
      }, 3000); // 3초 간격으로 이미지 변경
    },
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      });
    },
    handleScroll() {
      this.isScrollTopVisible = window.scrollY > 200; // 스크롤 위치에 따라 버튼 표시
    },
  },
  mounted() {
    this.startSlider();
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  },
};
</script>

<style scoped>
/* 전체 컨테이너 */
.home-container {
  position: relative;
  width: 100%;
  min-height: 100vh;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200px;
}

/* 소개 텍스트와 버튼 */
.intro {
  text-align: center;
  margin-bottom: 20px;
}

.title {
  font-size: 48px;
  font-weight: bold;
  color: #7f56d9;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 18px;
  color: #555;
  margin-bottom: 20px;
}

.btn-primary,
.btn-secondary {
  width: 200px;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  margin-top: 10px;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #7f56d9;
  color: white;
}

.btn-primary:hover {
  background-color: #6a48b0;
}

.btn-secondary {
  background-color: #bbb;
  color: white;
}

.btn-secondary:hover {
  background-color: #999;
}

/* 슬라이더 컨테이너 */
.slider-container {
  position: relative;
  width: 100%;
  max-width: 500px;
  height: 300px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.slider-wrapper {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 1s ease-in-out;
}

.slider-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  flex-shrink: 0;
}

/* 서비스 가이드 슬라이더 */
.guide-slider {
  width: 100%;
  max-width: 500px;
  margin: 20px 0;
}

.guide-slide {
  width: 100%;
  height: 300px;
  margin-bottom: 20px;
}

.guide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.guide-button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

/* 맨 위로 가기 버튼 */
.scroll-top-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 50px;
  height: 50px;
  background-color: #7f56d9;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  transition: opacity 0.3s;
}

.scroll-top-btn:hover {
  background-color: #6a48b0;
}

/* 반응형 */
@media (max-width: 768px) {
  .title {
    font-size: 36px;
  }

  .subtitle {
    font-size: 16px;
  }

  .btn-primary,
  .btn-secondary {
    font-size: 14px;
  }

  .slider-container {
    max-width: 100%;
    height: auto;
    aspect-ratio: 5 / 3;
  }

  .guide-slide {
    height: 200px;
  }

  .scroll-top-btn {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
}
</style>
