package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemServiceTest {

    private ItemServiceImpl itemService;
    private ItemDto item;
    private ItemDto itemDto;
    private long itemId;
    private long userId;

    @BeforeEach
    public void initTest() {
        itemId = 1;
        userId = 1;

        UserServiceImpl userService = new UserServiceImpl();
        itemService = new ItemServiceImpl(userService);
        userService.setIdCounter(1);
        itemService.setIdCounter(1);


        item = ItemDto.builder().id(itemId).name("name").description("description").available(true).owner(userId).build();
        itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        UserDto userDto = UserDto.builder().name("name").email("name@mail.com").build();
        userService.createUser(userDto);
    }

    @Test
    void createItem() {
        ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        assertEquals(item, itemToCreate);
    }

    @Test
    void updateItem() {
        Map<String, Object> itemToUpdate = Map.of("name", "name updated");

        ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        itemToCreate.setName("name updated");
        ItemDto itemActual = itemService.updateItem(userId, itemId, itemToUpdate);
        assertEquals(itemToCreate, itemActual);
    }

    @Test
    void getItemById() {
        ItemDto itemToCreate = itemService.createItem(userId, itemDto);

        ItemDto itemActual = itemService.getItemById(itemId);
        assertEquals(itemToCreate, itemActual);
    }

    @Test
    void getItemsByUser() {
        ItemDto itemToCreate = itemService.createItem(userId, itemDto);

        List<ItemDto> itemActual = itemService.getItemsByUser(userId);
        assertEquals(List.of(itemToCreate), itemActual);
    }

    @Test
    void searchItems() {
        ItemDto itemToCreate = itemService.createItem(userId, itemDto);

        List<ItemDto> itemActual = itemService.searchItems("description");
        assertEquals(List.of(itemToCreate), itemActual);
    }
}