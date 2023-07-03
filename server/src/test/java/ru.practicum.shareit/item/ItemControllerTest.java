package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.variables.Variables.HEADER;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testCreateItem() {
        long itemId = 1;
        long userId = 1;
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        ItemDto itemDtoToCreate = ItemDto.builder().name("name").description("description")
                .available(true).build();
        when(itemService.createItem(userId, itemDtoToCreate)).thenReturn(itemToCreate);

        String response = mockMvc.perform(post("/items").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        verify(itemService).createItem(userId, itemDtoToCreate);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void testGetItemsByUser() {
        long itemId = 1;
        long userId = 1;
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        when(itemService.getItemsByUser(1)).thenReturn(List.of(itemToCreate));

        String response = mockMvc.perform(get("/items").header(HEADER, userId)
                        .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService).getItemsByUser(userId);
        assertEquals(objectMapper.writeValueAsString(List.of(itemToCreate)), response);
    }

    @Test
    @SneakyThrows
    void testUpdateItem() {
        long itemId = 1;
        long userId = 1;
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        ItemDto itemUpdate = ItemDto.builder().name("name").build();
        when(itemService.updateItem(userId, itemId, itemUpdate)).thenReturn(itemToCreate);

        String response = mockMvc.perform(patch("/items/" + itemId).header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService).updateItem(userId, itemId, itemUpdate);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void testGetItem() {
        long itemId = 1;
        long userId = 1;
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        when(itemService.getItemById(1, 1)).thenReturn(itemToCreate);

        String response = mockMvc.perform(get("/items/" + itemId).header(HEADER, 1)
                        .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService).getItemById(itemId, userId);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void testSearchItems() {
        long itemId = 1;
        long userId = 1;
        String text = "Дрель";
        ItemDto itemToCreate = ItemDto.builder().id(itemId).name("Дрель аккумуляторная").description("description")
                .available(true).owner(userId).build();
        when(itemService.searchItems(text)).thenReturn(List.of(itemToCreate));

        String response = mockMvc.perform(get("/items/search?text=" + text)
                        .header(HEADER, userId)
                        .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        verify(itemService).searchItems("Дрель");
        assertEquals(objectMapper.writeValueAsString(List.of(itemToCreate)), response);
    }

    @Test
    @SneakyThrows
    void testCreateComment() {
        long itemId = 1;
        long userId = 1;
        CommentDto commentDtoToCreate = CommentDto.builder().id(1).authorId(userId).authorName("User 1").itemId(itemId)
                        .text("Comment 1").created(LocalDateTime.now()).build();

        when(itemService.createComment(userId, itemId, commentDtoToCreate)).thenReturn(commentDtoToCreate);

        String response = mockMvc.perform(post("/items/" + itemId + "/comment").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDtoToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService).createComment(userId, itemId, commentDtoToCreate);
        assertEquals(objectMapper.writeValueAsString(commentDtoToCreate), response);
    }
}