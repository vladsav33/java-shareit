package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemMapper {
    public static ItemDto toItemDTO(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.isAvailable(),
                null,
                null
        );
    }
}
