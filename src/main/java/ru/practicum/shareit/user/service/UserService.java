package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    User createUser(UserDto userDto);

    User updateUser(long userId, Map<String, Object> changes);

    void deleteUser(long userId);

    User getUser(long userId);

    List<User> getAllUsers();

    boolean checkUserExists(long userId);
}
