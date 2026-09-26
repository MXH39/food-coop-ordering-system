-- =============================================================
-- Reference schema for the Food Co-op Ordering System (MySQL 8.0)
-- In the application, Hibernate (ddl-auto=update) creates these
-- tables automatically. This file documents the structure and can
-- be used to create the database manually.
-- =============================================================

CREATE DATABASE IF NOT EXISTS food_coop
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_coop;

CREATE TABLE IF NOT EXISTS users (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100)  NOT NULL,
    email     VARCHAR(150)  NOT NULL UNIQUE,
    phone     VARCHAR(30),
    role      VARCHAR(20)   NOT NULL DEFAULT 'MEMBER'   -- MEMBER | VOLUNTEER_ADMIN
);

CREATE TABLE IF NOT EXISTS products (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(120)   NOT NULL,
    description    VARCHAR(500),
    category       VARCHAR(50),
    unit           VARCHAR(20),
    price          DECIMAL(10, 2) NOT NULL,
    stock_quantity INT            NOT NULL DEFAULT 0,
    active         TINYINT(1)     NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS pickup_weeks (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_start     DATE NOT NULL,
    week_end       DATE NOT NULL,
    order_deadline DATE NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'OPEN',  -- OPEN | CLOSED | COMPLETED
    notes          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orders (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT NOT NULL,
    pickup_week_id BIGINT NOT NULL,
    status         VARCHAR(30)    NOT NULL DEFAULT 'PENDING', -- PENDING | CONFIRMED | READY_FOR_PICKUP | COMPLETED | CANCELLED
    total_amount   DECIMAL(10, 2) NOT NULL DEFAULT 0,
    created_at     DATETIME       NOT NULL,
    CONSTRAINT fk_order_member      FOREIGN KEY (member_id)      REFERENCES users (id),
    CONSTRAINT fk_order_pickup_week FOREIGN KEY (pickup_week_id) REFERENCES pickup_weeks (id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT            NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal   DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_order   FOREIGN KEY (order_id)   REFERENCES orders (id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products (id)
);
