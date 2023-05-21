package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder
public class Item {
    public static long idCounter = 1;

    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long owner;
    private ItemRequest request;
}
