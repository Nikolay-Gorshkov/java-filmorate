-- Таблица пользователей
CREATE TABLE users (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    CONSTRAINT email_unique UNIQUE (email)
);

-- Таблица фильмов
CREATE TABLE films (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpaa_rating VARCHAR(10) NOT NULL,
    CONSTRAINT chk_release_date CHECK (release_date >= '1895-12-28'),
    CONSTRAINT chk_mpaa_rating CHECK (mpaa_rating IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);

-- Таблица жанров
CREATE TABLE genres (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Связующая таблица для жанров фильма
CREATE TABLE film_genres (
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Таблица лайков фильмов (связь многие-ко-многим между фильмами и пользователями)
CREATE TABLE film_likes (
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица дружбы пользователей с указанием статуса
CREATE TABLE friendships (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    status VARCHAR(15) NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('UNCONFIRMED', 'CONFIRMED'))
);

-- Таблица режиссеров
CREATE TABLE IF NOT EXISTS directors (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT director_pk PRIMARY KEY (id)
);

-- Связующая таблица режиссеров
CREATE TABLE IF NOT EXISTS film_directors (
    film_id INTEGER NOT NULL,
    director_id INTEGER NOT NULL,
    CONSTRAINT film_director_pk PRIMARY KEY (film_id, director_id),
    CONSTRAINT film_dir_fk FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT director_fk FOREIGN KEY (director_id) REFERENCES directors(id) ON DELETE CASCADE
);
