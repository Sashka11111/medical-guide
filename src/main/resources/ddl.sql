DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Goals;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS Steps;
DROP TABLE IF EXISTS Tips;
DROP TABLE IF EXISTS CategoryGoals;

-- Створення таблиці "Users"
CREATE TABLE Users (
    id_user       INTEGER PRIMARY KEY AUTOINCREMENT,
    username      VARCHAR (50)    NOT NULL UNIQUE,
    password      VARCHAR (50)    NOT NULL,
    profile_image BYTEA           NOT NULL
);

-- Створення таблиці "Goals"
CREATE TABLE Goals (
    id_goal     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_user     INTEGER,
    name_goal   VARCHAR (100),
    description VARCHAR (255) NOT NULL,
    start_date  DATE          NOT NULL,
    end_date    DATE          NOT NULL,
    status      VARCHAR (100) CHECK (status IN ('Активна', 'Завершена', 'Відкладена')),
    FOREIGN KEY (id_user) REFERENCES Users (id_user) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Створення таблиці "Category"
CREATE TABLE Category (
    id_category INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR (50)    NOT NULL
);

-- Створення таблиці "Steps"
CREATE TABLE Steps (
    id_step     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_goal     INTEGER,
    goal_name   VARCHAR (100),
    description TEXT,
    status      VARCHAR (100) CHECK (status IN ('Активний', 'Завершений')),
    FOREIGN KEY (id_goal) REFERENCES Goals (id_goal) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Створення таблиці "Tips"
CREATE TABLE Tips (
    id_tip   INTEGER PRIMARY KEY AUTOINCREMENT,
    tip_text TEXT NOT NULL
);

-- Створення таблиці "CategoryGoals"
CREATE TABLE CategoryGoals (
    id_category INTEGER,
    id_goal     INTEGER,
    PRIMARY KEY (id_category, id_goal),
    FOREIGN KEY (id_category) REFERENCES Category (id_category) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_goal) REFERENCES Goals (id_goal) ON DELETE CASCADE ON UPDATE CASCADE
);
