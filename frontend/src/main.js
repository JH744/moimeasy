import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import Header from "./components/Header.vue";
import SideBar from "./components/SideBar.vue";
import PrimeVue from 'primevue/config'; // PrimeVue 추가
import Aura from '@primevue/themes/aura'

const app = createApp(App);
app.component("Header", Header);
app.component("SideBar", SideBar);
app.use(PrimeVue, {theme: {preset: Aura, options:{
            prefix: 'p',
            darkModeSelector: 'system',
            cssLayer: false
        }
    }});
app.use(router).mount("#app");
