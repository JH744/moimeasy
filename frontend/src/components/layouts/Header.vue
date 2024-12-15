<template>
  <div class="header-wrapper">
    <div class="header-box">
      <div class="header-box1">
        <!-- 브래드크럼 설정 -->
        <!-- <img src="@/assets/arrowIcon.svg?url" alt="Arrow Icon" height="12px" />
        <p>DashBoard</p> -->
        <div class="card breadcrumb-wrapper">
          <Breadcrumb :home="home" :model="items">
            <template #item="{ item, props }">
              <router-link
                v-if="item.route"
                v-slot="{ href, navigate }"
                :to="item.route"
                custom
              >
                <a :href="href" v-bind="props.action" @click="navigate">
                  <span :class="[item.icon, 'text-color']" />
                  <span class="text-primary font-semibold">{{
                    item.label
                  }}</span>
                </a>
              </router-link>
              <a
                v-else
                :href="item.url"
                :target="item.target"
                v-bind="props.action"
              >
                <span class="text-dark">{{ item.label }}</span>
              </a>
            </template>
          </Breadcrumb>
        </div>
      </div>
      <div class="header-box2">
        <!-- 검색창 -->
        <div class="search-bar-box">
          <input class="search-bar" />
          <img
            src="@/assets/icons/searchIcon.svg?url"
            alt="search Icon"
            height="15px"
            class="search-icon"
          />
        </div>

        <!-- 알림메시지  -->
        <Toast position="bottom-right" />
        <div class="notifications-box" @click="showSecondary">
          <!-- <img
            src="@/assets/notifications.svg?url"
            alt="notifications Icon"
            height="28px"
          /> -->
          <OverlayBadge value="2" severity="danger">
            <i class="pi pi-bell" style="font-size: 24px" />
          </OverlayBadge>
        </div>
        <div class="profile-box">
          <img
            src="@/assets/icons/Avatar.svg?url"
            alt="Avatar Icon"
            height="32px"
          />
          <p>{{ nickName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import OverlayBadge from 'primevue/overlaybadge';
import Breadcrumb from 'primevue/breadcrumb';
import Toast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';

const nickName = ref('');
const userData = ref('');
const toast = useToast();
const route = useRoute();

// 브레드크럼 홈경로 설정
const home = ref({
  icon: 'pi pi-home',
  route: '/',
});

// 브레드크럼 페이지별 경로 설정
const items = ref([]);

// 라우트 변경 감지 및 업데이트
const updatePathInfo = () => {
  items.value = route.matched.map((item) => ({
    label: item.name,
    route: item.path,
  }));
};

// 초기 경로 설정
updatePathInfo();

// 라우트 변경 감지용
watch(
  () => route.matched,
  () => {
    updatePathInfo();
    console.log('라우트 변경됨:', items.value);
    const userDataStorage = localStorage.getItem('user');
    userData.value = userDataStorage ? JSON.parse(userDataStorage) : null;
    // 닉네임 업데이트
    nickName.value = userData.value?.nickname || '게스트';
  },
  { immediate: true } // 컴포넌트가 처음 마운트될 때도 실행
);
//Toast 전달 메시지 설정
const showSecondary = () => {
  toast.add({
    severity: 'secondary',
    summary: '[일정 등록]',
    detail: ' 우리 모임의 새 일정이 등록되었습니다.',
    life: 3000,
  });
  toast.add({
    severity: 'secondary',
    summary: '[쪽지]',
    detail: '운영자로 부터 쪽지가 도착했습니다.',
    life: 3000,
  });
};
</script>

<style>
.header-wrapper {
  width: 100%;
  height: 78px;
  background-color: #ffffff;
  border-bottom: 1px solid #e9e9e9;
}

.header-box {
  height: 100%;
  display: flex;
  padding: 0 13px;
  align-items: center;
  justify-content: space-between;
  gap: 79px;
}

.header-box1 {
  width: 309px;
  padding: 8px;
  display: flex;
  gap: 10px;

  p {
    width: 111px;
    font-size: 18px;
  }
}

.header-box2 {
  width: 763px;
  height: 40px;
  margin-right: 40px;
  display: flex;
  gap: 24px;
  align-items: center;
  justify-content: space-between;
}

.search-bar-box {
  width: 530px;
  border-radius: 10%;
  position: relative;
}

.search-bar {
  background: #f7f9fb;
  border-radius: 8px;
  flex-shrink: 0;
  height: 40px;
  width: 100%;
  border: none;
  padding: 10px 40px;
  box-sizing: border-box;
}
.search-icon {
  position: absolute;
  left: 15px;
  top: 12px;
  cursor: pointer;
}

.notifications-box {
  cursor: pointer;
}

.profile-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.breadcrumb-wrapper {
  display: flex;
  align-items: center;
}
.font-semibold {
  font-size: 14px;
  margin-top: 2px;
}
.text-dark {
  color: red;
}

.p-breadcrumb {
  min-width: 232px;
}
</style>
