import './assets/main.css';
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import axios from 'axios';
axios.defaults.baseURL = 'http://localhost:8088';
import Header from './components/layouts/Header.vue';
import SideBar from './components/layouts/SideBar.vue';
import PrimeVue from 'primevue/config'; // PrimeVue 추가
import Aura from '@primevue/themes/aura';
import ToastService from 'primevue/toastservice';
import ConfirmationService from 'primevue/confirmationservice';
import DialogService from 'primevue/dialogservice';
import { createPinia } from 'pinia';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import PanelMenu from 'primevue/panelmenu';

const pinia = createPinia();
const app = createApp(App);

// 컴포넌트 전역 등록
app.component('Header', Header);
app.component('SideBar', SideBar);
app.component('Button', Button);
app.component('InputText', InputText);
app.component('PanelMenu', PanelMenu);

// 플러그인 등록
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      prefix: 'p',
      darkModeSelector: 'none',
      cssLayer: false,
    },
  },
});
app.use(ConfirmationService);
app.use(ToastService);
app.use(DialogService);
app.use(pinia);

app.use(router).mount('#app');
