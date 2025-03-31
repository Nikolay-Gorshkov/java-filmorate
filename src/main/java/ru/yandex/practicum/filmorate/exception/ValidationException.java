package ru.yandex.practicum.filmorate.exception;

public final class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}