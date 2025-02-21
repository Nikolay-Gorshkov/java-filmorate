package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления фильмами в приложении Filmorate.
 */
@RestController
@RequestMapping("/films")
@Slf4j
public final class FilmController {
    /**
     * Хранилище фильмов, где ключ — идентификатор фильма, значение — объект фильма.
     */
    private final Map<Integer, Film> films = new HashMap<>();

    /**
     * Счетчик идентификаторов для новых фильмов.
     */
    private int idCounter = 0;

    /**
     * Добавляет новый фильм в систему.
     *
     * @param film объект фильма для добавления
     * @return добавленный фильм
     * @throws ValidationException если данные фильма некорректны (например, отрицательная продолжительность
     *                            или дата релиза раньше 28 декабря 1895 года)
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody final Film film) {
        validateFilm(film);

        // Проверка на положительную продолжительность
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        film.setId(++idCounter);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    /**
     * Обновляет существующий фильм в системе.
     *
     * @param film объект фильма с обновленными данными
     * @return обновленный фильм
     * @throws ValidationException если фильм с указанным ID не найден или данные некорректны
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody final Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new ValidationException("Фильм с таким id не найден");
        }
        validateFilm(film);

        // Проверка на положительную продолжительность
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    /**
     * Возвращает список всех фильмов, хранящихся в системе.
     *
     * @return список объектов Film
     */
    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошен список всех фильмов");
        return new ArrayList<>(films.values());
    }

    /**
     * Проверяет корректность данных фильма.
     *
     * @param film объект фильма для валидации
     * @throws ValidationException если дата релиза раньше 28 декабря 1895 года
     */
    private void validateFilm(final Film film) {
        // Проверка на дату релиза не раньше 28 декабря 1895 года
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза раньше 28 декабря 1895 года: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}