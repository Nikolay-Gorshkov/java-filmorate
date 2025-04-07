package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

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

    // Новые свойства:
    // Поле для жанров (фильм может относиться сразу к нескольким жанрам)
    private Set<Genre> genres;

    // Рейтинг MPAA
    private MpaaRating mpaaRating;

    public Film() {
    }

    public Film(final int id, final String name, final String description, final LocalDate releaseDate, final int duration,
                Set<Genre> genres, MpaaRating mpaaRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpaaRating = mpaaRating;
    }
}
