package ru.practicum.shareit.user.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmail;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Getter
@Setter
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper mapper;
    private long idCounter = 1;

    @Autowired
    public UserServiceImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    public UserDto createUser(UserDto userDto) {
        if (checkEmailExists(userDto.getEmail())) {
            log.error("Email {} already exists", userDto.getEmail());
            throw new DuplicateEmail("Email already exists");
        }
        User user = mapper.toUser(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("User {} was created", user);
        return mapper.toUserDto(user);
    }

    public UserDto updateUser(long userId, UserDto userUpdate) {
        User user = users.get(userId);

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            if (checkEmailExists(userUpdate.getEmail()) && !user.getEmail().equals(userUpdate.getEmail())) {
                log.error("Email {} already exists", userUpdate.getEmail());
                throw new DuplicateEmail("Email already exists");
            }
            user.setEmail(userUpdate.getEmail());
        }
        users.put(userId, user);
        log.info("User {} was updated", user);
        return mapper.toUserDto(user);
    }

    public void deleteUser(long userId) {
        users.remove(userId);
        log.info("User {} was deleted", userId);
    }

    public UserDto getUser(long userId) {
        log.info("User {} was retrieved", userId);
        return mapper.toUserDto(users.get(userId));
    }

    public List<UserDto> getAllUsers() {
        log.info("All users were retrieved");
        return users.values().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean checkEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public boolean checkUserExists(long userId) {
        return (users.get(userId) != null);
    }
}
