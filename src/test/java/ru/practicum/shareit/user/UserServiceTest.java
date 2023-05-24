package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {
    private UserServiceImpl userService;
    private UserDto userDto;
    private UserDto user;
    private final long userId = 1L;

    @BeforeEach
    void initTest() {
        userService = new UserServiceImpl(UserMapper.INSTANCE);
        userService.setIdCounter(1);
        userDto = UserDto.builder().name("name").email("name@mail.com").build();
        user = UserDto.builder().id(userId).name("name").email("name@mail.com").build();
    }

    @Test
    void testCreateValidUser() {
        UserDto userToCreate = userService.createUser(userDto);
        assertEquals(userToCreate, user);
    }

    @Test
    void testUpdateUser() {
        UserDto userUpdate = UserDto.builder().name("name updated").build();

        user = userService.createUser(userDto);
        user.setName("name updated");
        UserDto userActual = userService.updateUser(userId, userUpdate);
        assertEquals(user, userActual);
    }

    @Test
    void testDeleteUser() {
        user = userService.createUser(userDto);

        List<UserDto> usersActual = userService.getAllUsers();
        assertEquals(1, usersActual.size());
        userService.deleteUser(1L);
        usersActual = userService.getAllUsers();
        assertEquals(0, usersActual.size());
    }

    @Test
    void getUser() {
        user = userService.createUser(userDto);

        UserDto userActual = userService.getUser(userId);
        assertEquals(user, userActual);
    }

    @Test
    void getAllUsers() {
        user = userService.createUser(userDto);

        List<UserDto> usersActual = userService.getAllUsers();
        assertEquals(1, usersActual.size());
        assertEquals(List.of(user), usersActual);
    }

    @Test
    void checkUserExists() {
        user = userService.createUser(userDto);
        assertTrue(userService.checkUserExists(userId));
    }
}