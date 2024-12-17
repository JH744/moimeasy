import './assets/main.css';
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import axios from 'axios';
import Header from './components/layouts/Header.vue';
import SideBar from './components/layouts/SideBar.vue';
import SideSpeedDial from './components/layouts/SideSpeedDial.vue';
import PrimeVue from 'primevue/config'; // PrimeVue 추가
import Aura from '@primevue/themes/aura';
import ToastService from 'primevue/toastservice';
import DialogService from 'primevue/dialogservice';
import { createPinia } from 'pinia';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import PanelMenu from 'primevue/panelmenu';
import LoadingOverlay from './components/common/LoadingOverlay.vue';
import Noir from './presets/Noir.js';
import Avatar from 'primevue/avatar';
import FileUpload from 'primevue/fileupload';
import ConfirmationService from 'primevue/confirmationservice';
import ConfirmDialog from 'primevue/confirmdialog';
import Select from 'primevue/select';
import FloatLabel from 'primevue/floatlabel';
import Ripple from 'primevue/ripple';
import Toast from 'primevue/toast';

// axios 기본 설정 (Spring Boot 백엔드가 8088 포트라 가정)
axios.defaults.baseURL = 'http://localhost:8088'; // 서버 URL 설정

// 중앙집중식 Axios 인스턴스 임포트
import api from './axios';

const pinia = createPinia();
const app = createApp(App);

// 컴포넌트 전역 등록
app.component('Header', Header);
app.component('SideBar', SideBar);
app.component('Button', Button);
app.component('SideSpeedDial', SideSpeedDial);
app.component('InputText', InputText);
app.component('PanelMenu', PanelMenu);
app.component('LoadingOverlay', LoadingOverlay);
app.component('Avatar', Avatar);
app.component('FileUpload', FileUpload);
app.component('ConfirmDialog', ConfirmDialog);
app.component('Select', Select);
app.component('FloatLabel', FloatLabel);
// PrimeVue 플러그인 등록
app.use(
  PrimeVue,
  {
    theme: {
      preset: Aura,
      options: {
        prefix: 'p',
        darkModeSelector: 'none',
        cssLayer: false,
      },
    },
  },
  { ripple: true }
);
app.use(ToastService);
app.use(DialogService);
app.use(ConfirmationService);
app.use(pinia);
// 전역 속성으로 Axios 설정 (선택 사항)
app.config.globalProperties.$axios = api;
// Ripple 디렉티브 등록
app.directive('ripple', Ripple);
app.use(router).mount('#app');
