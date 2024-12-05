import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";
import svgLoader from "vite-svg-loader";
import Components from "unplugin-vue-components/vite";
import { PrimeVueResolver } from "unplugin-vue-components/resolvers";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    Components({
      resolvers: [PrimeVueResolver()], // PrimeVue 컴포넌트 자동 등록
    }),
    vueDevTools(),
    svgLoader(),
  ],

  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },

  // 빌드 출력 디렉토리 설정
  build: {
    outDir: "../src/main/resources/static", // Spring Boot의 정적 리소스 폴더로 설정
    emptyOutDir: true, // 기존 빌드 결과 삭제
  },

  // 개발 서버 설정
  server: {
    port: 3000, // 개발 서버 포트 설정
    proxy: {
      "/api": {
        target: "http://localhost", // Spring Boot 서버 주소
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""), // 경로 재작성
      },
    },
  },
});
