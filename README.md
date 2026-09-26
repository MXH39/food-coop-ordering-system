# Community Food Co-operative Ordering System

A web application for a volunteer-run community food cooperative that manages
weekly pickup ordering. Members browse the weekly product catalog, place an
order before the deadline, and pick up their food during the weekly pickup.
Volunteers manage products, pickup weeks and the order lifecycle.

Built for ISYS3001 Assessment 2 (Configuration and Procurement Management).

## Tech Stack

| Layer      | Technology                                   |
|------------|----------------------------------------------|
| Backend    | Spring Boot 2.7 (Java 8 / JDK 1.8)           |
| Data       | Spring Data JPA, Hibernate, MySQL 8.0        |
| Build      | Apache Maven 3.6.1                           |
| Frontend   | HTML5, CSS3, vanilla JavaScript (Node.js 20 used for dev tooling) |
| VCS        | Git with feature-branch workflow              |

## Features

- **Members** browse active products, place orders for the open pickup week,
  view their order history and cancel orders.
- **Volunteers** create pickup weeks, open/close ordering, add or deactivate
  products, confirm orders (stock is reduced on confirmation), mark orders as
  ready for pickup, complete or cancel them.
- **Business rules**: orders only while a week is OPEN and before its order
  deadline; stock is validated; cancelled confirmed orders return stock.

## Project Structure

```
src/main/java/com/foodcoop/
├── FoodCoopApplication.java      # Spring Boot entry point
├── controller/                   # REST controllers + global error handling
├── dto/                          # API response DTOs
├── model/                        # JPA entities (User, Product, PickupWeek, Order, OrderItem)
├── repository/                   # Spring Data JPA repositories
└── service/                      # Business logic
src/main/resources/
├── application.properties        # Config (externalized via environment variables)
├── application-prod.properties   # Production profile example
├── data.sql                      # Idempotent seed data
├── db/schema.sql                 # Reference schema (MySQL 8.0)
└── static/                       # Frontend (index, orders, admin pages)
```

## Getting Started

### Prerequisites

- JDK 1.8
- MySQL 8.0 running on localhost:3306 (or set `DB_URL`)
- Maven 3.6.1

### Database setup

The application creates tables automatically. To create them manually:

```sql
source src/main/resources/db/schema.sql
```

### Run

```bash
mvn spring-boot:run
```

Or with environment-specific configuration:

```bash
DB_URL=jdbc:mysql://localhost:3306/food_coop DB_USERNAME=root DB_PASSWORD=secret \
  mvn spring-boot:run
```

Then open http://localhost:8080

- `index.html` – member ordering page
- `orders.html` – member order history
- `admin.html` – volunteer administration

### Package

```bash
mvn clean package
java -jar target/food-coop-ordering-system-1.0.0.jar
```

## REST API Overview

| Method | Path                        | Description                          |
|--------|-----------------------------|--------------------------------------|
| GET    | `/api/products`             | Active product catalog               |
| POST   | `/api/products`             | Create product (volunteer)           |
| PUT    | `/api/products/{id}`        | Update product                       |
| GET    | `/api/pickup-weeks`         | List pickup weeks                    |
| GET    | `/api/pickup-weeks/open`    | Currently open week                  |
| POST   | `/api/pickup-weeks`         | Create pickup week (volunteer)       |
| GET    | `/api/members`              | List members                         |
| POST   | `/api/members`              | Register member                      |
| POST   | `/api/orders`               | Place order                          |
| GET    | `/api/orders?memberId=`     | Orders of one member                 |
| GET    | `/api/orders?pickupWeekId=` | Orders for one week (volunteer)      |
| PUT    | `/api/orders/{id}/status`   | Update order status (volunteer)      |
| PUT    | `/api/orders/{id}/cancel`   | Cancel own order (member)            |

## Configuration Management

See [docs/configuration-management.md](docs/configuration-management.md) for the
branching strategy, version control conventions and deployment configuration
used in this repository.
