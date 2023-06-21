package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {
    private BookingDto bookingDto;
    private Booking booking;
    private BookingMapper mapper;

    @BeforeEach
    void initTest() {
        mapper = Mappers.getMapper(BookingMapper.class);
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2022, 7, 3, 19, 55, 0),
                LocalDateTime.of(2022, 7, 4, 19, 55, 0),
                1L,
                new ItemDto(1L, "Item 1", "Description 1", true, 1L, 1L,
                        null, null, List.of(new CommentDto(1L, "Comment 1", 1L, 1L,
                        LocalDateTime.of(2022, 7, 5, 19, 55, 0), "Author 1"))),
                new UserDto(1L, "Name 1", "name@mail.com"),
                Status.APPROVED);
        booking = new Booking(
                1L,
                LocalDateTime.of(2022, 7, 3, 19, 55, 0),
                LocalDateTime.of(2022, 7, 4, 19, 55, 0),
                new Item(1L, "Item 1", "Description 1", true, 1L, 1L),
                new User(1L, "Name 1", "name@mail.com"),
                Status.APPROVED);
    }

    @Test
    void testMapperNull() {
        bookingDto = null;
        booking = null;
        assertNull(mapper.toBooking(bookingDto));
        assertNull(mapper.toBookingDto(booking));
    }

    @Test
    void testMapperToBooking() {
        Booking booking = mapper.toBooking(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
    }

    @Test
    void testMapperToBookingDto() {
        BookingDto bookingDto = mapper.toBookingDto(booking);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
    }
}
