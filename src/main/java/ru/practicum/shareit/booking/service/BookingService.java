package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getBookingsByUser(long userId, String state, Pageable page);

    List<BookingDto> getBookingsByItemsOfUser(long userId, String state, Pageable page);
}
