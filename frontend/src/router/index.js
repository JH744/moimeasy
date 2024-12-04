
import { createRouter, createWebHistory } from "vue-router";
import Home from "@/views/HomeVue.vue";
import Login from "@/views/login/LoginVue.vue";
import Signup from "@/views/login/SignupVue.vue";
import MoeimSelect from "@/views/moeim/MoeimSelectVue.vue";
import CreateMoeim from "@/views/moeim/CreateMoeimVue.vue";
import EnterMoeim from "@/views/moeim/EnterMoeimVue.vue";
import Schedule from "@/views/schedule/Schedule.vue";
import UserManage from "@/views/user/UserManageVue.vue";
import InviteUser from "@/views/user/InviteUserVue.vue";
import PayList from "@/views/PayList.vue";

const routes = [
  { path: "/", name: "Home", component: Home },
  { path: "/login", name: "Login", component: Login },
  { path: "/signup", name: "Signup", component: Signup },
  { path: "/moeim-select", name: "MoeimSelect", component: MoeimSelect },
  { path: "/create-moeim", name: "CreateMoeim", component: CreateMoeim },
  { path: "/enter-moeim", name: "EnterMoeim", component: EnterMoeim },
  { path: "/schedule", name: "Schedule", component: Schedule },
  { path: "/user-manage", name: "UserManage", component: UserManage },
    { path: "/invite-user", name: "InviteUser", component: InviteUser },
  { path: '/pay-list', name: 'PayList', component: PayList}
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
