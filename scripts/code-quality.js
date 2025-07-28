#!/usr/bin/env node

const { execSync } = require('child_process');
const path = require('path');

const ROOT_DIR = path.resolve(__dirname, '..');

console.log('🚀 Запуск проверки качества кода...\n');

const checks = [
  {
    name: '📋 ESLint проверка',
    command: 'npm run lint',
    required: true
  },
  {
    name: '🧪 Тесты',
    command: 'npm run test -- --watch=false --browsers=ChromeHeadless',
    required: false
  },
  {
    name: '📦 Проверка bundle size',
    command: 'npm run build -- --configuration=production',
    required: true
  },
  {
    name: '🔍 Проверка неиспользуемых импортов',
    command: 'npm run check-imports',
    required: true
  }
];

let failed = false;

for (const check of checks) {
  console.log(`${check.name}...`);
  
  try {
    execSync(check.command, {
      cwd: ROOT_DIR,
      stdio: 'inherit'
    });
    console.log(`✅ ${check.name} прошла успешно\n`);
  } catch (error) {
    console.error(`❌ ${check.name} не пройдена\n`);
    if (check.required) {
      failed = true;
    }
  }
}

if (failed) {
  console.error('💥 Проверка качества кода не пройдена!');
  process.exit(1);
} else {
  console.log('🎉 Все проверки пройдены успешно!');
}