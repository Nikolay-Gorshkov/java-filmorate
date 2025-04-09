package ru.yandex.practicum.filmorate.DTO;

import lombok.Data;

import java.util.List;

@Data
public class FilmResponse {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
    private MpaDTO mpa;
    private List<GenreDTO> genres;
}