package ru.yandex.practicum.filmorate.storage.Mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public MpaaRating getMpaById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<MpaaRating> ratings = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToMpa(rs), id);
        if (ratings.isEmpty()) {
            throw new NotFoundException("Рейтинг с id " + id + " не найден");
        }
        return ratings.get(0);
    }

    private MpaaRating mapRowToMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        for (MpaaRating rating : MpaaRating.values()) {
            if (rating.name().replace("_", "-").equalsIgnoreCase(name) || (rating.ordinal() + 1 == id)) {
                return rating;
            }
        }
        throw new SQLException("Неизвестный MPAA рейтинг с id " + id);
    }
}
