package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTest {
    private ItemMapper mapper;
    private ItemDto itemDto;
    private Item item;

    @BeforeEach
    void initTest() {
        long itemId = 1;
        long userId = 1;
        mapper = Mappers.getMapper(ItemMapper.class);
        item = Item.builder().id(itemId).name("name").description("description").available(true).owner(userId).build();
        itemDto = ItemDto.builder().id(itemId).name("name").description("description").available(true).build();
    }

    @Test
    void testMapperNull() {
        itemDto = null;
        item = null;
        assertNull(mapper.toItem(itemDto));
        assertNull(mapper.toItemDto(item));
    }

    @Test
    void testMapperToItem() {
        item = mapper.toItem(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void testMappertToItemDto() {
        itemDto = mapper.toItemDto(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
    }
}
