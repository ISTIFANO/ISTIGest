# ISTIGest — Food Delivery System

Spring Boot food delivery API built with **Clean Architecture**. Business logic stays independent from frameworks, databases, and messaging.

---

## Table of contents

1. [Overview](#overview)
2. [Tech stack](#tech-stack)
3. [Clean Architecture layers](#clean-architecture-layers)
4. [Project structure](#project-structure)
5. [How it works](#how-it-works)
6. [Prerequisites](#prerequisites)
7. [Quick start](#quick-start)
8. [Configuration](#configuration)
9. [API reference](#api-reference)
10. [Messaging (RabbitMQ)](#messaging-rabbitmq)
11. [Build & test](#build--test)
12. [Design principles](#design-principles)

---

## Overview

The system supports:

| Feature | Description |
|--------|-------------|
| Menu management | Create and list menu items (name + price) |
| Order creation | Place an order from existing menu item IDs |
| Async delivery | Publish order events to RabbitMQ and process delivery in the background |

Default server port: **8081**

---

## Tech stack

- Java 17
- Spring Boot 3.4
- Spring Web (REST)
- Spring Data JPA + PostgreSQL
- Spring AMQP + RabbitMQ
- Lombok
- Maven (wrapper included)
- Docker Compose (Postgres + RabbitMQ)

---

## Clean Architecture layers

Dependencies point **inward**. Outer layers know about inner ones; the domain does not depend on frameworks.

```
┌─────────────────────────────────────────────┐
│  Framework     Controllers, DTOs, HTTP      │
├─────────────────────────────────────────────┤
│  Infrastructure  RabbitMQ, delivery logic   │
├─────────────────────────────────────────────┤
│  Application   Use cases, event publisher   │
├─────────────────────────────────────────────┤
│  Domain        Entities, events, repository │
└─────────────────────────────────────────────┘
```

### Domain (`com.fooddelivery.domain`)

Core business model — no UI, no messaging details.

| Class | Role |
|-------|------|
| `MenuEntity` | Menu item (id, name, price) mapped to table `menu` |
| `OrderItem` | Item snapshot inside an order event |
| `OrderCreateEvent` | Domain event when an order is created |
| `MenuRepository` | Persistence port (Spring Data JPA interface) |

### Application (`com.fooddelivery.application`)

Orchestrates use cases.

| Class | Role |
|-------|------|
| `MenuUseCase` | List menu / add menu item |
| `OrderUseCase` | Validate items, build event, publish |
| `OrderEventPublisher` | Sends `OrderCreateEvent` JSON to RabbitMQ |

### Infrastructure (`com.fooddelivery.infrastructure`)

Talks to external systems.

| Class | Role |
|-------|------|
| `DeliveryListener` | Consumes messages from `delivery.queue` |
| `DeliverOrderComponent` | Processes delivery (logs order items) |
| `RabbitMQConfig` | Declares exchange, queue, and binding |

### Framework (`com.fooddelivery.framework`)

HTTP edge of the app.

| Class | Role |
|-------|------|
| `MenuController` | `GET/POST /menu` |
| `OrderController` | `POST /order` |
| DTOs | Request/response payloads |

---

## Project structure

```
src/main/java/com/fooddelivery/
├── FoodDeliveryApplication.java
├── application/
│   ├── service/
│   │   └── OrderEventPublisher.java
│   └── usecase/
│       ├── MenuUseCase.java
│       └── OrderUseCase.java
├── domain/
│   ├── entity/
│   │   └── MenuEntity.java
│   ├── event/
│   │   ├── OrderCreateEvent.java
│   │   └── OrderItem.java
│   └── repository/
│       └── MenuRepository.java
├── framework/
│   ├── controller/
│   │   ├── MenuController.java
│   │   └── OrderController.java
│   └── dto/
│       ├── CreateMenuItemRequest.java
│       ├── CreateOrderRequest.java
│       └── MenuResponse.java
└── infrastructure/
    ├── config/
    │   └── RabbitMQConfig.java
    ├── listener/
    │   └── DeliveryListener.java
    └── service/
        └── DeliverOrderComponent.java

src/main/resources/
└── application.properties

docker-compose.yml
pom.xml
```

---

## How it works

### Add a menu item

1. Client → `POST /menu`
2. `MenuController` → `MenuUseCase`
3. Entity saved via `MenuRepository` (PostgreSQL)
4. Response returns created item (id, name, price)

### Create an order

1. Client → `POST /order` with menu item UUIDs
2. `OrderUseCase` loads items from `MenuRepository`
3. Builds `OrderCreateEvent` with `OrderItem` list
4. `OrderEventPublisher` publishes JSON to exchange `order.exchange` (routing key `order.created`)
5. `DeliveryListener` receives the message from `delivery.queue`
6. `DeliverOrderComponent` runs delivery handling (async)

```
POST /order
    → OrderUseCase
        → OrderEventPublisher → RabbitMQ (order.exchange)
                                      ↓
                              delivery.queue
                                      ↓
                              DeliveryListener
                                      ↓
                              DeliverOrderComponent
```

---

## Prerequisites

- JDK 17+
- Docker & Docker Compose (for Postgres + RabbitMQ)
- Or local installs of PostgreSQL 16 and RabbitMQ 3

---

## Quick start

### 1. Start dependencies

```bash
docker compose up -d
```

This starts:

| Service | Port | Credentials |
|---------|------|-------------|
| PostgreSQL | `5432` | `delivery_admin` / `admin` — DB `food_delivery` |
| RabbitMQ | `5672` (AMQP), `15672` (UI) | `guest` / `guest` |

RabbitMQ management UI: http://localhost:15672

### 2. Run the app

Windows (PowerShell):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

App URL: http://localhost:8081

### 3. Try the API

```bash
# Add menu items
curl -X POST http://localhost:8081/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Burger\",\"price\":9.99}"

curl -X POST http://localhost:8081/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Fries\",\"price\":3.50}"

# List menu
curl http://localhost:8081/menu

# Create order (use IDs from the menu response)
curl -X POST http://localhost:8081/order ^
  -H "Content-Type: application/json" ^
  -d "{\"itemIds\":[\"<uuid-1>\",\"<uuid-2>\"]}"
```

On bash, use `\` instead of `^` for line continuation.

---

## Configuration

File: `src/main/resources/application.properties`

| Property | Default | Meaning |
|----------|---------|---------|
| `server.port` | `8081` | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/food_delivery` | Database |
| `spring.datasource.username` | `delivery_admin` | DB user |
| `spring.datasource.password` | `admin` | DB password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto schema update |
| `spring.rabbitmq.host` | `localhost` | Broker host |
| `spring.rabbitmq.port` | `5672` | Broker port |
| `spring.rabbitmq.queue` | `delivery.queue` | Consumer queue |

---

## API reference

Base URL: `http://localhost:8081`

### `GET /menu`

Returns all menu items.

**Response `200`**

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "Burger",
    "price": 9.99
  }
]
```

---

### `POST /menu`

Creates a menu item.

**Request**

```json
{
  "name": "Burger",
  "price": 9.99
}
```

**Response `200`**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Burger",
  "price": 9.99
}
```

---

### `POST /order`

Creates an order from existing menu item IDs and publishes a delivery event.

**Request**

```json
{
  "itemIds": [
    "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "b2c3d4e5-f6a7-8901-bcde-f12345678901"
  ]
}
```

**Response `200`** — empty body (event published asynchronously)

Invalid / unknown IDs are simply skipped (items not found are not added to the event).

---

## Messaging (RabbitMQ)

| Setting | Value |
|---------|--------|
| Exchange | `order.exchange` (topic) |
| Routing key | `order.created` |
| Queue | `delivery.queue` |

Event payload shape:

```json
{
  "orderItems": [
    {
      "itemName": "Burger",
      "price": 9.99
    }
  ]
}
```

`RabbitMQConfig` binds the queue to the exchange so publish and consume stay in sync.

---

## Build & test

```bash
# Compile
./mvnw -DskipTests compile

# Run tests
./mvnw test

# Package JAR
./mvnw -DskipTests package
java -jar target/food-delivery-0.0.1-SNAPSHOT.jar
```

---

## Design principles

1. **Separation of concerns** — HTTP, use cases, domain, and messaging live in different packages.
2. **Dependency inversion** — use cases depend on `MenuRepository`, not SQL.
3. **Business rules independence** — order/menu rules can be tested without controllers.
4. **Async side effects** — delivery runs after the order event is published, so the HTTP path stays light.

---

## License

Private / educational project (ISTIGest).
