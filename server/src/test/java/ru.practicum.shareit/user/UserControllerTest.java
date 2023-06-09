package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testGetAllUsers() {
        UserDto userToCreate = UserDto.builder().id(1L).name("name").email("user@user.com").build();
        when(userService.getAllUsers()).thenReturn(List.of(userToCreate));

        String response = mockMvc.perform(get("/users").contentType("application/json"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(userService).getAllUsers();
        assertEquals(objectMapper.writeValueAsString(List.of(userToCreate)), response);
    }

    @Test
    @SneakyThrows
    void testGetUser() {
        long userId = 1;
        UserDto user = UserDto.builder().id(1L).name("name").email("user@user.com").build();
        user.setId(userId);

        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @SneakyThrows
    void testDeleteUser() {
        long userId = 1;

        mockMvc.perform(delete("/users/" + userId).contentType("application/json"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @SneakyThrows
    void testCreateValidUser() {
        UserDto userDtoToCreate = UserDto.builder().id(1L).name("name").email("user@user.com").build();
        UserDto userToCreate = UserDto.builder().id(1L).name("name").email("user@user.com").build();
        when(userService.createUser(userDtoToCreate)).thenReturn(userToCreate);

        String response = mockMvc.perform(post("/users").contentType("application/json")
                .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        verify(userService).createUser(userDtoToCreate);
        assertEquals(objectMapper.writeValueAsString(userToCreate), response);
    }

    @Test
    @SneakyThrows
    void testUpdateUser() {
        long userId = 1;

        UserDto userUpdate = UserDto.builder().name("name").build();
        UserDto userToUpdate = UserDto.builder().id(1L).name("name").email("user@user.com").build();
        when(userService.updateUser(userId, userUpdate)).thenReturn(userToUpdate);

        String response = mockMvc.perform(patch("/users/" + userId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        verify(userService).updateUser(userId, userUpdate);
        assertEquals(objectMapper.writeValueAsString(userToUpdate), response);
    }
}