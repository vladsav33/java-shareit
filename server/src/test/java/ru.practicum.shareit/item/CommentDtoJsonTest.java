package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void testCommentDto() {
        CommentDto comment = new CommentDto(1L, "Comment 1", 1L, 1L,
                LocalDateTime.of(2022, 7, 5, 19, 55, 0), "Author 1");

        JsonContent<CommentDto> result = json.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Comment 1");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-07-05T19:55:00");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Author 1");
    }
}
