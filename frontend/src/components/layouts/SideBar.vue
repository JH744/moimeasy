<template>
  <div class="nav-bar">
    <!-- 사이드바 로고 -->
    <NavLogo />
    <!-- 사이드바 메뉴  -->
    <div class="main-menu-container">
      <div class="main-menu-header">
        <div class="main-menu-title">MAIN MENU</div>
      </div>
      <div class="card flex justify-center">
        <PanelMenu :model="items" class="pannel-item">
          <template #item="{ item }">
            <!-- 대쉬보드 메뉴 -->
            <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
              <a v-ripple class="dropmenu-item" :href="href" @click="navigate">
                <img class="icon-dashboard" :src="item.icon" width="15px" />
                <span class="menu-title">{{ item.label }}</span>
              </a>
            </router-link>
            <!-- 로그아웃 및 기타 -->
            <a v-else-if="item.command" v-ripple class="dropmenu-item" href="#" @click.prevent="item.command">
              <img class="icon-dashboard" :src="item.icon" width="15px" />
              <span class="menu-title">{{ item.label }}</span>
            </a>
            <!-- 기타 메뉴 -->
            <a v-else v-ripple class="dropmenu-item" :href="item.url" :target="item.target">
              <img class="icon-dashboard" :src="item.icon" width="15px" />
              <span class="menu-title">{{ item.label }}</span>
            </a>
          </template>
        </PanelMenu>
      </div>
    </div>
  </div>
</template>

<script setup>
import UsersIcon from '@/assets/icons/usersIcon.svg?url';
import OutlineIcon from '@/assets/icons/outlineIcon.svg?url';
import EditIcon from '@/assets/icons/editIcon.svg?url';
import Notebook from '@/assets/icons/notebook.svg?url';
import GraphIcon from '@/assets/icons/graphIcon.svg?url';
import PanelMenu from 'primevue/panelmenu';
import { ref } from 'vue';
import NavLogo from './NavLogo.vue';
import ChatIcon from '@/assets/icons/chatIcon.svg?url';
import albumIcon from '@/assets/icons/albumIcon.svg?url';

//사이드바 메뉴구성 아이템
const items = ref([
  {
    label: 'Dashboard',
    icon: OutlineIcon,
    route: '/main',
  },
  {
    label: '회원관리',
    icon: UsersIcon,
    items: [
      {
        label: '회원조회',
        icon: UsersIcon,
        route: '/user-manage',
      },
      {
        label: '초대목록',
        icon: UsersIcon,
        route: '/invitation-list',
      },
    ],
  },
  {
    label: '일정관리',
    icon: EditIcon,
    items: [
      {
        label: '일정조회',
        icon: EditIcon,
        route: '/schedule/calendar',
      },
      {
        label: '모임게시판',
        icon: EditIcon,
        route: '/schedule/board',
      },
    ],
  },
  {
    label: '회비관리',
    icon: Notebook,
    items: [
      {
        label: '납부내역',
        icon: Notebook,
        route: '/remittance-list',
      },
      {
        label: '거래내역',
        icon: Notebook,
        route: '/transaction-list',
      },
      {
        label: '정산요청',
        icon: Notebook,
        route: '/settlement',
      },
    ],
  },
  {
    label: '통계',
    icon: GraphIcon,
    route: '/category',
    // items: [
    //   {
    //     label: '카테고리별',
    //     icon: GraphIcon,
    //     route: '/category',
    //   },
    //   {
    //     label: '월별',
    //     icon: GraphIcon,
    //     route: '/category',
    //   },
    // ],
  },
  {
    label: '채팅',
    icon: ChatIcon,
    items: [
      {
        label: '채팅목록',
        icon: ChatIcon,
        url: '/chat-layout',
      },
    ],
  },
  {
    label: '갤러리',
    icon: albumIcon,
    url: '/gallery',
  },
]);
</script>

<style>
.nav-bar,
.nav-bar * {
  box-sizing: border-box;
}

