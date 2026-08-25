import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const backendUrl = process.env.VITE_DEV_API_BASE_URL || 'http://localhost:8081'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/oauth2/authorization': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/login/oauth2': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
