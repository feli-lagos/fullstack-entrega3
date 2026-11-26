import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // El backend solo acepta peticiones (CORS) desde este puerto
  server: {
    port: 5173,
    strictPort: true,
  },
})
