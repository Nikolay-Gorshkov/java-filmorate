package ru.yandex.practicum.filmorate.storage.Mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.util.Arrays;
import java.util.List;

@Component("inMemoryMpaStorage")
public class InMemoryMpaStorage implements MpaStorage {

    @Override
    public List<MpaaRating> getAllMpa() {
        return Arrays.asList(MpaaRating.values());
    }

    @Override
    public MpaaRating getMpaById(int id) {
        return Arrays.stream(MpaaRating.values())
                .filter(r -> r.ordinal() + 1 == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}

