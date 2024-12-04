<template>
    <div class="home-container">
      <!-- 소개 텍스트 -->
      <div class="intro">
        <h1 class="title">MoeimEasy</h1>
        <p class="subtitle">쉽고 간편한 모임 관리를 시작하세요!</p>
        <button class="btn-primary" @click="goToLogin">시작하기</button>
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
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        images: [
          "/intro/intro1.png", // 슬라이더에 사용할 이미지 경로
          "/intro/intro2.png",
          "/intro/intro3.png",
          "/intro/intro4.png",
          "/intro/intro5.png",
          "/intro/intro6.png",
          "/intro/intro7.png",
        ],
        currentImageIndex: 0,
      };
    },
    methods: {
      goToLogin() {
        this.$router.push("/login");
      },
      startSlider() {
        setInterval(() => {
          this.currentImageIndex =
            (this.currentImageIndex + 1) % this.images.length;
        }, 3000); // 3초 간격으로 이미지 변경
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
    width: 200%;
    height: 100vh;
    overflow: hidden;
    background-color: #f5f5f5;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
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
  
  .btn-primary {
    width: 200px;
    background-color: #7f56d9;
    color: white;
    padding: 10px;
    border-radius: 5px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    border: none;
    transition: background-color 0.3s ease;
  }
  
  .btn-primary:hover {
    background-color: #6a48b0;
  }
  
  /* 슬라이더 컨테이너 */
  .slider-container {
    position: relative;
    width: 100%;
    max-width: 500px; /* 슬라이더 최대 폭 */
    height: 300px; /* 고정된 높이 */
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  /* 슬라이더 래퍼 */
  .slider-wrapper {
    display: flex;
    width: 100%;
    height: 100%;
    transition: transform 1s ease-in-out;
  }
  
  /* 슬라이더 이미지 */
  .slider-image {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    flex-shrink: 0;
  }
  
  /* 반응형 */
  @media (max-width: 768px) {
    .title {
      font-size: 36px;
    }
  
    .subtitle {
      font-size: 16px;
    }
  
    .btn-primary {
      font-size: 14px;
    }
  
    .slider-container {
      max-width: 100%;
      height: auto;
      aspect-ratio: 5 / 3; /* 비율 유지 */
    }
  }
  </style>
  