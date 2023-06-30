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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.variables.Variables.HEADER;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Mock
    private RequestClient client;
    @InjectMocks
    private RequestController controller;
    private long userId;
    private long requestId;
    private ItemRequestDto request;
    private ResponseEntity<Object> entity;

    @BeforeEach
    void initTest() {
        userId = 1;
        requestId = 1;
        long itemId = 1;
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("name").description("description")
                .available(true).requestId(requestId).build();
        request = ItemRequestDto.builder().id(requestId).description("Request 1").requestor(userId)
                .created(LocalDateTime.now()).items(List.of(itemToCreate)).build();
        entity = new ResponseEntity<>(request, HttpStatus.OK);
    }

    @Test
    @SneakyThrows
    void createRequest() {
        when(client.createRequest(any(Long.class), any(ItemRequestDto.class))).thenReturn(entity);

        String response = mockMvc.perform(post("/requests")
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(request), response);
    }

    @Test
    @SneakyThrows
    void getOwnRequests() {
        when(client.getOwnRequests(userId)).thenReturn(entity);

        String response = mockMvc.perform(get("/requests")
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(request), response);
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        when(client.getAllRequests(userId, 0, 20)).thenReturn(entity);

        String response = mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(request), response);
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        when(client.getRequestById(requestId, userId)).thenReturn(entity);

        String response = mockMvc.perform(get("/requests/" + requestId)
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(request), response);
    }
}