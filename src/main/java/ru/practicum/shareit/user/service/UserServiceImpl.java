package ru.practicum.shareit.user.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.user.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserMapper mapper, UserRepository userRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        User user = mapper.toUser(userDto);
        userRepository.save(user);
        log.info("User {} was created", user);
        return mapper.toUserDto(user);
    }

    public UserDto updateUser(long userId, UserDto userUpdate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchUser("User was not found"));

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
        }
        userRepository.save(user);
        log.info("User {} was updated", user);
        return mapper.toUserDto(user);
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User {} was deleted", userId);
    }

    public UserDto getUser(long userId) {
        log.info("User {} was retrieved", userId);
        return mapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NoSuchUser("User was not found")));
    }

    public List<UserDto> getAllUsers() {
        log.info("All users were retrieved");
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    public boolean checkUserExists(long userId) {
        return userRepository.existsById(userId);
    }
}
