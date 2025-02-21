package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель пользователя с базовыми характеристиками.
 */
@Data
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    private int id;

    /**
     * Электронная почта пользователя, должна быть в корректном формате.
     */
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    /**
     * Логин пользователя, не может быть пустым и содержать пробелы.
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    /**
     * Имя пользователя, может быть пустым (в таком случае используется логин).
     */
    private String name;

    /**
     * Дата рождения пользователя, должна быть в прошлом.
     */
    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    /**
     * Пустой конструктор для создания объекта пользователя.
     */
    public User() {
    }

    /**
     * Конструктор с параметрами для создания объекта пользователя.
     *
     * @param id       уникальный идентификатор пользователя
     * @param email    электронная почта пользователя
     * @param login    логин пользователя
     * @param name     имя пользователя
     * @param birthday дата рождения пользователя
     */
    public User(final int id, final String email, final String login, final String name, final LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}