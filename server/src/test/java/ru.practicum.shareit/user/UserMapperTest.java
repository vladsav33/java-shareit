package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {
    private UserMapper mapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void initTest() {
        long userId = 1;
        mapper = Mappers.getMapper(UserMapper.class);
        userDto = UserDto.builder().name("name").email("name@mail.com").build();
        user = User.builder().id(userId).name("name").email("name@mail.com").build();
    }

    @Test
    void testMapper() {
        userDto = null;
        user = null;
        assertNull(mapper.toUser(userDto));
        assertNull(mapper.toUserDto(user));
    }

    @Test
    void testMapperToUser() {
        user = mapper.toUser(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testMapperToUserDto() {
        userDto = mapper.toUserDto(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
