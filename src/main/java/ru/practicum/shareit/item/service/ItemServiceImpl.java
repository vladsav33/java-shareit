package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoSuchItem;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.exceptions.WrongUser;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;
    private final UserService userService;

    public Item createItem(long userId, ItemDto itemDto) {
        if (!userService.checkUserExists(userId)) {
            throw new NoSuchUser("No such user");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        item.setId(Item.idCounter++);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(long userId, long itemId, Map<String, Object> changes) {
        if (items.get(itemId) == null) {
            throw new NoSuchItem("No such item");
        }
        if (items.get(itemId).getOwner() != userId) {
            throw new WrongUser("Wrong user for the update");
        }

        Item item = items.get(itemId);
        changes.forEach(
                (change, value) -> {
                    switch(change) {
                        case "name":
                            item.setName((String) value);
                            break;
                        case "description":
                            item.setDescription((String) value);
                            break;
                        case "available":
                            item.setAvailable((Boolean) value);
                            break;
                    }
                }
        );
        items.put(itemId, item);
        return item;
    }

    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    public List<Item> getItemsByUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getDescription() != null && item.getDescription()
                .toUpperCase()
                .contains(text.toUpperCase()))
                .collect(Collectors.toList());
    }
}
