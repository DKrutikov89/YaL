# Yandex Lavka Delivery Service

Система управления доставкой для Яндекс Лавка.

## Требования

- Java 17+
- Maven 3.6+

## Запуск

```bash
# Сборка проекта
mvn clean compile

# Запуск приложения
mvn spring-boot:run
```

## API

Приложение запускается на порту 8080.

### Endpoints

- `POST /couriers` - Создание курьеров
- `GET /couriers` - Получение списка курьеров
- `GET /couriers/{id}` - Получение курьера по ID

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Итерации разработки

- [x] Итерация 1: Основы Spring Boot и REST API
- [ ] Итерация 2: База данных и JPA
- [ ] Итерация 3: Заказы и связи между сущностями
