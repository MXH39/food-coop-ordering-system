# Changelog

All notable changes to this project are documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.0.0] - 2026-09-26

### Added
- Spring Boot application skeleton (Java 8, Maven build).
- Domain model: `User`, `Product`, `PickupWeek`, `Order`, `OrderItem`.
- REST API for products, pickup weeks, members and orders.
- Ordering business rules: order deadline, week status check, stock
  management on confirmation/cancellation.
- Static frontend: member ordering page, order history page, volunteer
  admin page.
- MySQL 8.0 reference schema and idempotent seed data.
- Externalized deployment configuration (`application.properties` with
  environment variable overrides, `prod` profile).
- Project documentation: README, configuration management guide.
