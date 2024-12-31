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
              <img :src="guide.src" :alt="`Guide Image ${index + 1}`" />
              <p class="image-caption">{{ guide.caption }}</p>
            </div>
          </div>
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
        '/guide/login.png',
        '/guide/signup.png',
        '/guide/moeim.png',
        '/guide/settlement.png',
        '/guide/chat.png',
        '/guide/invitation.png',
        '/guide/calendar.png',
        '/guide/schedule.png',
        '/guide/remittance.png',
        '/guide/transaction.png',
        '/guide/chart.png',
      ],
      guideImages: [
        { src: '/guide/login.png', caption: '로그인 화면' },
        { src: '/guide/signup.png', caption: '회원가입 화면' },
        { src: '/guide/moeim.png', caption: '모임 생성 및 가입 화면' },
        { src: '/guide/settlement.png', caption: '정산 요청 화면' },
        { src: '/guide/chat.png', caption: '모임 채팅 화면' },
        { src: '/guide/invitation.png', caption: '초대 화면' },
        { src: '/guide/calendar.png', caption: '캘린더 화면' },
        { src: '/guide/schedule.png', caption: '일정 화면' },
        { src: '/guide/remittance.png', caption: '송금 화면' },
        { src: '/guide/transaction.png', caption: '거래 내역 화면' },
        { src: '/guide/chart.png', caption: '통계 화면' },
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
      }, 2000); // 3초 간격으로 이미지 변경
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
  height: 450px;
  margin-top: 20px;
}

.modal-slider-wrapper {
  display: flex;
  transition: transform 0.5s ease;
}

.modal-slider-image {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  align-items: center;
  width: 100%;
  height: 100%;
  justify-content: flex-start;
}

.modal-slider-image img {
  width: 90%;
  height: 80%;
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

.guide-button-container {
  display: flex;
  justify-content: center;
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
.image-caption {
  font-size: 16px;
  color: #555; /* 조금 더 진한 색으로 가독성 향상 */
  text-align: center;
  margin-top: 10px; /* 이미지와의 간격을 줄임 */
}
</style>
