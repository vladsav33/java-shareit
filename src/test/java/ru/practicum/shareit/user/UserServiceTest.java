package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {
    private UserServiceImpl userService;
    private UserDto userDto;
    private User user;
    private final long userId = 1L;

    @BeforeEach
    void initTest() {
        userService = new UserServiceImpl();
        userService.idCounter = 1;
        userDto = UserDto.builder().name("name").email("name@mail.com").build();
        user = User.builder().id(userId).name("name").email("name@mail.com").build();
    }

    @Test
    void testCreateValidUser() {
        User userToCreate = userService.createUser(userDto);
        assertEquals(userToCreate, user);
    }

    @Test
    void testUpdateUser() {
        Map<String, Object> userDtoToUpdate = Map.of("name", "name updated");

        user = userService.createUser(userDto);
        user.setName("name updated");
        User userActual = userService.updateUser(userId, userDtoToUpdate);
        assertEquals(user, userActual);
    }

    @Test
    void testDeleteUser() {
        user = userService.createUser(userDto);

        List<User> usersActual = userService.getAllUsers();
        assertEquals(1, usersActual.size());
        userService.deleteUser(1L);
        usersActual = userService.getAllUsers();
        assertEquals(0, usersActual.size());
    }

    @Test
    void getUser() {
        user = userService.createUser(userDto);

        User userActual = userService.getUser(userId);
        assertEquals(user, userActual);
    }

    @Test
    void getAllUsers() {
        user = userService.createUser(userDto);

        List<User> usersActual = userService.getAllUsers();
        assertEquals(1, usersActual.size());
        assertEquals(List.of(user), usersActual);
    }

    @Test
    void checkUserExists() {
        user = userService.createUser(userDto);
        assertTrue(userService.checkUserExists(userId));
    }
}