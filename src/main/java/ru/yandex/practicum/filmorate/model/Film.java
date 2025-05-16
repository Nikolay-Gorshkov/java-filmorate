package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.DTO.GenreDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}