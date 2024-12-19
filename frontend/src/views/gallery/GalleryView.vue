<template>
  <div class="gallery-container">
    <div class="card">
      <Galleria
        :value="images"
        :responsiveOptions="responsiveOptions"
        :numVisible="5"
        :circular="true"
        containerStyle="max-width: 640px"
        :showItemNavigators="true"
        :showItemNavigatorsOnHover="true"
      >
        <template #item="slotProps">
          <img
            :src="slotProps.item.itemImageSrc"
            :alt="slotProps.item.alt"
            style="width: 100%; display: block"
          />
        </template>
        <template #thumbnail="slotProps">
          <img
            :src="slotProps.item.thumbnailImageSrc"
            :alt="slotProps.item.alt"
            style="display: block"
          />
        </template>
      </Galleria>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { PhotoService } from '@/service/PhotoService';
import Galleria from 'primevue/galleria';

onMounted(() => {
  PhotoService.getImages().then((data) => (images.value = data));
});

const images = ref();
const responsiveOptions = ref([
  {
    breakpoint: '1300px',
    numVisible: 4,
  },
  {
    breakpoint: '575px',
    numVisible: 1,
  },
]);
</script>
<style scoped>
.gallery-container {
  width: 100%;
}
.card {
  width: 650px;
  margin: 50px auto;
}
</style>
