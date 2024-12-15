<template>
  <div class="error-container">
    <div class="error-content">
      <h1>404</h1>
      <p>페이지를 찾을 수 없습니다</p>
      <router-link to="/" class="home-button"> 홈으로 돌아가기 </router-link>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.error-container {
  width: 100%;
  height: 90vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
}

.error-content {
  text-align: center;
}

h1 {
  font-size: 120px;
  color: var(--main-color);
  margin: 0;
  font-weight: bold;
}

p {
  font-size: 24px;
  color: var(--p-primary-700);
  margin: 20px 0 40px;
}

.home-button {
  display: inline-block;
  padding: 15px 30px;
  background-color: var(--p-primary-500);
  color: white;
  text-decoration: none;
  border-radius: 5px;
  font-size: 16px;
  transition: background-color 0.3s;
}

.home-button:hover {
  background-color: var(--main-color);
}
</style>
