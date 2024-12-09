import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/HomeView.vue';
import Login from '@/views/login/LoginView.vue';
import Signup from '@/views/login/SignupView.vue';
import MoeimSelect from '@/views/moeim/MoeimSelectView.vue';
import CreateMoeim from '@/views/moeim/CreateMoeimView.vue';
import EnterMoeim from '@/views/moeim/EnterMoeimView.vue';
import Schedule from '@/views/schedule/ScheduleView.vue';
import UserManage from '@/views/user/UserManageView.vue';
import UserProfile from '@/views/user/UserProfileView.vue';
import InviteUser from '@/views/user/InviteUserView.vue';
import InvitationList from '@/views/user/InvitationListView.vue'; // 초대 목록 추가
import PayList from '@/views/pay/PayList.vue';
import WebSocket from '@/views/WebSocketView.vue';
import BoardView from '@/views/board/BoardView.vue';
import Draw from '@/views/schedule/Draw.vue';
import TradeList from '@/views/pay/TradeList.vue';
import Category from '@/views/pay/Category.vue';

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/signup', name: 'Signup', component: Signup },
  { path: '/moeim-select', name: 'MoeimSelect', component: MoeimSelect },
  { path: '/create-moeim', name: 'CreateMoeim', component: CreateMoeim },
  { path: '/enter-moeim', name: 'EnterMoeim', component: EnterMoeim },
  { path: '/user-manage', name: 'UserManage', component: UserManage },
  {
    path: '/user-profile/:userId',
    name: 'UserProfile',
    component: UserProfile,
  },
  { path: '/invite-user', name: 'InviteUser', component: InviteUser },
  {
    path: '/invitation-list',
    name: 'InvitationList',
    component: InvitationList,
  }, // 초대 목록 추가
  { path: '/pay-list', name: 'PayList', component: PayList },
  { path: '/chat', name: 'WebSocket', component: WebSocket },
  { path: '/draw', name: 'draw', component: Draw },
  { path: '/trade-list', name: 'TradeList', component: TradeList },
  { path: '/category', name: 'Category', component: Category },
  {
    path: '/schedule',
    children: [
      {
        path: 'calendar',
        component: () => import('@/views/schedule/ScheduleView.vue'),
      },
      {
        path: 'board',
        component: () => import('@/views/board/BoardView.vue'),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
