package ru.yandex.practicum.filmorate.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> getRecommendations(int userId) {
        int nSimilarUsers = 5;
        int mRecommendations = 10;

        // Поиск похожих пользователей
        String sqlSimilarUsers = """
        SELECT fl2.user_id, COUNT(*) as common_likes
        FROM film_likes fl1
        JOIN film_likes fl2 ON fl1.film_id = fl2.film_id AND fl1.user_id != fl2.user_id
        WHERE fl1.user_id = ?
        GROUP BY fl2.user_id
        ORDER BY common_likes DESC
        LIMIT ?
    """;
        List<Integer> similarUsers = jdbcTemplate.query(sqlSimilarUsers, (rs, rowNum) -> rs.getInt("user_id"), userId, nSimilarUsers);

        // Получение лайкнутых фильмов пользователя
        String sqlLikedFilms = "SELECT film_id FROM film_likes WHERE user_id = ?";
        List<Integer> likedFilms = jdbcTemplate.query(sqlLikedFilms, (rs, rowNum) -> rs.getInt("film_id"), userId);

        // Получение ID рекомендованных фильмов
        String sqlRecommendedFilmIds = """
        SELECT fl.film_id, COUNT(*) as like_count
        FROM film_likes fl
        WHERE fl.user_id IN (%s)
        AND fl.film_id NOT IN (%s)
        GROUP BY fl.film_id
        ORDER BY like_count DESC
        LIMIT ?
    """;
        String similarUsersIn = similarUsers.stream().map(String::valueOf).collect(Collectors.joining(","));
        String likedFilmsIn = likedFilms.stream().map(String::valueOf).collect(Collectors.joining(","));
        if (similarUsers.isEmpty()) {
            similarUsersIn = "0";
        }
        if (likedFilms.isEmpty()) {
            likedFilmsIn = "0";
        }
        String sql = String.format(sqlRecommendedFilmIds, similarUsersIn, likedFilmsIn);
        List<Integer> recommendedFilmIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("film_id"), mRecommendations);

        // Получение фильмов с жанрами
        List<Film> films = filmStorage.getFilmsByIds(recommendedFilmIds);

        // Сортировка фильмов по порядку recommendedFilmIds
        @NotNull Map<Integer, @NotNull Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, f -> f));
        List<Film> orderedRecommendedFilms = recommendedFilmIds.stream()
                .map(filmMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return orderedRecommendedFilms;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}