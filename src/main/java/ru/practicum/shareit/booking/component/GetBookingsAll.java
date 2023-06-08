package ru.practicum.shareit.booking.component;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;

import java.util.List;
import java.util.stream.Collectors;

public class GetBookingsAll extends Chain {
    public GetBookingsAll(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    public List<BookingDto> findBookings(long userId) {
        return bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                .map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }
    public State getState() {
        return State.ALL;
    }
}
