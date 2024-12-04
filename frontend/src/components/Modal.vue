<template>
  <div class="modal-overlay" v-if="isVisible">
    <div class="modal-container">
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button @click="redirectToCreateMoeim" class="close-button">X</button>
      </div>
      <div class="modal-body">
        <p>모임 관리를 시작하세요!</p>
        <slot></slot>
      </div>
      <div class="modal-footer">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Modal",
  props: {
    isVisible: { type: Boolean, required: true },
    title: { type: String, default: "" },
  },
  emits: ["close"],
  methods: {
    redirectToCreateMoeim() {
      this.$emit("close");
      this.$router.push("/create-moeim"); // "모임 생성" 페이지로 이동
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  padding: 20px;
  border-radius: 12px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: center; /* 헤더를 가운데 정렬 */
  align-items: center;    /* 세로 중앙 정렬 */
  position: relative;
}

.modal-header h3 {
  font-weight: bold;
  font-size: 20px;
  color: #6a48b0;
}

.close-button {
  position: absolute;
  right: 0; /* 닫기 버튼 위치 조정 */
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.modal-body p {
  color: #414651;
  font-size: 16px;
  line-height: 1.5;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
