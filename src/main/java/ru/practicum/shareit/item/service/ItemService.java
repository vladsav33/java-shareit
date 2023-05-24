package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemUpdate);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItems(String text);
}
