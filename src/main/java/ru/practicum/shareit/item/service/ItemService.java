package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Item createItem(long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, Map<String, Object> changes);

    Item getItemById(long itemId);

    List<Item> getItemsByUser(long userId);

    List<Item> searchItems(String text);
}
