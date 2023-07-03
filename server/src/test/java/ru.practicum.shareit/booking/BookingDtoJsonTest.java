package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void testBookingDto() {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2022, 7, 3, 19, 55, 0),
                LocalDateTime.of(2022, 7, 4, 19, 55, 0),
                1L,
                new ItemDto(1L, "Item 1", "Description 1", true, 1L, 1L,
                        null, null, List.of(new CommentDto(1L, "Comment 1", 1L, 1L,
                        LocalDateTime.of(2022, 7, 5, 19, 55, 0), "Author 1"))),
                new UserDto(1L, "Name 1", "name@mail.com"),
                Status.APPROVED);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-07-03T19:55:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-07-04T19:55:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Description 1");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.lastBooking", BookingDto.class).isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.item.nextBooking", BookingDto.class).isEqualTo(null);

        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].text").isEqualTo("Comment 1");
        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].authorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].created").isEqualTo("2022-07-05T19:55:00");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].authorName").isEqualTo("Author 1");

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Name 1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("name@mail.com");

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
