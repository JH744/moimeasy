<template>
  <div class="schedule-dialog-container">
    <ConfirmDialog group="positioned" :modal="false">
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

<style></style>
