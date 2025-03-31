package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        userFriends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        userFriends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        userFriends.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
        userFriends.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        Set<Integer> friendIds = userFriends.getOrDefault(userId, Collections.emptySet());
        List<User> friendsList = new ArrayList<>();
        for (Integer id : friendIds) {
            User friend = users.get(id);
            if (friend != null) {
                friendsList.add(friend);
            }
        }
        return friendsList;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!users.containsKey(otherId)) {
            throw new NotFoundException("Пользователь с id " + otherId + " не найден");
        }
        Set<Integer> friends1 = userFriends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> friends2 = userFriends.getOrDefault(otherId, Collections.emptySet());
        Set<Integer> common = new HashSet<>(friends1);
        common.retainAll(friends2);
        List<User> commonFriends = new ArrayList<>();
        for (Integer id : common) {
            User friend = users.get(id);
            if (friend != null) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }
}