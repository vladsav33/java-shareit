package ru.practicum.shareit.item.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private final ItemMapper mapper = ItemMapper.INSTANCE;
    private final UserService userService;
    private long idCounter = 1;

    public ItemDto createItem(long userId, ItemDto itemDto) {
        if (!userService.checkUserExists(userId)) {
            log.error("User {} was not found", userId);
            throw new NoSuchUser("No such user");
        }
        Item item = mapper.toItem(itemDto);
        item.setOwner(userId);
        item.setId(idCounter++);
        items.put(item.getId(), item);
        log.info("Item {} was created", item);
        return mapper.toItemDto(item);
    }

    public ItemDto updateItem(long userId, long itemId, Map<String, Object> changes) {
        if (items.get(itemId) == null) {
            log.error("Item {} was not found", itemId);
            throw new NoSuchItem("No such item");
        }
        if (items.get(itemId).getOwner() != userId) {
            log.error("Wrong user {} to update item {}", userId, itemId);
            throw new WrongUser("Wrong user for the update");
        }

        Item item = items.get(itemId);
        changes.forEach(
                (change, value) -> {
                    switch (change) {
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
        log.info("Item {} was updated", item);
        return mapper.toItemDto(item);
    }

    public ItemDto getItemById(long itemId) {
        log.info("Item {} was retrieved", itemId);
        return mapper.toItemDto(items.get(itemId));
    }

    public List<ItemDto> getItemsByUser(long userId) {
        log.info("Items were retrieved for the user {}", userId);
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            log.info("Empty search string");
            return new ArrayList<>();
        }
        log.info("Items were retrieved for the search text {}", text);
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getDescription() != null && item.getDescription()
                .toUpperCase()
                .contains(text.toUpperCase()))
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }
}
