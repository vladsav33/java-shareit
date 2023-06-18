package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {
    @Test
    void testMapper() {
        UserDto userDto = null;
        User user = null;
        assertNull(UserMapper.INSTANCE.toUser(userDto));
        assertNull(UserMapper.INSTANCE.toUserDto(user));
    }
}