.nav-bar {
  background: #ffffff;
  width: 264px;
  width: 264px;
  height: 100vh;
  position: relative;
  box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.nav-logo {
  display: flex;
  flex-direction: row;
  gap: 16px;
  align-items: center;
  justify-content: flex-start;
  position: absolute;
  left: 24px;
  top: 24px;
}

.logo-container {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  position: static;
}

.logo-background {
  background: #ffffff;
  border-radius: 6px;
  /* border: 0.75px solid #e8e8e8; */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 42px;
  height: 42px;
  position: absolute;
  left: 0px;
  top: 0px;
  transition: all 0.2s;
}

.logo-background:hover {
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 15px rgba(129, 85, 165, 0.3);
  /* 블러 그림자 효과 */
}

.logo-background:active {
  cursor: pointer;
  border: none;
  background-color: #f8f3fb;
  box-shadow: 0 4px 15px rgba(62, 32, 84, 0.3);
  /* 블러 그림자 효과 */
}

.logo-icon {
  width: 24px;
  height: 24px;
  position: absolute;
  left: 9px;
  top: 9px;
  overflow: visible;
  cursor: pointer;
}

.logo-text-container {
  flex-shrink: 0;
  width: 116px;
  height: 24px;
  position: static;
}

.logo-text {
  color: var(--main-color);
  text-align: left;
  font-family: 'Poppins-Medium', sans-serif;
  font-size: 20px;
  line-height: 24px;
  font-weight: 500;
  position: absolute;
  left: 58px;
  top: 9px;
}

.main-menu-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  position: relative;
  left: 22px;
  top: 122px;
}

.main-menu-header {
  flex-shrink: 0;
  width: 240px;
  height: 24px;
  position: relative;
  overflow: hidden;
}

.main-menu-title {
  color: #969696;
  text-align: left;
  font-family: 'Poppins-Regular', sans-serif;
  font-size: 12px;
  line-height: 16px;
  letter-spacing: 0.04em;
  font-weight: 400;
  text-transform: uppercase;
  position: absolute;
  left: 0px;
  top: 4px;
}

.menu-tabs {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.menu-item,
.menu-item-active {
  border-radius: 8px;
  padding: 8px 8px 8px 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: center;
  flex-shrink: 0;
  width: 212px;
  height: 53px;
  position: relative;
  cursor: pointer;
}

.menu-item-active {
  background: #ebeef0;
}

.menu-item-text-container {
  display: flex;
  flex-direction: row;
  gap: 11px;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.icon-dashboard,
.icon-cart,
.icon-edit,
.icon-notebook,
.icon-graph,
.icon-users,
.icon-settings {
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  height: auto;
  position: relative;
  overflow: visible;
}

.menu-item-text {
  color: #181616;
  text-align: left;
  font-family: 'Poppins-Medium', sans-serif;
  font-size: 16px;
  line-height: 16px;
  font-weight: 500;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.dropdown-menu {
  display: flex;
  flex-direction: column;
  gap: 0px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.menu-item {
  border-radius: 8px;
  padding: 19px 8px 19px 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: center;
  flex-shrink: 0;
  width: 212px;
  position: relative;
  cursor: pointer;
}

.bottom-menu-container {
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  width: 264px;
  height: 189px;
  position: absolute;
  left: 0px;
  bottom: 2px;
}

.more-settings {
  border-top: 1px solid #9d9d9d;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.settings-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
  justify-content: flex-start;
  flex-shrink: 0;
  position: relative;
}

.settings-item {
  border-radius: 8px;
  padding: 8px 8px 8px 24px;
  display: flex;
  gap: 8px;
  align-items: flex-start;
  align-items: center;
  flex-shrink: 0;
  width: 212px;
  height: 53px;
  position: relative;
}

.settings-text {
  color: #181616;
  text-align: left;
  font-family: 'Poppins-Medium', sans-serif;
  font-size: 16px;
  line-height: 16px;
  font-weight: 500;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.dropmenu-item {
  display: flex;
  /* Flexbox 사용 */
  align-items: center;
  /* 수직 중앙 정렬 */
  gap: 10px;
  border-radius: 8px;
  padding: 8px 8px 8px 24px;
  width: 135px;
  border: none;
  height: 53px;
  cursor: pointer;
  /* 포인터 커서 */
  color: #181616;
  text-align: left;
  font-family: 'Poppins-Medium', sans-serif;
  font-size: 16px;
  line-height: 16px;
  font-weight: 500;
  padding: 0.5rem 1rem;
  /* px-4, py-2에 해당 */
}

.pannel-item {
  width: 100%;
  /* 기본적으로 전체 너비 */
}

@media (min-width: 768px) {

  /* md 브레이크포인트: 768px 이상 */
  .pannel-item {
    width: 13rem;
  }
}

@media (prefers-color-scheme: dark) {
  .pannel-item {
    color: #ffffff;
    /* 다크 모드에서 텍스트 색상 (surface-0의 예) */
  }
}

.p-panelmenu-panel {
  /* border: none !important; */
  padding: 0px;
  cursor: pointer;
  width: 190px;
}

.menu-title {
  width: 75px;
}

.p-panelmenu-item-content {
  span {
    font-size: 14px;
  }
}
</style>
