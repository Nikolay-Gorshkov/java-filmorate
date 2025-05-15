package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        String mpaaRatingDb = rs.getString("mpaa_rating");
        String mpaaEnumStr = mpaaRatingDb.replace("-", "_");
        film.setMpaaRating(MpaaRating.valueOf(mpaaEnumStr));
        film.setGenres(new ArrayList<>(getGenresForFilm(film.getId())));
        film.setDirectors(getDirectorsForFilm(rs.getInt("id")));
        return film;
    }

    private List<Genre> getGenresForFilm(int filmId) {
        String sql = "SELECT g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.id";  // сортируем по id жанра

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int genreId = rs.getInt("id");
            String genreName = rs.getString("name");
            return mapGenreName(genreName);
        }, filmId);
    }

    private Genre mapGenreName(String name) {
        switch (name.toLowerCase()) {
            case "комедия":
                return Genre.COMEDY;
            case "драма":
                return Genre.DRAMA;
            case "анимация":
                return Genre.ANIMATION;
            case "триллер":
                return Genre.THRILLER;
            case "документальный":
                return Genre.DOCUMENTARY;
            case "боевик":
                return Genre.ACTION;
            default:
                throw new IllegalArgumentException("Неизвестный жанр: " + name);
        }
    }


    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getMpaaRating() == null) {
            throw new ValidationException("Рейтинг MPA не указан");
        }
        if (film.getGenres() != null && film.getGenres().contains(null)) {
            throw new ValidationException("Передан некорректный жанр");
        }
        String sql = "INSERT INTO films (name, description, release_date, duration, mpaa_rating) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            // Преобразуем enum MPAA (например, PG_13) в строку с дефисом ("PG-13")
            String mpaaToDb = film.getMpaaRating().name().replace("_", "-");
            ps.setString(5, mpaaToDb);
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        if (!film.getDirectors().isEmpty()) {
            saveFilmDirectors(film);
        }
        return film;
    }

    private void saveFilmGenres(Film film) {
        String sqlGenre = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            int genreId = genre.ordinal() + 1;
            jdbcTemplate.update(sqlGenre, film.getId(), genreId);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpaa_rating = ? WHERE id = ?";
        String mpaaToDb = film.getMpaaRating().name().replace("_", "-");

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaaToDb,
                film.getId()
        );

        // Обновление жанров
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }

        // Обновление режиссеров
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            saveFilmDirectors(film);
        }

        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(0);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void addLike(int filmId, int userId) {
        getFilmById(filmId);
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным");
        }
        String sql = "SELECT f.*, COUNT(fl.user_id) AS likes " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private void saveFilmDirectors(Film film) {
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, film.getDirectors(), film.getDirectors().size(),
                (ps, director) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, director.getId());
                });
    }

    private List<Director> getDirectorsForFilm(int filmId) {
        String sql = "SELECT d.id AS director_id, d.name "
                + "FROM film_directors fd "
                + "JOIN directors d ON d.id = fd.director_id "
                + "WHERE fd.film_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Director(
                        rs.getInt("director_id"),
                        rs.getString("name")
                ), filmId);
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        String sql = "SELECT f.*, COUNT(fl.user_id) AS likes "
                + "FROM films f "
                + "LEFT JOIN film_likes fl ON f.id = fl.film_id "
                + "INNER JOIN film_directors fd ON f.id = fd.film_id "
                + "WHERE fd.director_id = ? "
                + "GROUP BY f.id "
                + "ORDER BY " + getOrderClause(sortBy);

        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    private String getOrderClause(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "year" -> "f.release_date";
            case "likes" -> "likes DESC";
            default -> throw new ValidationException("Недопустимый параметр сортировки: " + sortBy);
        };
    }

    @Override
    public List<Film> searchFilms(String query, List<String> byParams) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Поисковый запрос не может быть пустым");
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String searchPattern = "%" + query.toLowerCase() + "%";

        if (byParams.contains("title")) {
            conditions.add("LOWER(f.name) LIKE ?");
            params.add(searchPattern);
        }
        if (byParams.contains("director")) {
            conditions.add("LOWER(d.name) LIKE ?");
            params.add(searchPattern);
        }

        if (conditions.isEmpty()) {
            return Collections.emptyList();
        }

        String whereClause = "WHERE " + String.join(" OR ", conditions);

        String sql = "SELECT f.*, COUNT(fl.user_id) AS likes " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id " +
                whereClause +
                " GROUP BY f.id " +
                "ORDER BY likes DESC";

        return jdbcTemplate.query(sql, this::mapRowToFilm, params.toArray());
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, COUNT(fl.film_id) AS popularity " +
                "FROM films f " +
                "JOIN film_likes fl ON f.id = fl.film_id " +
                "JOIN film_genres fg ON f.id = fg.film_id " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fl.user_id IN (?, ?) " +
                "GROUP BY f.id " +
                "HAVING COUNT(DISTINCT fl.user_id) = 2 " +
                "ORDER BY popularity DESC";

        List<Film> commonFilms = jdbcTemplate.query(sql, new Object[]{userId, friendId}, this::mapRowToFilm);
        commonFilms.forEach(film -> {
            film.setGenres(getGenresForFilm(film.getId()));
        });
        return commonFilms;
    }

    @Override
    public List<Film> getFilmsByIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM films WHERE id IN (" + inSql + ")";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            String mpaaRatingDb = rs.getString("mpaa_rating");
            String mpaaEnumStr = mpaaRatingDb.replace("-", "_");
            film.setMpaaRating(MpaaRating.valueOf(mpaaEnumStr));
            return film;
        }, ids.toArray());

        // Получение жанров
        String sqlGenres = "SELECT fg.film_id, g.name FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (" + inSql + ")";
        List<Map<String, Object>> genreRows = jdbcTemplate.queryForList(sqlGenres, ids.toArray());
        Map<Integer, List<Genre>> genresMap = new HashMap<>();
        for (Map<String, Object> row : genreRows) {
            int filmId = (int) row.get("film_id");
            String genreName = (String) row.get("name");
            Genre genre = mapGenreName(genreName);
            genresMap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        }
        // Установка жанров фильмам
        for (Film film : films) {
            film.setGenres(genresMap.getOrDefault(film.getId(), Collections.emptyList()));
        }
        return films;
    }
}