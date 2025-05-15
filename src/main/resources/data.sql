INSERT INTO users (email, login, name, birthday)
SELECT 'user1@example.com', 'user1', 'User One', '1990-01-01'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user1@example.com');

-- Вставка лайков фильмов:
-- Пользователь 1 лайкнул Фильм 1 и Фильм 2
INSERT INTO film_likes (film_id, user_id) VALUES
(1, 1),
(2, 1),
-- Пользователь 2 лайкнул Фильм 1
(1, 2),
-- Пользователь 3 лайкнул Фильм 3
(3, 3);

-- Вставка дружбы:
-- Пользователь 1 отправил запрос в друзья пользователю 2 (статус UNCONFIRMED)
INSERT INTO friendships (user_id, friend_id, status) VALUES
(1, 2, 'UNCONFIRMED'),
-- Пользователь 2 и Пользователь 3 имеют подтверждённую дружбу (статус CONFIRMED)
(2, 3, 'CONFIRMED');
