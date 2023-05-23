package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, Map<String, Object> changes);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItems(String text);
}
