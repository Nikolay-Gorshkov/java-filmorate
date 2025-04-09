package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DTO.GenreDTO;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(@Qualifier("inMemoryGenreStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<GenreDTO> getAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();
        return genres.stream()
                .map(genre -> new GenreDTO(genre.ordinal() + 1, mapGenreNameRu(genre)))
                .collect(Collectors.toList());
    }

    public GenreDTO getGenreById(int id) {
        Genre genre = genreStorage.getGenreById(id);
        return new GenreDTO(id, mapGenreNameRu(genre));
    }

    private String mapGenreNameRu(Genre genre) {
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
}