package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final DirectorService directorService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.directorService = directorService;
    }

    public boolean filmExists(int filmId) {
        try {
            getFilmById(filmId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getMostPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == 0) {
            LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
            if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
                throw new ValidationException("Дата релиза не может быть раньше " + earliestReleaseDate);
            }
            if (film.getMpaaRating() == null) {
                throw new ValidationException("MPAA рейтинг обязателен");
            }
            boolean mpaValid = Arrays.stream(MpaaRating.values())
                    .anyMatch(r -> r.equals(film.getMpaaRating()));
            if (!mpaValid) {
                throw new NotFoundException("Некорректный MPAA рейтинг: " + film.getMpaaRating());
            }
            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    boolean genreValid = Arrays.stream(Genre.values())
                            .anyMatch(g -> g.equals(genre));
                    if (!genreValid) {
                        throw new NotFoundException("Некорректный жанр: " + genre);
                    }
                }
            }
            return filmStorage.addFilm(film);
        }

        Film existingFilm = filmStorage.getFilmById(film.getId());
        if (existingFilm == null) {
            return filmStorage.addFilm(film);
        }

        LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше " + earliestReleaseDate);
        }
        if (film.getMpaaRating() == null) {
            throw new ValidationException("MPAA рейтинг обязателен");
        }
        boolean mpaValid = Arrays.stream(MpaaRating.values())
                .anyMatch(r -> r.equals(film.getMpaaRating()));
        if (!mpaValid) {
            throw new NotFoundException("Некорректный MPAA рейтинг: " + film.getMpaaRating());
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                boolean genreValid = Arrays.stream(Genre.values())
                        .anyMatch(g -> g.equals(genre));
                if (!genreValid) {
                    throw new NotFoundException("Некорректный жанр: " + genre);
                }
            }
        }
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> searchFilms(String query, List<String> byParams) {
        validateSearchParams(byParams);
        return filmStorage.searchFilms(query, byParams);
    }

    private void validateSearchParams(List<String> byParams) {
        if (byParams == null || byParams.isEmpty()) {
            throw new ValidationException("Параметр 'by' обязателен");
        }
        for (String param : byParams) {
            if (!param.equals("title") && !param.equals("director")) {
                throw new ValidationException("Недопустимое значение параметра 'by': " + param);
            }
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        if (userId == friendId) {
            throw new IllegalArgumentException("Пользователь и друг не могут быть одним и тем же человеком");
        }
        userService.getUserById(userId);
        userService.getUserById(friendId);
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        return commonFilms != null ? commonFilms : Collections.emptyList();
    }
}