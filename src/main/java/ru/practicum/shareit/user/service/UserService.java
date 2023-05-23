package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(long userId, Map<String, Object> changes);

    void deleteUser(long userId);

    UserDto getUser(long userId);

    List<UserDto> getAllUsers();

    boolean checkUserExists(long userId);
}
