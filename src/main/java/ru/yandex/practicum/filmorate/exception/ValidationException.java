package ru.yandex.practicum.filmorate.exception;

/**
 * Пользовательское исключение для валидации данных в приложении Filmorate.
 */
public final class ValidationException extends RuntimeException {
    /**
     * Создает исключение с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public ValidationException(final String message) {
        super(message);
    }
}