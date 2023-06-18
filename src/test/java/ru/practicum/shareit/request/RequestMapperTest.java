package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestMapperTest {
    @Test
    void testMapper() {
        ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
        ItemRequestDto requestDto = null;
        ItemRequest request = null;
        assertNull(mapper.toRequest(requestDto));
        assertNull(mapper.toRequestDto(request));
    }
}
