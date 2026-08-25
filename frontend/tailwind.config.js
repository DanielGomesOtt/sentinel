/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        medium: {
          bg: '#ffffff',
          'bg-dark': '#121212',
          surface: '#fafafa',
          'surface-dark': '#1e1e1e',
          card: '#ffffff',
          'card-dark': '#1a1a1a',
          border: '#e6e6e6',
          'border-dark': '#2a2a2a',
          text: '#242424',
          'text-dark': '#f0f0f0',
          muted: '#6b6b6b',
          'muted-dark': '#9b9b9b',
          green: '#1a8917',
          'green-hover': '#0f730c',
          'green-light': '#f0f9f0',
          'green-dark-bg': '#0f2911',
        }
      },
      fontFamily: {
        sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', '"Segoe UI"', 'Roboto', 'sans-serif'],
        serif: ['Georgia', 'Cambria', '"Times New Roman"', 'Times', 'serif'],
      }
    },
  },
  plugins: [],
}
