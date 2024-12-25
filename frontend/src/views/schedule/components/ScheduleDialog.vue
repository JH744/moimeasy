<template>
  <div class="schedule-dialog-container">
    <ConfirmDialog
      group="positioned"
      :modal="false"
      style="background-color: 'red'"
    >
      <template #message>
        <div class="modal-body">
          <p>
            일정명 :<strong>{{ selectedEvent.title }}</strong>
          </p>
          <p>시작: {{ selectedEvent.start }}</p>
          <p>종료: {{ selectedEvent.end }}</p>
          <p>위치: {{ selectedEvent.location || '미정' }}</p>
          <p>설명: {{ selectedEvent.description || '없음' }}</p>
        </div>
      </template>
    </ConfirmDialog>
  </div>
</template>

<script setup>
defineProps({
  selectedEvent: Object,
});
</script>

<style>
.schedule-dialog-container {
  .p-dialog-header {
    background-color: rgb(218, 150, 231);
    border-top-left-radius: 15px;
    border-top-right-radius: 15px;
  }

  .modal-body {
    padding: 20px;
    background-color: aqua;
  }
}
</style>
