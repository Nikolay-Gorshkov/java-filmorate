package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;


@RestController
@Slf4j
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private static final String DIRECTOR_ID = "directorId";

    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // Изменено с OK на CREATED для создания ресурса
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Создание режиссера: {}", director.getName());
        return directorService.createDirector(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Обновление режиссера с ID: {}", director.getId());
        return directorService.updateDirector(director);
    }

    @GetMapping("/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirector(@PathVariable(DIRECTOR_ID) Integer id) {  // Именованный параметр
        log.debug("Получение режиссера с ID: {}", id);
        return directorService.getDirectorById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> getAllDirectors() {
        log.debug("Получение всех режиссеров");
        return directorService.getAllDirectors();
    }

    @DeleteMapping("/{directorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // Более подходящий статус для удаления
    public void deleteDirector(@PathVariable(DIRECTOR_ID) Integer id) {
        log.info("Удаление режиссера с ID: {}", id);
        directorService.deleteDirector(id);
    }
}
