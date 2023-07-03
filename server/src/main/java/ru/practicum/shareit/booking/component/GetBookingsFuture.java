package ru.practicum.shareit.booking.component;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GetBookingsFuture extends Chain {
    public GetBookingsFuture(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    public List<BookingDto> findBookings(long userId, Pageable page) {
        return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page).stream()
                .map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public State getState() {
        return State.FUTURE;
    }
}
