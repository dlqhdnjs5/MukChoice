import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const isDevelopment = mode !== 'production';

  return {
    plugins: [react()],
    server: {
      port: 3000,
      // 개발환경에서만 proxy 설정
      ...(isDevelopment && {
        proxy: {
          '/api': {
            target: 'http://localhost:8080',
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, '/'),
          },
        },
      }),
    },
    define: {
      __MODE__: JSON.stringify(mode),
    },
    build: {
      outDir: 'dist',
      sourcemap: mode === 'development',
      minify: mode === 'production',
    },
  };
});
