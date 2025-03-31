package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int userIdCounter = 0;
    private final Map<Integer, Set<Integer>> userFriends = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++userIdCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUser(user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        validateUser(id);
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        userFriends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        userFriends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        userFriends.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
        userFriends.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        validateUser(userId);
        Set<Integer> friendIds = userFriends.getOrDefault(userId, Collections.emptySet());
        return friendIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        validateUser(userId);
        validateUser(otherId);
        Set<Integer> friends1 = userFriends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> friends2 = userFriends.getOrDefault(otherId, Collections.emptySet());
        Set<Integer> common = new HashSet<>(friends1);
        common.retainAll(friends2);
        return common.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void validateUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
