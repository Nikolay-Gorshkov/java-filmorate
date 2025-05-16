package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String name = (user.getName() == null || user.getName().isBlank())
                ? user.getLogin()
                : user.getName();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, name); // Используем переменную name
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        user.setName(name); // Обновляем поле name
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        String name = (user.getName() == null || user.getName().isBlank())
                ? user.getLogin()
                : user.getName();

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                name,
                Date.valueOf(user.getBirthday()),
                user.getId());

        user.setName(name); // Обновляем поле name
        return user;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(0);
    }

    @Override
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId);
        if (rowsAffected == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        String deleteFriendships = "DELETE FROM friendships WHERE user_id = ? OR friend_id = ?";
        jdbcTemplate.update(deleteFriendships, userId, userId);
    }

    @Override
    public void clearAll() {
        String sqlDeleteFriendships = "DELETE FROM friendships";
        String sqlDeleteUsers = "DELETE FROM users";

        jdbcTemplate.update(sqlDeleteFriendships);
        jdbcTemplate.update(sqlDeleteUsers);
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);

        String selectSql = "SELECT status FROM friendships WHERE user_id = ? AND friend_id = ?";
        List<String> statuses = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString("status"), friendId, userId);

        if (!statuses.isEmpty() && "UNCONFIRMED".equals(statuses.get(0))) {
            String updateSql = "UPDATE friendships SET status = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(updateSql, "CONFIRMED", friendId, userId);
            String insertSql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, userId, friendId, "CONFIRMED");
        } else {
            String insertSql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, userId, friendId, "UNCONFIRMED");
        }

        String insertEventSql = "INSERT INTO user_event (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertEventSql, userId, "FRIEND", "ADD", friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        String insertEventSql = "INSERT INTO user_event (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertEventSql, userId, "FRIEND", "REMOVE", friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        getUserById(userId);
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        getUserById(userId);
        getUserById(otherId);
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
                "JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherId);
    }

    @Override
    public List<Event> getFeed(int userId) {
        String sql = "SELECT * FROM user_event WHERE user_id = ? ORDER BY timestamp DESC, event_id DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Event event = new Event();
            event.setEventId(rs.getInt("event_id"));
            event.setUserId(rs.getInt("user_id"));
            event.setEventType(rs.getString("event_type"));
            event.setOperation(rs.getString("operation"));
            event.setEntityId(rs.getInt("entity_id"));
            event.setTimestamp(rs.getTimestamp("timestamp").getTime());
            return event;
        }, userId);
    }
}

