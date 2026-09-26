-- =============================================================
-- Seed data. Idempotent: rows are only inserted when missing.
-- =============================================================

-- Members and volunteers (volunteer account used by the admin page)
INSERT INTO users (id, full_name, email, phone, role)
SELECT 1, 'Alice Volunteer', 'alice@foodcoop.org', '0400 000 001', 'VOLUNTEER_ADMIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@foodcoop.org');

INSERT INTO users (id, full_name, email, phone, role)
SELECT 2, 'Bob Member', 'bob@example.com', '0400 000 002', 'MEMBER' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@example.com');

INSERT INTO users (id, full_name, email, phone, role)
SELECT 3, 'Carol Member', 'carol@example.com', '0400 000 003', 'MEMBER' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carol@example.com');

-- Product catalog
INSERT INTO products (id, name, description, category, unit, price, stock_quantity, active)
SELECT 1, 'Seasonal Vegetables Box', 'A mixed box of fresh local vegetables.', 'VEGETABLES', 'box', 18.00, 30, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 1);

INSERT INTO products (id, name, description, category, unit, price, stock_quantity, active)
SELECT 2, 'Free-range Eggs', 'Dozen free-range eggs from a nearby farm.', 'DAIRY', 'dozen', 6.50, 40, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 2);

INSERT INTO products (id, name, description, category, unit, price, stock_quantity, active)
SELECT 3, 'Sourdough Loaf', 'Wholegrain sourdough baked by a local bakery.', 'BAKERY', 'loaf', 7.00, 25, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 3);

INSERT INTO products (id, name, description, category, unit, price, stock_quantity, active)
SELECT 4, 'Raw Honey Jar', 'Unprocessed honey from community hives, 500g.', 'PANTRY', 'jar', 12.00, 20, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 4);

INSERT INTO products (id, name, description, category, unit, price, stock_quantity, active)
SELECT 5, 'Organic Apples', 'Crisp seasonal apples, unsprayed.', 'FRUIT', 'kg', 4.50, 60, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = 5);

-- Next two pickup weeks (open ordering week and the following one)
INSERT INTO pickup_weeks (id, week_start, week_end, order_deadline, status, notes)
SELECT 1, DATE(DATE_ADD(CURDATE(), INTERVAL 7 - WEEKDAY(CURDATE()) DAY)),
       DATE(DATE_ADD(CURDATE(), INTERVAL 9 - WEEKDAY(CURDATE()) DAY)),
       DATE(DATE_ADD(CURDATE(), INTERVAL 5 - WEEKDAY(CURDATE()) DAY)),
       'OPEN', 'Pickup Saturday 9am-12pm at the community hall.' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM pickup_weeks WHERE id = 1);

INSERT INTO pickup_weeks (id, week_start, week_end, order_deadline, status, notes)
SELECT 2, DATE(DATE_ADD(CURDATE(), INTERVAL 14 - WEEKDAY(CURDATE()) DAY)),
       DATE(DATE_ADD(CURDATE(), INTERVAL 16 - WEEKDAY(CURDATE()) DAY)),
       DATE(DATE_ADD(CURDATE(), INTERVAL 12 - WEEKDAY(CURDATE()) DAY)),
       'OPEN', 'Pickup Saturday 9am-12pm at the community hall.' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM pickup_weeks WHERE id = 2);
