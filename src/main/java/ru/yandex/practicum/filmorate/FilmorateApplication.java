package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Filmorate, запускающий Spring Boot приложение.
 */
@SpringBootApplication
public final class FilmorateApplication {
	/**
	 * Приватный конструктор для предотвращения создания экземпляров утилитарного класса.
	 */
	private FilmorateApplication() {
	}

	/**
	 * Точка входа в приложение.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(final String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}
}
