package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserClient client;
    @InjectMocks
    private UserController controller;
    private long userId;
    private long itemId;
    private long bookingId;
    private UserDto userDto;
    private ResponseEntity<Object> entity;

    @BeforeEach
    void initTest() {
        userId = 1;
        long itemId = 1;
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        userDto = UserDto.builder().id(1L).name("name").email("user@user.com").build();
        entity = new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Test
    @SneakyThrows
    void getUsersAll() {
        when(client.getUsersAll()).thenReturn(entity);

        String response = mockMvc.perform(get("/users").contentType("application/json"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).getUsersAll();
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @Test
    @SneakyThrows
    void getUser() {
        when(client.getUser(userId)).thenReturn(entity);

        String response = mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andReturn().getResponse().getContentAsString();
        verify(client).getUser(userId);
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mockMvc.perform(delete("/users/" + userId).contentType("application/json"))
                .andExpect(status().isOk());
        verify(client, times(1)).deleteUser(userId);
    }

    @Test
    @SneakyThrows
    void createUser() {
        entity = new ResponseEntity<>(userDto, HttpStatus.CREATED);
        when(client.createUser(userDto)).thenReturn(entity);

        String response = mockMvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        verify(client).createUser(userDto);
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @Test
    @SneakyThrows
    void updateUser() {
        when(client.updateUser(userId, userDto)).thenReturn(entity);

        String response = mockMvc.perform(patch("/users/" + userId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(client).updateUser(userId, userDto);
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }
}