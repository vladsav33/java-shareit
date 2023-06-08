package ru.practicum.shareit.booking.component;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GetBookingsPast extends Chain {
    public GetBookingsPast(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    public List<BookingDto> findBookings(long userId) {
        return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                .map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public State getState() {
        return State.PAST;
    }
}
