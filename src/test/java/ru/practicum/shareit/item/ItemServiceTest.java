package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemServiceTest {

    private ItemServiceImpl itemService;
    private UserServiceImpl userService;
    private ItemMapper itemMapper;
    private Item item;
    private ItemDto itemDto;
    private long itemId;
    private long userId;
    private UserDto userDto;

    @BeforeEach
    public void initTest() {
        itemId = 1;
        userId = 1;

        userService = new UserServiceImpl();
        itemService = new ItemServiceImpl(itemMapper, userService);
        userService.idCounter = 1;
        itemService.idCounter = 1;


        item = Item.builder().id(itemId).name("name").description("description").available(true).owner(userId).build();
        itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        userDto = UserDto.builder().name("name").email("name@mail.com").build();
        userService.createUser(userDto);
    }

    @Test
    void createItem() {
        Item itemToCreate = itemService.createItem(userId, itemDto);
        assertEquals(item, itemToCreate);
    }

    @Test
    void updateItem() {
        Map<String, Object> itemToUpdate = Map.of("name", "name updated");

        Item itemToCreate = itemService.createItem(userId, itemDto);
        itemToCreate.setName("name updated");
        Item itemActual = itemService.updateItem(userId, itemId, itemToUpdate);
        assertEquals(itemToCreate, itemActual);
    }

    @Test
    void getItemById() {
        Item itemToCreate = itemService.createItem(userId, itemDto);

        Item itemActual = itemService.getItemById(itemId);
        assertEquals(itemToCreate, itemActual);
    }

    @Test
    void getItemsByUser() {
        Item itemToCreate = itemService.createItem(userId, itemDto);

        List<Item> itemActual = itemService.getItemsByUser(userId);
        assertEquals(List.of(itemToCreate), itemActual);
    }

    @Test
    void searchItems() {
        Item itemToCreate = itemService.createItem(userId, itemDto);

        List<Item> itemActual = itemService.searchItems("description");
        assertEquals(List.of(itemToCreate), itemActual);
    }
}