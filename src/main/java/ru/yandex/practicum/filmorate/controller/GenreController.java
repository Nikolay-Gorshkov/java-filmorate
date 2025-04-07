package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class GenreController {

    @GetMapping("/genres")
    public List<GenreResponse> getAllGenres() {
        return Arrays.stream(Genre.values())
                .map(genre -> new GenreResponse(genre.ordinal() + 1, getGenreName(genre)))
                .toList();
    }

    @GetMapping("/genres/{id}")
    public GenreResponse getGenreById(@PathVariable int id) {
        Optional<Genre> genreOpt = Arrays.stream(Genre.values())
                .filter(g -> g.ordinal() + 1 == id)
                .findFirst();
        if (genreOpt.isEmpty()) {
            throw new NotFoundException("Жанр с id " + id + " не найден");
        }
        Genre genre = genreOpt.get();
        return new GenreResponse(id, getGenreName(genre));
    }

    private String getGenreName(Genre genre) {
        switch (genre) {
            case COMEDY:
                return "Комедия";
            case DRAMA:
                return "Драма";
            case ANIMATION:
                return "Мультфильм";
            case THRILLER:
                return "Триллер";
            case DOCUMENTARY:
                return "Документальный";
            case ACTION:
                return "Боевик";
            default:
                return genre.name();
        }
    }

    public static class GenreResponse {
        private int id;
        private String name;

        public GenreResponse(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}