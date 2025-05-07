package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Film {
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    private List<Genre> genres = new ArrayList<>();

    private MpaaRating mpaaRating;

    private List<Director> directors = new ArrayList<>();

    public Film() {
    }

    public Film(final int id, final String name, final String description, final LocalDate releaseDate, final int duration,
                List<Genre> genres, MpaaRating mpaaRating, List<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.mpaaRating = mpaaRating;
        this.directors = directors != null ? directors : new ArrayList<>();
    }
}