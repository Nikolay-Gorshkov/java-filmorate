package ru.yandex.practicum.filmorate.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmRequest {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @NotNull(message = "Рейтинг MPA обязателен")
    private MpaDTO mpa;

    private List<GenreDTO> genres;

    @Data
    public static class MpaDTO {
        private int id;
        private String name;
    }

    @Data
    public static class GenreDTO {
        private int id;
        private String name;
    }
}
