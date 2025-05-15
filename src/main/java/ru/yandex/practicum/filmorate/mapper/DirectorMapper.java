package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.DTO.DirectorDto;
import ru.yandex.practicum.filmorate.DTO.DirectorResponse;
import ru.yandex.practicum.filmorate.model.Director;

public class DirectorMapper {
    public static Director toDirector(DirectorDto dto) {
        Director director = new Director();
        director.setId(dto.getId());
        director.setName(dto.getName());
        return director;
    }

    public static DirectorResponse toDirectorResponse(Director director) {
        DirectorResponse response = new DirectorResponse();
        response.setId(director.getId());
        response.setName(director.getName());
        return response;
    }
}
