package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        final String sql = "INSERT INTO reviews " +
                "(content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        String insertEventSql = "INSERT INTO user_event (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertEventSql, review.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return review;
    }


    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        String insertEventSql = "INSERT INTO user_event (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertEventSql, review.getUserId(), "REVIEW", "UPDATE", review.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        if (review == null) {
            throw new NotFoundException("Review with id " + reviewId + " not found");
        }
        int userId = review.getUserId();

        String sql = "DELETE FROM reviews WHERE review_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, reviewId);
        if (rowsAffected == 0) {
            throw new NotFoundException("Review with id " + reviewId + " not found");
        }

        String insertEventSql = "INSERT INTO user_event (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertEventSql, userId, "REVIEW", "REMOVE", reviewId);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{reviewId}, this::mapRowToReview);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Review> getReviews(Integer filmId, int count) {
        String sql;
        Object[] params;
        if (filmId == null) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            params = new Object[]{count};
        } else {
            sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            params = new Object[]{filmId, count};
        }
        return jdbcTemplate.query(sql, params, this::mapRowToReview);
    }

    @Transactional
    @Override
    public void addLike(Long reviewId, int userId) {
        String sqlGet = "SELECT interaction_type FROM review_interactions WHERE review_id = ? AND user_id = ?";
        String sqlInsert = "INSERT INTO review_interactions (review_id, user_id, interaction_type) VALUES (?, ?, 'LIKE')";
        String sqlUpdate = "UPDATE review_interactions SET interaction_type = 'LIKE' WHERE review_id = ? AND user_id = ?";
        String sqlUpdateUseful = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";

        try {
            String currentType = jdbcTemplate.queryForObject(sqlGet, String.class, reviewId, userId);
            if ("LIKE".equals(currentType)) {
                // Do nothing
            } else if ("DISLIKE".equals(currentType)) {
                jdbcTemplate.update(sqlUpdate, reviewId, userId);
                jdbcTemplate.update(sqlUpdateUseful, 2, reviewId);
            }
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update(sqlInsert, reviewId, userId);
            jdbcTemplate.update(sqlUpdateUseful, 1, reviewId);
        }
    }

    @Transactional
    @Override
    public void addDislike(Long reviewId, int userId) {
        String sqlGet = "SELECT interaction_type FROM review_interactions WHERE review_id = ? AND user_id = ?";
        String sqlInsert = "INSERT INTO review_interactions (review_id, user_id, interaction_type) VALUES (?, ?, 'DISLIKE')";
        String sqlUpdate = "UPDATE review_interactions SET interaction_type = 'DISLIKE' WHERE review_id = ? AND user_id = ?";
        String sqlUpdateUseful = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";

        try {
            String currentType = jdbcTemplate.queryForObject(sqlGet, String.class, reviewId, userId);
            if ("DISLIKE".equals(currentType)) {
                // Do nothing
            } else if ("LIKE".equals(currentType)) {
                jdbcTemplate.update(sqlUpdate, reviewId, userId);
                jdbcTemplate.update(sqlUpdateUseful, -2, reviewId);
            }
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update(sqlInsert, reviewId, userId);
            jdbcTemplate.update(sqlUpdateUseful, -1, reviewId);
        }
    }

    @Transactional
    @Override
    public void removeLike(Long reviewId, int userId) {
        String sqlCheck = "SELECT interaction_type FROM review_interactions WHERE review_id = ? AND user_id = ?";
        try {
            String type = jdbcTemplate.queryForObject(sqlCheck, String.class, reviewId, userId);
            if ("LIKE".equals(type)) {
                String sqlDelete = "DELETE FROM review_interactions WHERE review_id = ? AND user_id = ?";
                jdbcTemplate.update(sqlDelete, reviewId, userId);
                String sqlUpdateUseful = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
                jdbcTemplate.update(sqlUpdateUseful, reviewId);
            }
        } catch (EmptyResultDataAccessException e) {
            // No interaction, do nothing
        }
    }

    @Transactional
    @Override
    public void removeDislike(Long reviewId, int userId) {
        String sqlCheck = "SELECT interaction_type FROM review_interactions WHERE review_id = ? AND user_id = ?";
        try {
            String type = jdbcTemplate.queryForObject(sqlCheck, String.class, reviewId, userId);
            if ("DISLIKE".equals(type)) {
                String sqlDelete = "DELETE FROM review_interactions WHERE review_id = ? AND user_id = ?";
                jdbcTemplate.update(sqlDelete, reviewId, userId);
                String sqlUpdateUseful = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
                jdbcTemplate.update(sqlUpdateUseful, reviewId);
            }
        } catch (EmptyResultDataAccessException e) {
            // No interaction, do nothing
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getLong("review_id"));
        r.setContent(rs.getString("content"));
        r.setIsPositive(rs.getBoolean("is_positive"));
        r.setUserId(rs.getInt("user_id"));
        r.setFilmId(rs.getInt("film_id"));
        r.setUseful(rs.getInt("useful"));
        return r;
    }
}