package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель фильма с базовыми характеристиками.
 */
@Data
public class Film {
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    /**
     * Уникальный идентификатор фильма.
     */
    private int id;

    /**
     * Название фильма, не может быть пустым.
     */
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    /**
     * Описание фильма, не должно превышать {MAX_DESCRIPTION_LENGTH} символов.
     */
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Описание не должно превышать 200 символов")
    private String description;

    /**
     * Дата релиза фильма, не может быть в будущем.
     */
    @NotNull(message = "Дата релиза обязательна")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма в минутах, должна быть положительной.
     */
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    /**
     * Пустой конструктор для создания объекта фильма.
     */
    public Film() {
    }

    /**
     * Конструктор с параметрами для создания объекта фильма.
     *
     * @param id          уникальный идентификатор фильма
     * @param name        название фильма
     * @param description описание фильма
     * @param releaseDate дата релиза фильма
     * @param duration    продолжительность фильма в минутах
     */
    public Film(final int id, final String name, final String description, final LocalDate releaseDate, final int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}