#!/bin/bash

# Скрипт для инициализации MinIO bucket для аватаров
# Запускается после старта MinIO контейнера

echo "Waiting for MinIO to be ready..."
until curl -s http://localhost:9000/minio/health/live; do
    sleep 1
done

echo "MinIO is ready. Creating bucket..."

# Создание bucket для аватаров
mc alias set myminio http://localhost:9000 minioadmin minioadmin
mc mb myminio/taskboard-avatars --ignore-existing

# Настройка lifecycle правил (удаление старых версий через 30 дней)
mc ilm add myminio/taskboard-avatars --expiry-days 30

# Настройка CORS для веб-загрузки
mc anonymous set download myminio/taskboard-avatars

echo "MinIO bucket 'taskboard-avatars' created successfully!"
echo "Bucket URL: http://localhost:9000/taskboard-avatars"
echo "MinIO Console: http://localhost:9001"
