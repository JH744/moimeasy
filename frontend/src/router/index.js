import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/HomeView.vue';
import Login from '@/views/login/LoginView.vue';
import Signup from '@/views/signup/SignupView.vue';
import MoeimSelect from '@/views/moeim/MoeimSelectView.vue';
import CreateMoeim from '@/views/moeim/CreateMoeimView.vue';
import EnterMoeim from '@/views/moeim/EnterMoeimView.vue';
import UserManage from '@/views/user/UserManageView.vue';
import UserProfile from '@/views/user/UserProfileView.vue';
import InviteUser from '@/views/user/InviteUserView.vue';
import InvitationList from '@/views/user/InvitationListView.vue'; // 초대 목록 추가
import RemittanceList from '@/views/transaction/RemittanceList.vue';
import WebSocket from '@/views/chat/WebSocketView.vue';
import TransactionList from '@/views/transaction/TransactionList.vue';
import Category from '@/views/transaction/Category.vue';
import ChatRoomList from '@/views/chat/ChatRoomListView.vue';
import ChatView from '@/views/chat/ChatView.vue';

// 다른 뷰를 추가로 임포트

import { useAuthStore } from '../stores/auth';

const routes = [
  // **루트 경로를 /home으로 리디렉션하는 라우트 추가**
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/signup', name: 'Signup', component: Signup },
  { path: '/moeim-select', name: 'MoeimSelect', component: MoeimSelect },
  { path: '/create-moeim', name: 'CreateMoeim', component: CreateMoeim },
  { path: '/enter-moeim', name: 'EnterMoeim', component: EnterMoeim },
  { path: '/user-manage', name: '회원관리', component: UserManage },
  {
    path: '/user-profile/:userId',
    name: '회원프로필',
    component: UserProfile,
  },
  { path: '/invite-user', name: '회원초대', component: InviteUser },
  {
    path: '/invitation-list',
    name: '초대목록',
    component: InvitationList,
  }, // 초대 목록 추가
  {
    path: '/remittance-list',
    name: 'RemittanceList',
    component: RemittanceList,
  },
  { path: '/chat', name: 'WebSocket', component: WebSocket },
  {
    path: '/transaction-list',
    name: 'TransactionList',
    component: TransactionList,
  },
  { path: '/category', name: 'Category', component: Category },
  { path: '/chat-room-list', name: '채팅목록', component: ChatRoomList },
  {
    path: '/chat/:roomId',
    name: 'ChatView',
    component: () => import('@/views/chat/ChatView.vue'),
    props: true, // roomId를 컴포넌트에 props로 전달
  },
  {
    path: '/schedule',
    name: '일정',
    children: [
      {
        path: 'calendar',
        name: '일정 캘린더',
        component: () => import('@/views/schedule/ScheduleView.vue'),
      },
      {
        path: 'board',
        name: '게시판',
        component: () => import('@/views/board/BoardView.vue'),
      },
      {
        path: 'post',
        name: '게시글작성',
        component: () => import('@/views/board/BoardPostView.vue'),
      },
      {
        path: 'chte',
        name: '채팅',
        component: () => import('@/views/schedule/components/ChTe.vue'),
      },
    ],
  },
  {
    path: '/storage', // 파이어베이스 테스트 경로
    children: [
      {
        path: 'upload',
        component: () => import('@/firebase/StorageUploader.vue'),
      },
      {
        path: 'dragUpload',
        component: () => import('@/firebase/StorageDragDropUploader.vue'),
      },
      {
        path: 'load',
        component: () => import('@/firebase/StorageImageDownloader.vue'),
      },
    ],
  },
  {
    path: '/:catchAll(.*)',
    component: () => import('@/views/Error/Error404.vue'),
  },
  {
    path: '/chat-layout',
    name: '채팅',
    component: () => import('@/views/chat/ChatLayoutView.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// router.beforeEach((to, from, next) => {
//   const isAuthenticated = localStorage.getItem('accessToken'); // 인증 상태 확인

//   if (to.path !== '/login' && !isAuthenticated) {
//     next('/login'); // 인증되지 않은 사용자는 로그인 화면으로 리다이렉트
//   } else {
//     next(); // 인증된 사용자는 요청한 경로로 이동
//   }
// });

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('accessToken'); // 인증 상태 확인

  // 로그인이 필요한 페이지를 지정 (예: /main, /moeim-select)
  const requiresAuth = !['/login', '/signup'].includes(to.path);

  if (requiresAuth && !isAuthenticated) {
    // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
    next('/login');
  } else {
    // 인증된 사용자 또는 예외 경로는 그대로 진행
    next();
  }
});

export default router;
