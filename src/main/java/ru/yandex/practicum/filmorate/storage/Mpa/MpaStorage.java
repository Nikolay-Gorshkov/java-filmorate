package ru.yandex.practicum.filmorate.storage.Mpa;

import ru.yandex.practicum.filmorate.model.MpaaRating;
import java.util.List;

public interface MpaStorage {
    List<MpaaRating> getAllMpa();
    MpaaRating getMpaById(int id);
}

