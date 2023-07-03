package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testBookingDto() {
        ItemRequestDto requestDto = new ItemRequestDto(
                1L,
                "Description 1",
                1L,
                LocalDateTime.of(2022, 7, 3, 19, 55, 0),
                List.of(new ItemDto(1L, "Item 1", "Description 1", true, 1L, 1L,
                        null, null, List.of(new CommentDto(1L, "Comment 1", 1L, 1L,
                        LocalDateTime.of(2022, 7, 5, 19, 55, 0), "Author 1")))));

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description 1");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-07-03T19:55:00");

        assertThat(result).extractingJsonPathNumberValue("$.items[0].comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].comments[0].text").isEqualTo("Comment 1");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].comments[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].comments[0].authorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].comments[0].created").isEqualTo("2022-07-05T19:55:00");
        assertThat(result).extractingJsonPathStringValue("$.items[0].comments[0].authorName").isEqualTo("Author 1");

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Description 1");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.items[0].lastBooking", BookingDto.class).isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.items[0].nextBooking", BookingDto.class).isEqualTo(null);
    }
}
