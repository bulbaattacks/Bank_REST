#### Описание проекта:
REST API сервис для системы управления банковскими картами.

#### Стек проекта:
- Java 21
- JUnit 5
- Spring Boot
- Spring Security and JWT
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Liquibase
- Maven
- Docker

#### Запуск тестов:
1. Выполнить в корне проекта:
```
mvn test
```
#### Запуск приложения:
1. Запустить Docker:
```
docker compose up -d
```
2. Выполнить в корне проекта:
```
mvn spring-boot:run
```

#### API:
1. Открыть Swagger:
```
http://localhost:8080/swagger-ui/index.html
```
