package ru.yandex.practicum.filmorate.DTO;

import lombok.Data;

@Data
public class GenreDTO {
    private int id;
    private String name;

    public GenreDTO() {
    }

    public GenreDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
