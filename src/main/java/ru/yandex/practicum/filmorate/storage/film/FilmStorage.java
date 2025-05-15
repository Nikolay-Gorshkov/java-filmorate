package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    void deleteFilm(int filmId);

    List<Film> getAllFilms();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    List<Film> searchFilms(String query, List<String> byParams);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getFilmsByIds(List<Integer> ids);
}