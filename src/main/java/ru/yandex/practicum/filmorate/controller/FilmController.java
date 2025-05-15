package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.DTO.FilmRequest;
import ru.yandex.practicum.filmorate.DTO.FilmResponse;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PutMapping
    public FilmResponse updateFilm(@Valid @RequestBody FilmRequest filmRequest) {
        Film filmToUpdate = FilmMapper.toFilm(filmRequest);
        Film updatedFilm = filmService.updateFilm(filmToUpdate);
        log.info("Обновлен фильм: {}", updatedFilm);
        return FilmMapper.toFilmResponse(updatedFilm);
    }

    @GetMapping
    public List<FilmResponse> getAllFilms() {
        log.info("Запрошен список всех фильмов");
        return filmService.getAllFilms().stream()
                .map(FilmMapper::toFilmResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmResponse getFilm(@PathVariable int id) {
        Film film = filmService.getFilmById(id);
        return FilmMapper.toFilmResponse(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
        log.info("Фильм с id {} удален", id);
    }

    @GetMapping("/popular")
    public List<FilmResponse> getPopularFilms(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {

        List<Film> films = filmService.getMostPopularFilmsByGenreAndYear(count, genreId, year);
        return films.stream()
                .map(FilmMapper::toFilmResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, id);
    }

    @PostMapping
    public FilmResponse addFilm(@Valid @RequestBody FilmRequest filmRequest) {
        Film film = FilmMapper.toFilm(filmRequest);
        Film createdFilm = filmService.addFilm(film);
        log.info("Создан фильм: {}", createdFilm);
        return FilmMapper.toFilmResponse(createdFilm);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(
            @PathVariable int directorId,
            @RequestParam(defaultValue = "likes") String sortBy) {

        log.info("Получение фильмов режиссера {} с сортировкой по {}", directorId, sortBy);
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<FilmResponse> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "title,director") List<String> by) {

        List<Film> films = filmService.searchFilms(query, by); // Получаем List<Film>

        return films.stream()
                .map(FilmMapper::toFilmResponse) // Преобразуем каждый Film в FilmResponse
                .collect(Collectors.toList());
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam("userId") int userId, @RequestParam("friendId") int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}

