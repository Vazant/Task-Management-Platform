import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: false,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 30000,
    requestTimeout: 30000,
    responseTimeout: 30000,
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'cypress/support/e2e.ts',
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    typescript: {
      configFile: 'tsconfig.cypress.json'
    },
    // Добавляем поддержку SPA routing
    experimentalStudio: true,
    // Увеличиваем время ожидания для CI
    pageLoadTimeout: 60000,
    // Отключаем проверку безопасности для локальных тестов
    chromeWebSecurity: false,
    // Добавляем retry для нестабильных тестов
    retries: {
      runMode: 2,
      openMode: 0
    },
    // Увеличиваем время ожидания для команд
    execTimeout: 60000,
  },

  component: {
    devServer: {
      framework: 'angular',
      bundler: 'webpack',
    },
    specPattern: 'src/**/*.cy.ts',
  },
});
