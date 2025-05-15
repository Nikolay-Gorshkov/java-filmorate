package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private static final String NOT_FOUND_MSG = "Режиссер с id: %d не найден";

    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director)
                .orElseThrow(() -> notFoundException(director.getId()));
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> notFoundException(id));
    }

    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public void deleteDirector(Integer id) {
        if (!directorStorage.deleteDirector(id)) {
            throw notFoundException(id);
        }
    }

    private NotFoundException notFoundException(Integer id) {
        return new NotFoundException(String.format(NOT_FOUND_MSG, id));
    }
}
