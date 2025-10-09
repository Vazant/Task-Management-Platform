#!/bin/bash

# Скрипт для запуска тестов микросервисов
# Usage: ./test.sh [service] [test-type]

set -e

SERVICE=${1:-all}
TEST_TYPE=${2:-all}

echo "🧪 Запуск тестов микросервисов"
echo "Service: $SERVICE"
echo "Test Type: $TEST_TYPE"

# Функция для запуска unit тестов
run_unit_tests() {
    echo "🔬 Запуск unit тестов..."
    
    if [ "$SERVICE" = "all" ]; then
        for service_dir in services/*/; do
            if [ -f "$service_dir/pom.xml" ]; then
                service_name=$(basename "$service_dir")
                echo "Тестирование $service_name..."
                cd "$service_dir"
                mvn test -Dtest="*Test"
                cd - > /dev/null
            fi
        done
    else
        echo "Тестирование $SERVICE..."
        cd "services/$SERVICE"
        mvn test -Dtest="*Test"
        cd - > /dev/null
    fi
}

# Функция для запуска integration тестов
run_integration_tests() {
    echo "🔗 Запуск integration тестов..."
    
    # Запуск инфраструктуры для тестов
    echo "🏗️ Запуск тестовой инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres redis kafka zookeeper
    
    # Ожидание готовности инфраструктуры
    echo "⏳ Ожидание готовности инфраструктуры..."
    sleep 30
    
    if [ "$SERVICE" = "all" ]; then
        for service_dir in services/*/; do
            if [ -f "$service_dir/pom.xml" ]; then
                service_name=$(basename "$service_dir")
                echo "Integration тестирование $service_name..."
                cd "$service_dir"
                mvn test -Dtest="*IntegrationTest"
                cd - > /dev/null
            fi
        done
    else
        echo "Integration тестирование $SERVICE..."
        cd "services/$SERVICE"
        mvn test -Dtest="*IntegrationTest"
        cd - > /dev/null
    fi
    
    # Остановка тестовой инфраструктуры
    echo "🛑 Остановка тестовой инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml down
}

# Функция для запуска E2E тестов
run_e2e_tests() {
    echo "🎭 Запуск E2E тестов..."
    
    # Запуск полной инфраструктуры
    echo "🏗️ Запуск полной инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml up -d
    
    # Ожидание готовности всех сервисов
    echo "⏳ Ожидание готовности всех сервисов..."
    sleep 60
    
    # Запуск E2E тестов
    if [ "$SERVICE" = "all" ]; then
        for service_dir in services/*/; do
            if [ -f "$service_dir/pom.xml" ]; then
                service_name=$(basename "$service_dir")
                echo "E2E тестирование $service_name..."
                cd "$service_dir"
                mvn test -Dtest="*E2ETest"
                cd - > /dev/null
            fi
        done
    else
        echo "E2E тестирование $SERVICE..."
        cd "services/$SERVICE"
        mvn test -Dtest="*E2ETest"
        cd - > /dev/null
    fi
    
    # Остановка инфраструктуры
    echo "🛑 Остановка инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml down
}

# Функция для запуска нагрузочных тестов
run_load_tests() {
    echo "⚡ Запуск нагрузочных тестов..."
    
    # Запуск инфраструктуры
    echo "🏗️ Запуск инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml up -d
    
    # Ожидание готовности
    echo "⏳ Ожидание готовности..."
    sleep 60
    
    # Запуск JMeter тестов
    if [ -f "scripts/load-testing.jmx" ]; then
        echo "🚀 Запуск JMeter тестов..."
        jmeter -n -t scripts/load-testing.jmx -l results.jtl
    else
        echo "⚠️ JMeter тесты не найдены"
    fi
    
    # Остановка инфраструктуры
    echo "🛑 Остановка инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml down
}

# Функция для генерации отчета о покрытии
generate_coverage_report() {
    echo "📊 Генерация отчета о покрытии..."
    
    if [ "$SERVICE" = "all" ]; then
        for service_dir in services/*/; do
            if [ -f "$service_dir/pom.xml" ]; then
                service_name=$(basename "$service_dir")
                echo "Генерация отчета для $service_name..."
                cd "$service_dir"
                mvn jacoco:report
                cd - > /dev/null
            fi
        done
    else
        echo "Генерация отчета для $SERVICE..."
        cd "services/$SERVICE"
        mvn jacoco:report
        cd - > /dev/null
    fi
}

# Основная логика
case $TEST_TYPE in
    unit)
        run_unit_tests
        ;;
    integration)
        run_integration_tests
        ;;
    e2e)
        run_e2e_tests
        ;;
    load)
        run_load_tests
        ;;
    coverage)
        generate_coverage_report
        ;;
    all)
        run_unit_tests
        run_integration_tests
        generate_coverage_report
        ;;
    *)
        echo "❌ Неподдерживаемый тип тестов: $TEST_TYPE"
        echo "Поддерживаемые типы: unit, integration, e2e, load, coverage, all"
        exit 1
        ;;
esac

echo "🎉 Тестирование завершено успешно!"
echo ""
echo "📊 Результаты тестов:"
echo "  Unit тесты: target/surefire-reports/"
echo "  Integration тесты: target/surefire-reports/"
echo "  Покрытие кода: target/site/jacoco/"
