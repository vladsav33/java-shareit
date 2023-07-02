package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {
    private CommentMapper mapper;

    @BeforeEach
    void initTest() {
        mapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    void testMapperNull() {
        CommentDto commentDto = null;
        Comment comment = null;
        assertNull(mapper.toComment(commentDto));
        assertNull(mapper.toCommentDto(comment));
    }

    @Test
    void testMapperValues() {
        Comment comment = Comment.builder().id(1)
                .created(LocalDateTime.of(2022, 11, 1, 12, 10, 1))
                .itemId(1).text("Text").author(null).build();
        CommentDto commentDto = CommentDto.builder().id(1)
                .created(LocalDateTime.of(2022, 11, 1, 12, 10, 1))
                .itemId(1).text("Text").authorId(0).build();
        assertEquals(comment.getId(), mapper.toComment(commentDto).getId());
        assertEquals(commentDto, mapper.toCommentDto(comment));
    }
}
