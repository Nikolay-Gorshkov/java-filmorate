package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DTO.MpaDTO;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("inMemoryMpaStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaDTO> getAllMpa() {
        List<MpaaRating> ratings = mpaStorage.getAllMpa();
        return ratings.stream()
                .map(rating -> new MpaDTO(rating.ordinal() + 1, rating.name().replace("_", "-")))
                .collect(Collectors.toList());
    }

    public MpaDTO getMpaById(int id) {
        MpaaRating rating = mpaStorage.getMpaById(id);
        return new MpaDTO(id, rating.name().replace("_", "-"));
    }
}