package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.DTO.FilmRequest;
import ru.yandex.practicum.filmorate.DTO.FilmResponse;
import ru.yandex.practicum.filmorate.DTO.MpaDTO;
import ru.yandex.practicum.filmorate.DTO.GenreDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {

    public static Film toFilm(FilmRequest dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        if (dto.getMpa() == null) {
            throw new IllegalArgumentException("Поле mpa не может быть пустым.");
        }
        int mpaId = dto.getMpa().getId();
        String mpaName = dto.getMpa().getName();
        film.setMpaaRating(parseMpaaRating(mpaId, mpaName));

        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            List<Genre> genreList = dto.getGenres().stream()
                    .map(g -> parseGenre(g.getId(), g.getName()))
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenres(genreList);
        }
        return film;
    }

    public static FilmResponse toFilmResponse(Film film) {
        FilmResponse dto = new FilmResponse();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate().toString());
        dto.setDuration(film.getDuration());

        // Используем вынесенные классы MpaDTO и GenreDTO
        MpaDTO mpaDTO = new MpaDTO();
        mpaDTO.setId(film.getMpaaRating().ordinal() + 1);
        mpaDTO.setName(film.getMpaaRating().name().replace("_", "-"));
        dto.setMpa(mpaDTO);

        List<GenreDTO> genreList = film.getGenres().stream().map(genre -> {
            GenreDTO genreDTO = new GenreDTO();
            genreDTO.setId(genre.ordinal() + 1);
            genreDTO.setName(getGenreNameRu(genre));
            return genreDTO;
        }).collect(Collectors.toList());
        dto.setGenres(genreList);
        return dto;
    }

    private static MpaaRating parseMpaaRating(int id, String name) {
        if (name != null && !name.isBlank()) {
            try {
                return MpaaRating.valueOf(name.replace("-", "_").toUpperCase());
            } catch (IllegalArgumentException ignored) {
                throw new NotFoundException("Некорректный MPAA рейтинг: " + name);
            }
        }
        if (id >= 1 && id <= 5) {
            switch (id) {
                case 1: return MpaaRating.G;
                case 2: return MpaaRating.PG;
                case 3: return MpaaRating.PG_13;
                case 4: return MpaaRating.R;
                case 5: return MpaaRating.NC_17;
            }
        }
        throw new NotFoundException("Некорректный MPAA рейтинг: " + id);
    }

    private static Genre parseGenre(int id, String name) {
        if (id >= 1 && id <= 6) {
            switch (id) {
                case 1: return Genre.COMEDY;
                case 2: return Genre.DRAMA;
                case 3: return Genre.ANIMATION;
                case 4: return Genre.THRILLER;
                case 5: return Genre.DOCUMENTARY;
                case 6: return Genre.ACTION;
            }
        }
        throw new NotFoundException("Некорректный жанр: " + id);
    }

    private static String getGenreNameRu(Genre genre) {
        switch (genre) {
            case COMEDY: return "Комедия";
            case DRAMA: return "Драма";
            case ANIMATION: return "Мультфильм";
            case THRILLER: return "Триллер";
            case DOCUMENTARY: return "Документальный";
            case ACTION: return "Боевик";
            default: return genre.name();
        }
    }
}