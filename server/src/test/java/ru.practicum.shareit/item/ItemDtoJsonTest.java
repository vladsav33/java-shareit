package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testItemDto() {
        ItemDto itemDto = new ItemDto(1L, "Item 1", "Description 1", true, 1L, 1L,
                        null, null, List.of(new CommentDto(1L, "Comment 1", 1L, 1L,
                        LocalDateTime.of(2022, 7, 5, 19, 55, 0), "Author 1")));

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description 1");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking", BookingDto.class).isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.nextBooking", BookingDto.class).isEqualTo(null);

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Comment 1");
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].authorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2022-07-05T19:55:00");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Author 1");
    }
}

