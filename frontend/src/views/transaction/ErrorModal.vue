<Dialog v-model:visible="visible" modal :style="{ width: '30rem', height: '30rem' }" @show="onShow">
    <template #header>
      <div class="dialog-header">
        <i class="pi pi-check-circle dialog-icon"></i>
        <div class="dialog-title">주의</div>
      </div>
    </template>
    <div class="dialog-content">
      <p>
        <span class="dialog-username">{{ moeimName }}</span> 의
        <span class="dialog-message">거래내역이 존재하지 않습니다.</span>
      </p>
      <Button label="닫기" rounded @click="closeDialog" />
    </div>
  </Dialog>