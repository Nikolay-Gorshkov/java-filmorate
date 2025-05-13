package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    List<Film> getAllFilms();

    List<Film> getMostPopularFilms(int count);

    List<Film> getMostPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);
}