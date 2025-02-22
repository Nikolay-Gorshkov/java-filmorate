package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления пользователями в приложении Filmorate.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public final class UserController {

    /**
     * Хранилище пользователей, где ключ — идентификатор пользователя, значение — объект пользователя.
     */
    private final Map<Integer, User> users = new HashMap<>();

    /**
     * Счетчик идентификаторов для новых пользователей.
     */
    private int idCounter = 0;

    /**
     * Создает нового пользователя в системе.
     *
     * @param user объект пользователя для создания
     * @return созданный пользователь
     * @throws ValidationException если данные пользователя некорректны
     */
    @PostMapping
    public User createUser(@Valid @RequestBody final User user) {
        setDefaultName(user);
        user.setId(++idCounter);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    /**
     * Обновляет существующего пользователя в системе.
     *
     * @param user объект пользователя с обновленными данными
     * @return обновленный пользователь
     * @throws ValidationException если пользователь с указанным ID не найден или данные некорректны
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new ValidationException("Пользователь с таким id не найден");
        }
        setDefaultName(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    /**
     * Возвращает список всех пользователей, хранящихся в системе.
     *
     * @return список объектов User
     */
    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return new ArrayList<>(users.values());
    }

    /**
     * Если имя пользователя не задано или пустое, устанавливает значение логина в качестве имени.
     *
     * @param user объект пользователя для проверки
     */
    private void setDefaultName(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}