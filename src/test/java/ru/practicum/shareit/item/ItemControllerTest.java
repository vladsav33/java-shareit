package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        Item itemToCreate = Item.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        ItemDto itemDtoToCreate = ItemDto.builder().name("name").description("description")
                .available(true).build();
        when(itemService.createItem(userId, itemDtoToCreate)).thenReturn(itemToCreate);

        String response = mockMvc.perform(post("/items").header("X-Sharer-User-Id", userId)
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
        Item itemToCreate = Item.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        when(itemService.getItemsByUser(1)).thenReturn(List.of(itemToCreate));

        String response = mockMvc.perform(get("/items").header("X-Sharer-User-Id", userId)
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
        Item itemToCreate = Item.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        Map<String, Object> itemDtoToCreate = Map.of("name", "name");
        when(itemService.updateItem(userId, itemId, itemDtoToCreate)).thenReturn(itemToCreate);

        String response = mockMvc.perform(patch("/items/" + itemId).header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService).updateItem(userId, itemId, itemDtoToCreate);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void testGetItem() {
        long itemId = 1;
        long userId = 1;
        Item itemToCreate = Item.builder().id(itemId).name("name").description("description")
                .available(true).owner(userId).build();
        when(itemService.getItemById(1)).thenReturn(itemToCreate);

        String response = mockMvc.perform(get("/items/" + itemId).header("X-Sharer-User-Id", 1)
                        .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService).getItemById(itemId);
        assertEquals(objectMapper.writeValueAsString(itemToCreate), response);
    }

    @Test
    @SneakyThrows
    void testSearchItems() {
        long itemId = 1;
        long userId = 1;
        String text = "Дрель";
        Item itemToCreate = Item.builder().id(itemId).name("Дрель аккумуляторная").description("description")
                .available(true).owner(userId).build();
        when(itemService.searchItems(text)).thenReturn(List.of(itemToCreate));

        String response = mockMvc.perform(get("/items/search?text=" + text)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        verify(itemService).searchItems("Дрель");
        assertEquals(objectMapper.writeValueAsString(List.of(itemToCreate)), response);
    }
}