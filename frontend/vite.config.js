import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig(({ mode }) => {
  // Load env file based on current mode (development, production, etc.)
  const env = loadEnv(mode, process.cwd(), '');

  // Fallback to default port 5173 if not set
  const port = parseInt(env.VITE_PORT) || 5173;

  return {
    plugins: [react()],
    resolve: {
      alias: {
        '@services': path.resolve(__dirname, 'src/services'),
        '@features': path.resolve(__dirname, 'src/features'),
      },
    },
    server: {
      port,
      strictPort: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
        },
      },
    },
  };
});
