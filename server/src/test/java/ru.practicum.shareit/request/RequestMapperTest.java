package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestMapperTest {
    private ItemRequestMapper mapper;
    private ItemRequestDto requestDto;
    private ItemRequest request;

    @BeforeEach
    void initTest() {
        mapper = Mappers.getMapper(ItemRequestMapper.class);
        long requestId = 1;
        long userId = 1;
        requestDto = ItemRequestDto.builder().id(requestId).created(LocalDateTime.now()).requestor(userId)
                .description("Request 1").build();
        request = ItemRequest.builder().id(requestId).created(LocalDateTime.now()).requestor(userId)
                .description("Request 1").build();
    }

    @Test
    void testMapperNull() {
        requestDto = null;
        request = null;
        assertNull(mapper.toRequest(requestDto));
        assertNull(mapper.toRequestDto(request));
    }

    @Test
    void testMapperToRequest() {
        request = mapper.toRequest(requestDto);
        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getCreated(), requestDto.getCreated());
        assertEquals(request.getDescription(), requestDto.getDescription());
    }

    @Test
    void testMapperToRequestDto() {
        requestDto = mapper.toRequestDto(request);
        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getCreated(), request.getCreated());
        assertEquals(requestDto.getDescription(), request.getDescription());
    }
}
