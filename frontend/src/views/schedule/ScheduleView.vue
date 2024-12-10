<template>
  <div class="schedule-section">
    <div class="calendar-container">
      <ScheduleCalendar />
    </div>
  </div>
</template>

<script setup>
import ScheduleCalendar from './components/ScheduleCalendar.vue';
</script>

<style scoped>
.schedule-section {
  /* background-color: #f9f1ff; */
  background-color: #f8f9fa;
  width: 100%;
  overflow-x: auto;
}

.calendar-container {
  background-color: #ffffff;
  border: 1px solid #e9e9e9;
  padding: 20px;
  border-radius: 5%;
  width: 1100px;
  margin: 10px auto;
}
</style>
