package ru.practicum.shareit.request.model;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest toRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toRequestDto(ItemRequest itemRequest);
}
