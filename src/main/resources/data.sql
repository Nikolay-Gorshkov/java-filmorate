-- Вставка пользователей, если они еще не существуют
INSERT INTO users (email, login, name, birthday)
SELECT 'user1@example.com', 'user1', 'User One', '1990-01-01'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user1@example.com');

INSERT INTO users (email, login, name, birthday)
SELECT 'user2@example.com', 'user2', 'User Two', '1992-02-02'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user2@example.com');

INSERT INTO users (email, login, name, birthday)
SELECT 'user3@example.com', 'user3', 'User Three', '1995-03-03'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user3@example.com');
