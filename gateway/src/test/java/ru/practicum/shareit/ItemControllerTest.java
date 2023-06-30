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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.variables.Variables.HEADER;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Mock
    private ItemClient client;
    @InjectMocks
    private ItemController controller;
    private long userId;
    private long itemId;
    private ItemDto itemToCreate;
    private ResponseEntity<Object> entity;

    @BeforeEach
    void initTest() {
        userId = 1;
        itemId = 1;
        long requestId = 1;
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        itemToCreate = ItemDto.builder().id(itemId).name("name").description("description")
                .available(true).requestId(requestId).build();
        entity = new ResponseEntity<>(itemToCreate, HttpStatus.OK);
    }

    @Test
    @SneakyThrows
    void createItem() {
        entity = new ResponseEntity<>(itemToCreate, HttpStatus.CREATED);
        when(client.createItem(userId, itemToCreate)).thenReturn(entity);

        String response = mockMvc.perform(post("/items").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        verify(client).createItem(userId, itemToCreate);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void createComment() {
        CommentDto commentToCreate = CommentDto.builder().id(1)
                .authorId(userId).authorName("User 1").itemId(itemId)
                .text("Comment 1").created(LocalDateTime.now()).build();
        entity = new ResponseEntity<>(commentToCreate, HttpStatus.CREATED);
        when(client.createComment(userId, itemId, commentToCreate)).thenReturn(entity);

        String response = mockMvc.perform(post("/items/" + itemId + "/comment").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        verify(client).createComment(userId, itemId, commentToCreate);
        assertEquals(objectMapper.writeValueAsString(commentToCreate), response);
    }

    @Test
    @SneakyThrows
    void getItemsByUser() {
        when(client.getItemsByUser(1)).thenReturn(entity);

        String response = mockMvc.perform(get("/items").header(HEADER, userId)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).getItemsByUser(userId);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void updateItem() {
        when(client.updateItem(userId, itemId, itemToCreate)).thenReturn(entity);

        String response = mockMvc.perform(patch("/items/" + itemId).header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).updateItem(userId, itemId, itemToCreate);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void getItem() {
        when(client.getItemById(1, 1)).thenReturn(entity);

        String response = mockMvc.perform(get("/items/" + itemId).header(HEADER, 1)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).getItemById(itemId, userId);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void searchItems() {
        String text = "Дрель";
        when(client.searchItems(text)).thenReturn(entity);

        String response = mockMvc.perform(get("/items/search?text=" + text)
                        .header(HEADER, userId)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        verify(client).searchItems("Дрель");
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }
}