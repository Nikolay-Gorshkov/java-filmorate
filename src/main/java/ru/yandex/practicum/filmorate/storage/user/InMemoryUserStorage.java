package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int userIdCounter = 0;
    // Изменили: вместо Map<Integer, Set<Integer>> используем Map с хранением статуса дружбы.
    private final Map<Integer, Map<Integer, FriendshipStatus>> userFriends = new HashMap<>();

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
        userFriends.computeIfAbsent(userId, k -> new HashMap<>()).put(friendId, FriendshipStatus.UNCONFIRMED);
    }

    // Новый метод для подтверждения заявки в друзья.
    public void confirmFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        if (userFriends.containsKey(friendId) &&
                userFriends.get(friendId).getOrDefault(userId, null) == FriendshipStatus.UNCONFIRMED) {
            // При подтверждении статусы обновляем для обоих пользователей.
            userFriends.get(friendId).put(userId, FriendshipStatus.CONFIRMED);
            userFriends.computeIfAbsent(userId, k -> new HashMap<>()).put(friendId, FriendshipStatus.CONFIRMED);
        } else {
            throw new NotFoundException("Нет запроса на дружбу от пользователя " + friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        if(userFriends.containsKey(userId)) {
            userFriends.get(userId).remove(friendId);
        }
        if(userFriends.containsKey(friendId)) {
            userFriends.get(friendId).remove(userId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        validateUser(userId);
        Map<Integer, FriendshipStatus> friendsMap = userFriends.getOrDefault(userId, Collections.emptyMap());
        // Возвращаем только подтверждённых друзей.
        return friendsMap.entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> users.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        validateUser(userId);
        validateUser(otherId);
        Map<Integer, FriendshipStatus> friends1 = userFriends.getOrDefault(userId, Collections.emptyMap());
        Map<Integer, FriendshipStatus> friends2 = userFriends.getOrDefault(otherId, Collections.emptyMap());
        Set<Integer> commonIds = new HashSet<>();
        for (Map.Entry<Integer, FriendshipStatus> entry : friends1.entrySet()) {
            if (entry.getValue() == FriendshipStatus.CONFIRMED &&
                    friends2.getOrDefault(entry.getKey(), null) == FriendshipStatus.CONFIRMED) {
                commonIds.add(entry.getKey());
            }
        }
        return commonIds.stream()
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
