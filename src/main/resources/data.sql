-- Вставка тестовых пользователей
INSERT INTO users (email, login, name, birthday) VALUES
('user1@example.com', 'user1', 'User One', '1990-01-01'),
('user2@example.com', 'user2', 'User Two', '1992-02-02'),
('user3@example.com', 'user3', 'User Three', '1995-03-03');

-- Вставка тестовых фильмов
INSERT INTO films (name, description, release_date, duration, mpaa_rating) VALUES
('Film One', 'Description for Film One', '2000-01-01', 120, 'PG-13'),
('Film Two', 'Description for Film Two', '2010-05-15', 90, 'R'),
('Film Three', 'Description for Film Three', '2020-07-20', 150, 'PG');

-- Вставка тестовых жанров
INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Анимация'),
('Триллер'),
('Документальный'),
('Боевик');

-- Связь фильмов и жанров:
-- Фильм 1: Комедия (id=1) и Драма (id=2)
INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1), (1, 2);
-- Фильм 2: Триллер (id=4)
INSERT INTO film_genres (film_id, genre_id) VALUES
(2, 4);
-- Фильм 3: Анимация (id=3)
INSERT INTO film_genres (film_id, genre_id) VALUES
(3, 3);

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
