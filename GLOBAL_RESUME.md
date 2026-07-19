# ISTIGest — Global Resume

End-to-end **food delivery** stack: Clean Architecture Spring Boot API + Angular dashboard.

| Piece | Path (local) | GitHub |
|-------|----------------|--------|
| **API** | `C:\Users\aamir\Desktop\ALL\ISTIGest` | https://github.com/ISTIFANO/ISTIGest |
| **UI** | `C:\Users\aamir\Desktop\ALL\ISTIGest-ui` | https://github.com/ISTIFANO/ISTIGest-ui |

> GitHub may also show renamed remotes (`Food-delivery-api` / `Food-delivery-ui`).

---

## 1. What the system does

1. Manage **menu items** (name + price) in PostgreSQL  
2. Create **orders** from selected menu IDs  
3. Publish an **order event** to RabbitMQ  
4. Process **delivery** asynchronously via a queue listener  
5. Operate everything from an **Angular dashboard** (menu, orders, workspace)

```text
┌─────────────────┐     /api (proxy)      ┌──────────────────┐
│  Angular UI     │ ───────────────────► │  Spring Boot API │
│  :4200          │                      │  :8081           │
└─────────────────┘                      └────────┬─────────┘
                                                  │
                         ┌────────────────────────┼────────────────────────┐
                         ▼                        ▼                        ▼
                  PostgreSQL :5432          RabbitMQ :5672            pgAdmin :5050
                  (menu table)              (delivery.queue)
```

---

## 2. Backend (ISTIGest) — Clean Architecture

### Tech stack

- Java 17, Spring Boot 3.4  
- Spring Web, Data JPA, AMQP  
- PostgreSQL 16, RabbitMQ 3  
- SpringDoc OpenAPI (Swagger UI)  
- Docker Compose, Maven Wrapper  
- Config via `.env` (gitignored)

### Layers

| Layer | Package | Responsibility |
|-------|---------|----------------|
| **Domain** | `domain/` | `MenuEntity`, `OrderItem`, `OrderCreateEvent`, `MenuRepository` |
| **Application** | `application/` | `MenuUseCase`, `OrderUseCase`, `OrderEventPublisher` |
| **Infrastructure** | `infrastructure/` | RabbitMQ config, `DeliveryListener`, `DeliverOrderComponent` |
| **Framework** | `framework/` | Controllers, DTOs, OpenAPI config |

### Main API endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/menu` | List menu items |
| `POST` | `/menu` | Add menu item `{ "name", "price" }` |
| `POST` | `/order` | Create order `{ "itemIds": ["uuid", ...] }` |

### Messaging

| Setting | Value |
|---------|--------|
| Exchange | `order.exchange` (topic) |
| Routing key | `order.created` |
| Queue | `delivery.queue` (from env) |

Flow: `POST /order` → `OrderUseCase` → `OrderEventPublisher` → RabbitMQ → `DeliveryListener` → `DeliverOrderComponent`

### Local URLs (API side)

| Service | URL / port |
|---------|------------|
| API | http://localhost:8081 |
| Swagger | http://localhost:8081/swagger-ui.html |
| OpenAPI JSON | http://localhost:8081/v3/api-docs |
| pgAdmin | http://localhost:5050 |
| RabbitMQ UI | http://localhost:15672 |
| PostgreSQL | `localhost:5432` |

### Env (API)

Copy `.env.example` → `.env`. Main keys: `POSTGRES_*`, `SPRING_DATASOURCE_*`, `RABBITMQ_*`, `PGADMIN_*`, `SERVER_PORT`.

### Run (API)

```powershell
cd C:\Users\aamir\Desktop\ALL\ISTIGest
docker compose up -d
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd spring-boot:run
```

---

## 3. Frontend (ISTIGest-ui) — Angular dashboard

### Tech stack

- Angular 21 (standalone components)  
- Signals store, HttpClient  
- SCSS, Fraunces + Outfit fonts  
- Dev proxy `/api` → backend  

### Architecture

```text
src/app
├── components/
│   ├── page-structure/   # Header, Footer, LoadingScreen
│   └── link-items/       # SectionBoard, ItemCard
├── core/
│   ├── config/           # Dashboard sections (conf-driven)
│   ├── models/
│   └── services/         # FoodApiService
├── store/                # AppStore (signals)
├── utils/
├── views/                # Home, Menu, Orders, Workspace
└── environments/         # API base URL, titles, Swagger link
```

### Routes

| Route | Page |
|-------|------|
| `/` | Home — section dashboard |
| `/menu` | Menu board (list / add) |
| `/orders` | Select items & place order |
| `/workspace` | Ops snapshot |

### Env (UI)

- `.env` / `.env.example` (gitignored `.env`)  
- Runtime values in `src/environments/environment*.ts`  
- Keep `proxy.conf.json` target aligned with `API_ORIGIN`  
- `.vscode/` is gitignored  

### Run (UI)

```powershell
cd C:\Users\aamir\Desktop\ALL\ISTIGest-ui
cp .env.example .env   # first time
npm install
npm start
```

UI: http://localhost:4200

---

## 4. Full stack quick start

```powershell
# 1) Infra + API
cd C:\Users\aamir\Desktop\ALL\ISTIGest
docker compose up -d
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd spring-boot:run

# 2) UI (other terminal)
cd C:\Users\aamir\Desktop\ALL\ISTIGest-ui
npm start
```

Then open:

1. http://localhost:4200 — dashboard  
2. http://localhost:8081/swagger-ui.html — API docs  
3. http://localhost:5050 — pgAdmin (`admin@fooddelivery.local` / value from `.env`)  

---

## 5. Design principles

**Backend**

- Separation of concerns (domain independent of HTTP / messaging)  
- Dependency inversion (use cases depend on repository abstractions)  
- Async side effects for delivery via events  

**Frontend**

- Config-driven dashboard sections  
- Thin views + shared components  
- Central store for menu / selection / orders  

---

## 6. Project layout (repos)

```text
Desktop/ALL/
├── ISTIGest/              # Spring Boot API (this repo)
│   ├── docker-compose.yml
│   ├── .env / .env.example
│   ├── pom.xml
│   ├── GLOBAL_RESUME.md   # this file
│   └── src/main/java/com/fooddelivery/...
└── ISTIGest-ui/           # Angular UI (separate repo)
    ├── .env / .env.example
    ├── proxy.conf.json
    └── src/app/...
```

---

## 7. Typical demo path

1. Start Docker + API + UI  
2. In UI **Menu**: add “Burger”, “Fries”  
3. In UI **Orders**: select items → **Place order**  
4. Check API/Rabbit logs for delivery processing  
5. Optional: inspect `menu` table in pgAdmin  

---

*ISTIGest ecosystem — API Clean Architecture + Angular control board.*
