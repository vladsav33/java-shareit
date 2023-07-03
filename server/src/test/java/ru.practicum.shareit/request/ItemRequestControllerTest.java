package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.variables.Variables.HEADER;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private long userId;
    private long requestId;
    private Pageable page;
    private ItemRequestDto requestDto;

    @BeforeEach
    void initTest() {
        userId = 1;
        requestId = 1;
        long itemId = 1;
        page = PageRequest.of(0, 20);
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("Дрель аккумуляторная").description("description")
                .available(true).owner(userId).build();
        requestDto = ItemRequestDto.builder().id(requestId).description("Request 1").requestor(userId)
                .items(List.of(itemToCreate)).build();
    }

    @Test
    @SneakyThrows
    void createRequest() {
        when(itemRequestService.createRequest(any(Long.class), any(ItemRequestDto.class))).thenReturn(requestDto);

        String response = mockMvc.perform(post("/requests")
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(requestDto), response);
    }

    @Test
    @SneakyThrows
    void getOwnRequests() {
        when(itemRequestService.getOwnRequests(userId)).thenReturn(List.of(requestDto));

        String response = mockMvc.perform(get("/requests")
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), response);
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        when(itemRequestService.getAllRequests(userId, page)).thenReturn(List.of(requestDto));

        String response = mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), response);
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        when(itemRequestService.getRequestById(requestId, userId)).thenReturn(requestDto);

        String response = mockMvc.perform(get("/requests/" + requestId)
                        .header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(requestDto), response);
    }
}