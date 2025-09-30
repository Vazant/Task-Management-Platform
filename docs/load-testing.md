# Нагрузочное тестирование Task Management Platform

## Обзор

Данный документ описывает процесс проведения нагрузочного тестирования для Task Management Platform с использованием Apache JMeter.

## Установка и настройка

### Требования
- Apache JMeter 5.6.2 или выше
- Java 11 или выше
- Запущенный backend сервер на порту 8080

### Установка JMeter

#### macOS (с Homebrew)
```bash
brew install jmeter
```

#### Linux (Ubuntu/Debian)
```bash
wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.2.tgz
tar -xzf apache-jmeter-5.6.2.tgz
sudo mv apache-jmeter-5.6.2 /opt/
export JMETER_HOME=/opt/apache-jmeter-5.6.2
```

#### Windows
1. Скачайте JMeter с [официального сайта](https://jmeter.apache.org/download_jmeter.cgi)
2. Распакуйте архив
3. Установите переменную окружения `JMETER_HOME`

## Тестовые сценарии

### 1. Light Load (Легкая нагрузка)
- **Пользователи**: 10
- **Время нарастания**: 60 секунд
- **Длительность**: 5 минут
- **Цель**: Проверка базовой функциональности

### 2. Medium Load (Средняя нагрузка)
- **Пользователи**: 25
- **Время нарастания**: 2 минуты
- **Длительность**: 10 минут
- **Цель**: Проверка производительности при обычной нагрузке

### 3. Heavy Load (Высокая нагрузка)
- **Пользователи**: 50
- **Время нарастания**: 3 минуты
- **Длительность**: 15 минут
- **Цель**: Проверка производительности при пиковой нагрузке

### 4. Stress Load (Стресс-тестирование)
- **Пользователи**: 100
- **Время нарастания**: 5 минут
- **Длительность**: 20 минут
- **Цель**: Определение точки отказа системы

## Запуск тестов

### Автоматический запуск
```bash
# Перейдите в директорию проекта
cd /path/to/Task-Management-Platform

# Запустите скрипт нагрузочного тестирования
./scripts/run-load-tests.sh
```

### Ручной запуск
```bash
# Установите переменную окружения
export JMETER_HOME=/path/to/apache-jmeter-5.6.2

# Запустите тест
$JMETER_HOME/bin/jmeter -n -t scripts/load-testing.jmx -l results.jtl -e -o report/
```

## Анализ результатов

### Ключевые метрики

#### Время отклика
- **Цель**: < 200ms для 95% запросов
- **Приемлемо**: < 500ms для 95% запросов
- **Критично**: > 1000ms

#### Пропускная способность
- **Цель**: > 1000 RPS
- **Приемлемо**: > 500 RPS
- **Критично**: < 100 RPS

#### Ошибки
- **Цель**: < 0.1%
- **Приемлемо**: < 1%
- **Критично**: > 5%

### Отчеты JMeter

#### HTML отчет
```bash
# Генерация HTML отчета
$JMETER_HOME/bin/jmeter -g results.jtl -o html-report/
```

#### Графики производительности
- **Response Time Over Time**: Время отклика во времени
- **Throughput Over Time**: Пропускная способность во времени
- **Active Threads Over Time**: Количество активных пользователей
- **Response Time Distribution**: Распределение времени отклика

## Оптимизация на основе результатов

### Backend оптимизация

#### База данных
```sql
-- Создание индексов для часто используемых запросов
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);
CREATE INDEX idx_projects_user_id ON projects(user_id);
```

#### Connection Pool
```properties
# application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

#### Кэширование
```java
@Cacheable(value = "tasks", key = "#userId")
public List<Task> getUserTasks(Long userId) {
    return taskRepository.findByUserId(userId);
}
```

### Frontend оптимизация

#### Lazy Loading
```typescript
const routes: Routes = [
  {
    path: 'tasks',
    loadChildren: () => import('./features/tasks/tasks.module').then(m => m.TasksModule)
  }
];
```

#### OnPush Change Detection
```typescript
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskListComponent {
  // Component implementation
}
```

## Мониторинг в реальном времени

### Spring Boot Actuator
```properties
# Включение метрик
management.endpoints.web.exposure.include=health,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

### Prometheus + Grafana
```yaml
# docker-compose.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
  
  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

## Troubleshooting

### Частые проблемы

#### 1. Connection Refused
```
Error: Connection refused to localhost:8080
```
**Решение**: Убедитесь, что backend сервер запущен на порту 8080

#### 2. Out of Memory
```
Error: java.lang.OutOfMemoryError: Java heap space
```
**Решение**: Увеличьте heap size для JMeter
```bash
export JVM_ARGS="-Xms1g -Xmx4g"
```

#### 3. High Response Times
**Возможные причины**:
- Медленные SQL запросы
- Недостаток ресурсов сервера
- Проблемы с сетью

**Решения**:
- Оптимизация SQL запросов
- Увеличение ресурсов сервера
- Настройка connection pool

### Логирование

#### Backend логи
```properties
# application.properties
logging.level.com.taskboard.api=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

#### JMeter логи
```bash
# Запуск с подробным логированием
$JMETER_HOME/bin/jmeter -n -t test.jmx -l results.jtl -j jmeter.log
```

## Автоматизация

### CI/CD интеграция

#### GitHub Actions
```yaml
name: Load Testing
on:
  schedule:
    - cron: '0 2 * * *'  # Ежедневно в 2:00 AM

jobs:
  load-test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Setup Java
      uses: actions/setup-java@v3
      with:
        java-version: '17'
    - name: Setup JMeter
      run: |
        wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.2.tgz
        tar -xzf apache-jmeter-5.6.2.tgz
        echo "JMETER_HOME=$PWD/apache-jmeter-5.6.2" >> $GITHUB_ENV
    - name: Run Load Tests
      run: |
        ./scripts/run-load-tests.sh
    - name: Upload Results
      uses: actions/upload-artifact@v3
      with:
        name: load-test-results
        path: load-test-results/
```

## Заключение

Нагрузочное тестирование является критически важным этапом в разработке и поддержке Task Management Platform. Регулярное проведение тестов помогает:

1. **Выявить узкие места** в производительности
2. **Обеспечить стабильность** при росте нагрузки
3. **Планировать масштабирование** системы
4. **Поддерживать качество** пользовательского опыта

Рекомендуется проводить нагрузочное тестирование:
- После каждого крупного релиза
- При изменении архитектуры
- Перед развертыванием в production
- При планировании масштабирования
