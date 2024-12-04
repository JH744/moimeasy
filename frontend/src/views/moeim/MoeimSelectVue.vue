<template>
    <div class="authority-container">
      <h2 class="title">권한 설정</h2>
      <div class="button-group">
        <button class="btn-primary" @click="selectCreateMoeim">운영자 권한</button>
        <button class="btn-secondary" @click="selectEnterMoeim">모임 코드 입력</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "AuthoritySelect",
    methods: {
      selectCreateMoeim() {
        console.log("운영자 권한 선택");
        this.$router.push("/create-moeim");
      },
      selectEnterMoeim() {
        console.log("모임 코드 입력 선택");
        this.$router.push("/enter-moeim");
      },
    },
  };
  </script>
  
  <style scoped>
  /* 컨테이너 스타일 */
  .authority-container {
    width: 400px;
    min-width: 400px;
    margin: 50px auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    text-align: center;
  }
  
  /* 제목 스타일 */
  .title {
    font-size: 24px;
    color: #414651;
    font-weight: bold;
    margin-bottom: 20px;
  }
  
  
  /* 버튼 그룹 */
  .button-group {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  /* 버튼 스타일 */
  .btn-primary {
    background-color: #7f56d9;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .btn-primary:hover {
    background-color: #6a48b0;
  }
  
  .btn-secondary {
    background-color: white;
    color: #414651;
    border: 2px solid #414651;
    padding: 10px 20px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .btn-secondary:hover {
    background-color: #f7f5fc;
  }
  </style>
  