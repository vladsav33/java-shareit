package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTest {
    @Test
    void testMapper() {
        ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
        ItemDto itemDto = null;
        Item item = null;
        assertNull(mapper.toItem(itemDto));
        assertNull(mapper.toItemDto(item));
    }
}
