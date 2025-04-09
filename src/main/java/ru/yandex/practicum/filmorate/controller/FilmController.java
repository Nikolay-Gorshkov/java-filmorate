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

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public FilmResponse addFilm(@Valid @RequestBody FilmRequest filmRequest) {
        Film film = FilmMapper.toFilm(filmRequest);
        Film createdFilm = filmService.addFilm(film);
        log.info("Создан фильм: {}", createdFilm);
        return FilmMapper.toFilmResponse(createdFilm);
    }
}

