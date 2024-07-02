-- Вставка даних в таблицю Medicines
INSERT INTO Medicines (name, description, manufacturer, form, purpose, image)
VALUES
    ('Парацетамол [Nurofen]', 'Знеболює легкий та помірний біль і жар', 'Reckitt Benckiser', 'Таблетки (перорально)', 'Знеболення, зниження температури', 'https://tabletki.ua/uk/category/534/product/nurofen-tabletki-dlya-doroslyh-i-ditey-vid-6-rokiv-20-tabl-200-mg-p-12345'),
    ('Сироп від алергії [Claritin]', 'Полегшує симптоми алергії, такі як чхання, нежить та свербіж в очах', 'Schering-Plough', 'Сироп (перорально)', 'Лікування алергії', 'https://www.claritin.com.ua/product/syrup/'),
    ('Антибіотичні капсули [Amoxicillin]', 'Лікує бактеріальні інфекції', 'GlaxoSmithKline', 'Капсули (перорально)', 'Лікування бактеріальних інфекцій', 'https://www.gsk.com.ua/uk-ua/products/amoxicillin/'),
    ('Інгалятор для астми [Salbutamol]', 'Доставляє ліки для розслаблення дихальних шляхів у хворих на астму', 'GlaxoSmithKline', 'Інгалятор (інгаляційний)', 'Лікування симптомів астми', 'https://www.gsk.com.ua/uk-ua/products/salbutamol/'),
    ('Жувальні таблетки від печії [Rennie]', 'Нейтралізує шлункову кислоту для полегшення печії', 'Bayer HealthCare', 'Жувальні таблетки (перорально)', 'Лікування печії та диспепсії', 'https://rennie.com.ua/uk/'),
    ('Антидепресанти [Zoloft]', 'Покращують настрій і зменшують симптоми депресії', 'Pfizer', '[Форма, наприклад, таблетки (перорально), капсули (перорально)]', 'Лікування депресії', 'https://www.pfizer.com.ua/uk/products/zoloft'),
    ('Снодійні таблетки [Melatonin]', 'Сприяють розслабленню та сну', 'Nature’s Bounty', 'Таблетки (перорально)', 'Лікування тимчасової безсоння', 'https://www.naturesbounty.com/uk-ua/product/melatonin-10mg-time-release-tablets/');

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
    ('Admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'ADMIN'),
    ('User1', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER'),
    ('User2', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER');

-- Вставка даних в таблицю Bookmarks
INSERT INTO Bookmarks (user_id, medicine_id)
VALUES
    (2, 1),  -- Користувач user1 - Парацетамол
    (2, 2),  -- Користувач user1 - Аспірин
    (3, 3);  -- Користувач user2 - Цитрамон
