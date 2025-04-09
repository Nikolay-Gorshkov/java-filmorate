package ru.yandex.practicum.filmorate.DTO;

public class MpaDTO {
    private int id;
    private String name;

    public MpaDTO() {
    }

    public MpaDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}