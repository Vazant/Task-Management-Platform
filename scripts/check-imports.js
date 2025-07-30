#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const glob = require('glob');

// Конфигурация
const CONFIG = {
  srcDir: 'client/src/app',
  maxRelativePathDepth: 3,
  forbiddenPatterns: [
    /\.\.\/\.\.\/\.\.\/\.\.\//, // ../../../
    /\.\.\/\.\.\/\.\.\//, // ../../
  ],
  barrelExports: {
    '@models': 'client/src/app/core/models/index.ts',
    '@services': 'client/src/app/core/services/index.ts',
    '@utils': 'client/src/app/core/utils/index.ts'
  },
  // Исключения для стандартных импортов
  excludedImports: [
    '@angular/core',
    '@angular/router',
    '@angular/common',
    '@angular/forms',
    '@angular/material',
    '@ngrx/store',
    '@ngrx/effects',
    '@ngrx/entity',
    'rxjs',
    'rxjs/operators'
  ]
};

// Функции проверки
function checkRelativePathDepth(importPath) {
  const depth = (importPath.match(/\.\.\//g) || []).length;
  return depth <= CONFIG.maxRelativePathDepth;
}

function checkForbiddenPatterns(importPath) {
  return !CONFIG.forbiddenPatterns.some(pattern => pattern.test(importPath));
}

function findDuplicateImports(files) {
  const imports = new Map();
  const duplicates = [];

  files.forEach(file => {
    const content = fs.readFileSync(file, 'utf8');
    const importMatches = content.match(/import.*from\s+['"]([^'"]+)['"]/g);

    if (importMatches) {
      importMatches.forEach(match => {
        const importPath = match.match(/from\s+['"]([^'"]+)['"]/)[1];

        // Пропускаем стандартные импорты
        if (CONFIG.excludedImports.some(excluded => importPath.startsWith(excluded))) {
          return;
        }

        if (!imports.has(importPath)) {
          imports.set(importPath, []);
        }
        imports.get(importPath).push(file);
      });
    }
  });

  imports.forEach((files, importPath) => {
    if (files.length > 1) {
      duplicates.push({ importPath, files });
    }
  });

  return duplicates;
}

function checkBarrelExports() {
  const issues = [];

  Object.entries(CONFIG.barrelExports).forEach(([alias, barrelPath]) => {
    if (!fs.existsSync(barrelPath)) {
      issues.push(`Missing barrel export: ${barrelPath}`);
    }
  });

  return issues;
}

// Основная функция
function main() {
  console.log('🔍 Проверка импортов в Angular проекте...\n');

  const tsFiles = glob.sync(`${CONFIG.srcDir}/**/*.ts`, { ignore: ['**/*.spec.ts'] });
  let hasIssues = false;

  // Проверка длинных относительных путей
  console.log('📏 Проверка длинных относительных путей:');
  tsFiles.forEach(file => {
    const content = fs.readFileSync(file, 'utf8');
    const importMatches = content.match(/import.*from\s+['"]([^'"]+)['"]/g);

    if (importMatches) {
      importMatches.forEach(match => {
        const importPath = match.match(/from\s+['"]([^'"]+)['"]/)[1];

        if (importPath.startsWith('.')) {
          if (!checkRelativePathDepth(importPath)) {
            console.log(`  ❌ ${file}: слишком длинный путь "${importPath}"`);
            hasIssues = true;
          }

          if (!checkForbiddenPatterns(importPath)) {
            console.log(`  ❌ ${file}: запрещенный паттерн "${importPath}"`);
            hasIssues = true;
          }
        }
      });
    }
  });

  // Проверка дублирующихся импортов
  console.log('\n🔄 Проверка дублирующихся импортов:');
  const duplicates = findDuplicateImports(tsFiles);
  if (duplicates.length > 0) {
    duplicates.forEach(({ importPath, files }) => {
      console.log(`  ❌ Дублирование "${importPath}":`);
      files.forEach(file => console.log(`    - ${file}`));
      hasIssues = true;
    });
  } else {
    console.log('  ✅ Дублирующихся импортов не найдено');
  }

  // Проверка barrel-экспортов
  console.log('\n📦 Проверка barrel-экспортов:');
  const barrelIssues = checkBarrelExports();
  if (barrelIssues.length > 0) {
    barrelIssues.forEach(issue => {
      console.log(`  ❌ ${issue}`);
      hasIssues = true;
    });
  } else {
    console.log('  ✅ Все barrel-экспорты существуют');
  }

  // Результат
  console.log('\n' + '='.repeat(50));
  if (hasIssues) {
    console.log('❌ Найдены проблемы с импортами!');
    process.exit(1);
  } else {
    console.log('✅ Все импорты в порядке!');
  }
}

// Запуск
if (require.main === module) {
  main();
}

module.exports = { main, checkRelativePathDepth, checkForbiddenPatterns, findDuplicateImports };
