package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class MpaController {

    @GetMapping("/mpa")
    public List<MpaResponse> getAllMpa() {
        return Arrays.stream(MpaaRating.values())
                .map(mpa -> new MpaResponse(mpa.ordinal() + 1, getMpaName(mpa)))
                .toList();
    }

    @GetMapping("/mpa/{id}")
    public MpaResponse getMpaById(@PathVariable int id) {
        Optional<MpaaRating> mpaOpt = Arrays.stream(MpaaRating.values())
                .filter(m -> m.ordinal() + 1 == id)
                .findFirst();
        if (mpaOpt.isEmpty()) {
            throw new NotFoundException("Рейтинг с id " + id + " не найден");
        }
        MpaaRating mpa = mpaOpt.get();
        return new MpaResponse(id, getMpaName(mpa));
    }

    private String getMpaName(MpaaRating mpa) {
        switch (mpa) {
            case G:
                return "G";
            case PG:
                return "PG";
            case PG_13:
                return "PG-13";
            case R:
                return "R";
            case NC_17:
                return "NC-17";
            default:
                return mpa.name();
        }
    }

    public static class MpaResponse {
        private int id;
        private String name;

        public MpaResponse(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}