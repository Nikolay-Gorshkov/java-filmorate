package ru.yandex.practicum.filmorate.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDTO {
    private int id;
    private String name;
}
