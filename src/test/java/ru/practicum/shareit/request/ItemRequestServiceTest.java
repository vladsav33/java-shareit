package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NoSuchRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {
    private ItemRequestServiceImpl requestService;
    private ItemRequestDto requestDto;
    private long userId;
    private long requestId;
    private final ItemRequestRepository requestRepository = Mockito.mock(ItemRequestRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private Pageable page;

    @BeforeEach
    void initTest() {
        userId = 1;
        requestId = 1;
        requestService = new ItemRequestServiceImpl(requestRepository, userRepository, mapper,
                itemRepository, itemMapper);
        requestDto = ItemRequestDto.builder().id(requestId).created(LocalDateTime.now()).requestor(userId)
                .description("Request 1").items(null).build();
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        page = PageRequest.of(0, 20);
    }

    @Test
    void createRequest() {
        when(requestRepository.save(mapper.toRequest(requestDto))).thenReturn(mapper.toRequest(requestDto));
        ItemRequestDto requestToCreate = requestService.createRequest(userId, requestDto);
        assertEquals(requestDto, requestToCreate);
    }

    @Test
    void getOwnRequests() {
        when(requestRepository.findAllByRequestorIs(userId)).thenReturn(List.of(mapper.toRequest(requestDto)));
        requestDto.setItems(new ArrayList<>());
        List<ItemRequestDto> requests = requestService.getOwnRequests(userId);

        assertEquals(requests, List.of(requestDto));
    }

    @Test
    void getAllRequests() {
        when(requestRepository.findAllByRequestorIsNot(userId, page)).thenReturn(List.of(mapper.toRequest(requestDto)));
        requestDto.setItems(new ArrayList<>());
        List<ItemRequestDto> requests = requestService.getAllRequests(userId, page);

        assertEquals(requests, List.of(requestDto));
    }

    @Test
    void getRequestById() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.ofNullable(mapper.toRequest(requestDto)));
        requestDto.setItems(new ArrayList<>());
        ItemRequestDto requests = requestService.getRequestById(requestId, userId);

        assertEquals(requests, requestDto);
    }

    @Test
    void getRequestByIdNotFound() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.ofNullable(null));
        assertThatThrownBy(() -> {
            ItemRequestDto requests = requestService.getRequestById(requestId, userId);
        }).isInstanceOf(NoSuchRequest.class).hasMessage("This request was not found");
    }
}