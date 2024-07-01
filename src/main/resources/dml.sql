-- Вставка даних в таблицю Medicines
INSERT INTO Medicines (name, manufacturer, form, purpose, image)
VALUES
    ('Парацетамол', 'Байєр', 'Таблетки', 'Зниження температури', NULL),
    ('Аспірин', 'Байєр', 'Таблетки', 'Зниження температури, знеболювальний ефект', NULL),
    ('Цитрамон', 'Україна', 'Таблетки', 'Зниження температури, знеболювальний ефект, зняття головного болю', NULL);

-- Вставка даних в таблицю Categories
INSERT INTO Categories (category_name)
VALUES
    ('Анальгетики'),
    ('Антибіотики'),
    ('Вітаміни'),
    ('Протизапальні засоби'),
    ('Антигістамінні засоби');

-- Вставка даних в таблицю MedicineCategories
INSERT INTO MedicineCategories (medicine_id, category_id)
VALUES
    (1, 1),  -- Парацетамол - Анальгетики
    (2, 1),  -- Аспірин - Анальгетики
    (3, 1);  -- Цитрамон - Анальгетики

-- Вставка даних в таблицю Users
INSERT INTO Users (username, password, role)
VALUES
    ('admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'ADMIN'),
    ('User1', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER'),
    ('User2', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER');

-- Вставка даних в таблицю Bookmarks
INSERT INTO Bookmarks (user_id, medicine_id)
VALUES
    (2, 1),  -- Користувач user1 - Парацетамол
    (2, 2),  -- Користувач user1 - Аспірин
    (3, 3);  -- Користувач user2 - Цитрамон
