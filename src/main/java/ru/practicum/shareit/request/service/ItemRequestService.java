package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwnRequests(long userId);

    List<ItemRequestDto> getAllRequests(long userId, Pageable page);

    ItemRequestDto getRequestById(long requestId, long userId);
}
