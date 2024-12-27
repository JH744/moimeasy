<template>
  <div class="home-container">
    <!-- 소개 텍스트 -->
    <div class="intro">
      <h1 class="title">MoeimEasy</h1>
      <p class="subtitle">쉽고 간편한 모임 관리를 시작하세요!</p>
      <button class="btn-primary" @click="goToLogin">시작하기</button><br />
      <button class="btn-secondary" @click="toggleGuideModal">
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

    <!-- 서비스 가이드 모달 -->
    <div v-if="isGuideModalVisible" class="guide-modal">
      <div class="guide-modal-content">
        <!-- Close Button -->
        <button class="modal-close-btn" @click="toggleGuideModal">×</button>
        <div class="modal-slider-container">
          <div
            class="modal-slider-wrapper"
            :style="{ transform: `translateX(-${currentGuideIndex * 100}%)` }"
          >
            <div
              class="modal-slider-image"
              v-for="(guide, index) in guideImages"
              :key="index"
            >
              <img :src="guide" alt="Guide Image" />
            </div>
          </div>

          <!-- Navigation Buttons -->
          <button
            class="nav-btn prev-btn"
            @click="prevGuideSlide"
            :disabled="currentGuideIndex === 0"
          >
            ◀
          </button>
          <button
            class="nav-btn next-btn"
            @click="nextGuideSlide"
            :disabled="currentGuideIndex === guideImages.length - 1"
          >
            ▶
          </button>
        </div>
        <!-- Centered Start Button -->
        <div class="guide-button-container">
          <button class="btn-primary" @click="goToLogin">시작하기</button>
        </div>
      </div>
    </div>
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
        '/guide/settlement.png',
        '/guide/chat.png',
        '/intro/intro4.png',
        '/intro/intro5.png',
        '/intro/intro6.png',
        '/intro/intro7.png',
      ],
      currentImageIndex: 0,
      currentGuideIndex: 0,
      isGuideModalVisible: false, // 모달 표시 여부
    };
  },
  methods: {
    goToLogin() {
      this.$router.push('/login');
    },
    toggleGuideModal() {
      this.isGuideModalVisible = !this.isGuideModalVisible;
    },
    startSlider() {
      setInterval(() => {
        this.currentImageIndex =
          (this.currentImageIndex + 1) % this.images.length;
      }, 3000); // 3초 간격으로 이미지 변경
    },
    prevGuideSlide() {
      if (this.currentGuideIndex > 0) {
        this.currentGuideIndex--;
      }
    },
    nextGuideSlide() {
      if (this.currentGuideIndex < this.guideImages.length - 1) {
        this.currentGuideIndex++;
      }
    },
  },
  mounted() {
    this.startSlider();
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

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-60%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  padding: 10px;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  z-index: 1000;
}

.nav-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.prev-btn {
  left: 10px;
}

.next-btn {
  right: 10px;
}

/* 모달 전체 스타일 */
.guide-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.guide-modal-content {
  position: relative;
  background: white;
  width: 90%;
  max-width: 800px;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
}

/* Close Button */
.modal-close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  font-weight: bold;
  color: #aaa;
  cursor: pointer;
}

.modal-close-btn:hover {
  color: #333;
}

/* 모달 내 슬라이더 */
.modal-slider-container {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 400px;
  margin-top: 20px;
}

.modal-slider-wrapper {
  display: flex;
  transition: transform 0.5s ease;
}

.modal-slider-image {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
}

.modal-slider-image img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 이미지 비율 유지 */
}

/* Navigation Buttons */
.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  padding: 10px;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  z-index: 1000;
}

.nav-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.prev-btn {
  left: 10px;
}

.next-btn {
  right: 10px;
}

/* 시작하기 버튼 정렬 */
.guide-button-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.btn-primary {
  background-color: #7f56d9;
  color: white;
  padding: 10px 20px;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary:hover {
  background-color: #6a48b0;
}
</style>
