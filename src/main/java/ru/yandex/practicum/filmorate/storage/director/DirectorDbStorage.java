package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_SQL = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM directors WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM directors";
    private static final String DELETE_SQL = "DELETE FROM directors WHERE id = ?";

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        int affectedRows = jdbcTemplate.update(
                UPDATE_SQL,
                director.getName(),
                director.getId()
        );
        if (affectedRows == 0) {
            return Optional.empty();
        }
        return getDirectorById(director.getId());
    }

    @Override
    public Optional<Director> getDirectorById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_ID_SQL,
                            new DirectorRowMapper(),
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query(SELECT_ALL_SQL, new DirectorRowMapper());
    }

    @Override
    public boolean deleteDirector(Integer id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }
}
