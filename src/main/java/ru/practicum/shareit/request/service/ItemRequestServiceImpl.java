package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoSuchRequest;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    public final ItemRequestRepository itemRequestRepository;
    public final UserRepository userRepository;
    public final ItemRequestMapper itemRequestMapper;
    public final ItemRepository itemRepository;
    public final ItemMapper itemMapper;

    public ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUser("User was not found");
        }
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Description is null");
        }
        ItemRequest request = itemRequestMapper.toRequest(itemRequestDto);
        itemRequestRepository.save(request);
        return itemRequestMapper.toRequestDto(request);
    }

    public List<ItemRequestDto> getOwnRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUser(("This user was not found"));
        }

        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestorIs(userId).stream()
                .map(itemRequestMapper::toRequestDto).collect(Collectors.toList());
        requests.forEach(itemRequestDto -> itemRequestDto.setItems(itemRepository.findItemsByRequestIdIs(itemRequestDto.getId())
                        .stream().map(itemMapper::toItemDto).collect(Collectors.toList())));
        return requests;
    }

    public List<ItemRequestDto> getAllRequests(long userId, Pageable page) {

        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestorIsNot(userId, page).stream()
                .map(itemRequestMapper::toRequestDto).collect(Collectors.toList());
        requests.forEach(itemRequestDto -> itemRequestDto.setItems(itemRepository.findItemsByRequestIdIs(itemRequestDto.getId())
                .stream().map(itemMapper::toItemDto).collect(Collectors.toList())));
        return requests;
    }

    public ItemRequestDto getRequestById(long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchUser(("This user was not found"));
        }
        ItemRequestDto request = itemRequestMapper.toRequestDto(itemRequestRepository
                .findById(requestId).orElseThrow(() -> new NoSuchRequest("This request was not found")));
        request.setItems(itemRepository.findItemsByRequestIdIs(request.getId())
                .stream().map(itemMapper::toItemDto).collect(Collectors.toList()));
        return request;
    }
}
