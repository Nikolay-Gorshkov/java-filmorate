package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.List;

@Component("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {

    @Override
    public List<Genre> getAllGenres() {
        return Arrays.asList(Genre.values());
    }

    @Override
    public Genre getGenreById(int id) {
        Genre[] genres = Genre.values();
        if (id < 1 || id > genres.length) {
            throw new NotFoundException("Жанр с id " + id + " не найден");
        }
        return genres[id - 1];
    }
}
