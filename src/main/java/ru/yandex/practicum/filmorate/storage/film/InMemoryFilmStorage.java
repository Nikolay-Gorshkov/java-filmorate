package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();
    private int filmIdCounter = 0;

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(filmIdCounter++);
        films.put(film.getId(), film);
        filmLikes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        films.remove(filmId);
        filmLikes.remove(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmLikes.get(filmId).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        filmLikes.get(filmId).remove(userId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(
                        filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                        filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size()))
                .limit(count)
                .toList();
    }

    @Override
    public List<Film> getMostPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        return films.values().stream()
                .filter(f -> genreId == null || f.getGenres() != null &&
                        f.getGenres().stream().anyMatch(g -> g.ordinal() + 1 == genreId))
                .filter(f -> year == null || f.getReleaseDate().getYear() == year)
                .sorted((f1, f2) -> Integer.compare(
                        filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                        filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size()))
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        List<Film> directorFilms = films.values().stream()
                .filter(f -> f.getDirectors().stream()
                        .anyMatch(d -> d.getId() == directorId))
                .collect(Collectors.toList());

        switch (sortBy.toLowerCase()) {
            case "year":
                directorFilms.sort(Comparator.comparing(Film::getReleaseDate));
                break;
            case "likes":
                directorFilms.sort((f1, f2) -> Integer.compare(
                        filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                        filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size()
                ));
                break;
            default:
                throw new ValidationException("Недопустимый параметр сортировки: " + sortBy);
        }

        return directorFilms;
    }

    @Override
    public List<Film> searchFilms(String query, List<String> byParams) {
        String searchQuery = query.toLowerCase();

        return films.values().stream()
                .filter(film -> {
                    boolean matchesTitle = byParams.contains("title")
                            && film.getName().toLowerCase().contains(searchQuery);

                    boolean matchesDirector = byParams.contains("director")
                            && film.getDirectors().stream()
                            .anyMatch(d -> d.getName().toLowerCase().contains(searchQuery));

                    return matchesTitle || matchesDirector;
                })
                .sorted((f1, f2) -> Integer.compare(
                        filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                        filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return List.of();
    }

    @Override
    public List<Film> getFilmsByIds(List<Integer> ids) {
        return ids.stream()
                .filter(films::containsKey)
                .map(films::get)
                .collect(Collectors.toList());
    }
}