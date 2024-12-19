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
import FindEmailView from '@/views/find/FindEmailView.vue';
import FindUserInfoView from '@/views/find/FindUserInfoView.vue';
import ResetPasswordView from '@/views/find/ResetPasswordView.vue';

import Settlement from '@/views/moeim/SettlementView.vue';
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
  { path: '/find-email', name: 'FindEmail', component: FindEmailView },
  { path: '/finduser', name: 'FindUserInfo', component: FindUserInfoView },
  {
    path: '/resetpassword',
    name: 'ResetPassword',
    component: ResetPasswordView,
  },

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
  },
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
    props: true,
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
        path: 'board-post',
        name: '게시물 작성',
        component: () => import('@/views/board/BoardPostView.vue'),
      },
      {
        path: 'board-update/:id',
        name: '게시물 수정',
        component: () => import('@/views/board/BoardUpdateView.vue'),
        props: true,
      },
      {
        path: 'board-detail/:id',
        name: '게시물 상세',
        component: () => import('@/views/board/BoardDetailView.vue'),
        props: true,
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
    component: () => import('@/views/error/Error404.vue'),
  },
  {
    path: '/chat-layout',
    name: '채팅',
    component: () => import('@/views/chat/ChatLayoutView.vue'),
  },
  { path: '/settlement', name: '정산요청', component: Settlement },
  {
    path: '/main',
    name: '메인',
    component: () => import('@/views/MainView.vue'),
  },
  {
    path: '/gallery',
    name: '갤러리',
    component: () => import('@/views/gallery/GalleryView.vue'),
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

  // 로그인이 필요 없는 페이지 (로그인, 회원가입 등)
  const publicPages = [
    '/login',
    '/signup',
    '/find-email',
    '/finduser',
    '/resetpassword',
  ];
  const requiresAuth = !publicPages.includes(to.path); // 인증이 필요한 페이지

  if (isAuthenticated && to.path === '/login') {
    // 로그인 상태에서 로그인 페이지 접근 시 메인 페이지로 리다이렉트
    next('/main');
  } else if (requiresAuth && !isAuthenticated) {
    // 인증이 필요한 페이지인데 로그인 상태가 아니라면 로그인 페이지로 이동
    next('/login');
  } else {
    // 그 외의 경우는 정상 진행
    next();
  }
});

export default router;
