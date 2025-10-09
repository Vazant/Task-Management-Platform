#!/bin/bash

# Скрипт для развертывания микросервисов
# Usage: ./deploy.sh [environment] [service]

set -e

ENVIRONMENT=${1:-local}
SERVICE=${2:-all}

echo "🚀 Развертывание микросервисов"
echo "Environment: $ENVIRONMENT"
echo "Service: $SERVICE"

# Функция для развертывания в Docker
deploy_docker() {
    echo "📦 Развертывание в Docker..."
    
    # Остановка существующих контейнеров
    docker-compose -f infrastructure/docker/docker-compose.yml down
    
    # Сборка образов сервисов
    if [ "$SERVICE" = "all" ]; then
        echo "🔨 Сборка всех сервисов..."
        for service_dir in services/*/; do
            if [ -f "$service_dir/Dockerfile" ]; then
                service_name=$(basename "$service_dir")
                echo "Сборка $service_name..."
                docker build -t taskboard/$service_name:latest "$service_dir"
            fi
        done
    else
        echo "🔨 Сборка сервиса $SERVICE..."
        docker build -t taskboard/$SERVICE:latest "services/$SERVICE/"
    fi
    
    # Запуск инфраструктуры
    echo "🏗️ Запуск инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres redis kafka zookeeper
    
    # Ожидание готовности инфраструктуры
    echo "⏳ Ожидание готовности инфраструктуры..."
    sleep 30
    
    # Запуск сервисов
    if [ "$SERVICE" = "all" ]; then
        echo "🚀 Запуск всех сервисов..."
        docker-compose -f infrastructure/docker/docker-compose.yml up -d
    else
        echo "🚀 Запуск сервиса $SERVICE..."
        docker-compose -f infrastructure/docker/docker-compose.yml up -d "$SERVICE"
    fi
    
    echo "✅ Развертывание завершено!"
}

# Функция для развертывания в Kubernetes
deploy_kubernetes() {
    echo "☸️ Развертывание в Kubernetes..."
    
    # Применение namespace
    kubectl apply -f infrastructure/kubernetes/namespace.yaml
    
    # Применение конфигураций
    kubectl apply -f infrastructure/kubernetes/configmap.yaml
    kubectl apply -f infrastructure/kubernetes/secrets.yaml
    kubectl apply -f infrastructure/kubernetes/rbac.yaml
    kubectl apply -f infrastructure/kubernetes/network-policy.yaml
    
    # Применение манифестов сервисов
    if [ "$SERVICE" = "all" ]; then
        echo "🚀 Развертывание всех сервисов..."
        for service_dir in services/*/; do
            if [ -f "$service_dir/k8s.yaml" ]; then
                service_name=$(basename "$service_dir")
                echo "Развертывание $service_name..."
                kubectl apply -f "$service_dir/k8s.yaml"
            fi
        done
    else
        echo "🚀 Развертывание сервиса $SERVICE..."
        kubectl apply -f "services/$SERVICE/k8s.yaml"
    fi
    
    # Применение HPA
    kubectl apply -f infrastructure/kubernetes/hpa.yaml
    
    # Применение PDB
    kubectl apply -f infrastructure/kubernetes/pod-disruption-budget.yaml
    
    echo "✅ Развертывание завершено!"
}

# Функция для развертывания в локальной среде
deploy_local() {
    echo "💻 Развертывание в локальной среде..."
    
    # Запуск инфраструктуры
    echo "🏗️ Запуск инфраструктуры..."
    docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres redis kafka zookeeper
    
    # Ожидание готовности инфраструктуры
    echo "⏳ Ожидание готовности инфраструктуры..."
    sleep 30
    
    # Запуск сервисов
    if [ "$SERVICE" = "all" ]; then
        echo "🚀 Запуск всех сервисов..."
        for service_dir in services/*/; do
            if [ -f "$service_dir/pom.xml" ]; then
                service_name=$(basename "$service_dir")
                echo "Запуск $service_name..."
                cd "$service_dir"
                mvn spring-boot:run &
                cd - > /dev/null
            fi
        done
    else
        echo "🚀 Запуск сервиса $SERVICE..."
        cd "services/$SERVICE"
        mvn spring-boot:run
        cd - > /dev/null
    fi
    
    echo "✅ Развертывание завершено!"
}

# Основная логика
case $ENVIRONMENT in
    local)
        deploy_local
        ;;
    docker)
        deploy_docker
        ;;
    kubernetes|k8s)
        deploy_kubernetes
        ;;
    *)
        echo "❌ Неподдерживаемая среда: $ENVIRONMENT"
        echo "Поддерживаемые среды: local, docker, kubernetes"
        exit 1
        ;;
esac

echo "🎉 Развертывание завершено успешно!"
echo ""
echo "📊 Полезные команды:"
echo "  docker-compose -f infrastructure/docker/docker-compose.yml logs -f"
echo "  kubectl get pods -n taskboard"
echo "  kubectl logs -f deployment/$SERVICE -n taskboard"
