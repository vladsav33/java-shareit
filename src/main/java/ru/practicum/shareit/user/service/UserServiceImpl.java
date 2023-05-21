package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmail;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private UserMapper userMapper;
    public long idCounter = 1;


    public User createUser(UserDto userDto) {
        if (checkEmailExists(userDto.getEmail())) {
            log.error("Email {} already exists", userDto.getEmail());
            throw new DuplicateEmail("Email already exists");
        }
        User user = userMapper.toUser(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("User {} was created", user);
        return user;
    }

    public User updateUser(long userId, Map<String, Object> changes) {
        User user = users.get(userId);
        changes.forEach(
                (change, value) -> {
                    switch (change) {
                        case "name":
                            user.setName((String) value);
                            break;
                        case "email":
                            String email = (String) value;
                            if (!user.getEmail().equals(email) && checkEmailExists(email)) {
                                log.error("Email {} already exists", user.getEmail());
                                throw new DuplicateEmail("Email already exists");
                            }
                            user.setEmail((String) value);
                            break;
                    }
                }
        );
        users.put(userId, user);
        log.info("User {} was updated", user);
        return user;
    }

    public void deleteUser(long userId) {
        users.remove(userId);
        log.info("User {} was deleted", userId);
    }

    public User getUser(long userId) {
        log.info("User {} was retrieved", userId);
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        log.info("All users were retrieved");
        return new ArrayList<>(users.values());
    }

    private boolean checkEmailExists(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).count() != 0;
    }

    public boolean checkUserExists(long userId) {
        return (users.get(userId) != null);
    }
}
