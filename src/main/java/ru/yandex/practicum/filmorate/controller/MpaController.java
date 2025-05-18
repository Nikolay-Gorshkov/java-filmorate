package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.DTO.MpaDTO;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<MpaDTO> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public MpaDTO getMpaById(@PathVariable int id) {
        return mpaService.getMpaById(id);
    }
}