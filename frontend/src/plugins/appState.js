import { reactive } from 'vue';

export default {
  install: (app) => {
    const _appState = reactive({ theme: 'Aura', darkTheme: false });

    app.config.globalProperties.$appState = _appState;
  },
};
