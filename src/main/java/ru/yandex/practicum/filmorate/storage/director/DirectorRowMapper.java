package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {

    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
