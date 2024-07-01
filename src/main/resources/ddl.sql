DROP TABLE IF EXISTS Medicines;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS MedicineCategories;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Bookmarks;

-- Таблиця лікарських засобів
CREATE TABLE Medicines (
    medicine_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,-- Назва лікарського засобу
    manufacturer TEXT NOT NULL,-- Виробник
    form TEXT NOT NULL,-- Форма випуску
    purpose TEXT NOT NULL,-- Призначення
    image BYTEA -- Шлях до зображення
);

-- Таблиця категорій
CREATE TABLE Categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL  -- Назва категорії
);

-- Таблиця для зв'язку багато до багатьох між Medicines і Categories
CREATE TABLE MedicineCategories (
    medicine_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    PRIMARY KEY (medicine_id, category_id),
    FOREIGN KEY (medicine_id) REFERENCES Medicines(medicine_id),-- Зовнішній ключ до Medicines
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)-- Зовнішній ключ до Categories
);

-- Таблиця користувачів
CREATE TABLE Users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,-- Ім'я користувача, унікальне
    password TEXT NOT NULL,-- Пароль
    role TEXT DEFAULT 'USER' NOT NULL CHECK(role IN ('USER', 'ADMIN')) -- Роль користувача: 'user' або 'admin', за замовчуванням 'user'
);

-- Таблиця закладок
CREATE TABLE Bookmarks (
    bookmark_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,-- ID користувача, зовнішній ключ до Users
    medicine_id INTEGER NOT NULL,-- ID лікарського засобу, зовнішній ключ до Medicines
    FOREIGN KEY (user_id) REFERENCES Users(user_id),-- Зовнішній ключ до Users
    FOREIGN KEY (medicine_id) REFERENCES Medicines(medicine_id)-- Зовнішній ключ до Medicines
);
