package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private UserDto userDto;
    private UserDto user;
    private final long userId = 1L;
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserServiceImpl userService = new UserServiceImpl(UserMapper.INSTANCE, userRepository);

    @BeforeEach
    void initTest() {
        userDto = UserDto.builder().name("name").email("name@mail.com").build();
        user = UserDto.builder().id(userId).name("name").email("name@mail.com").build();
    }

    @Test
    void testCreateValidUser() {
        UserDto userToCreate = userService.createUser(user);
        when(userRepository.save(UserMapper.INSTANCE.toUser(userToCreate))).thenReturn(UserMapper.INSTANCE.toUser(userToCreate));
        assertEquals(userToCreate, user);
    }

    @Test
    void testUpdateUser() {
        UserDto userUpdate = UserDto.builder().name("name updated").email("new@email.com").build();
        when(userRepository.save(UserMapper.INSTANCE.toUser(userUpdate))).thenReturn(UserMapper.INSTANCE.toUser(userUpdate));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(UserMapper.INSTANCE.toUser(userUpdate)));

        user = userService.createUser(userDto);
        user.setName("name updated");
        user.setEmail("new@email.com");
        UserDto userActual = userService.updateUser(userId, userUpdate);
        assertEquals(user, userActual);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.save(UserMapper.INSTANCE.toUser(userDto))).thenReturn(UserMapper.INSTANCE.toUser(userDto));
        when(userRepository.findAll()).thenReturn(Stream.of(userDto)
                .map(UserMapper.INSTANCE::toUser)
                .collect(Collectors.toList()));
        doNothing().when(userRepository).deleteById(userId);

        user = userService.createUser(userDto);
        List<UserDto> usersActual = userService.getAllUsers();
        assertEquals(1, usersActual.size());
        userService.deleteUser(1L);
        usersActual.remove(0);
        assertEquals(0, usersActual.size());
    }

    @Test
    void getUser() {
        when(userRepository.save(UserMapper.INSTANCE.toUser(userDto))).thenReturn(UserMapper.INSTANCE.toUser(userDto));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(UserMapper.INSTANCE.toUser(userDto)));

        user = userService.createUser(userDto);
        UserDto userActual = userService.getUser(userId);
        assertEquals(user, userActual);
    }

    @Test
    void getUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(NoSuchUser.class)
                .hasMessage("User was not found");
    }

    @Test
    void getAllUsers() {
        when(userRepository.save(UserMapper.INSTANCE.toUser(userDto))).thenReturn(UserMapper.INSTANCE.toUser(userDto));
        when(userRepository.findAll()).thenReturn(Stream.of(userDto)
                .map(UserMapper.INSTANCE::toUser)
                .collect(Collectors.toList()));

        user = userService.createUser(userDto);
        List<UserDto> usersActual = userService.getAllUsers();
        assertEquals(1, usersActual.size());
        assertEquals(List.of(user), usersActual);
    }
}
