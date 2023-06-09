package ru.practicum.shareit.booking.component;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;

import java.util.List;
import java.util.stream.Collectors;

public class GetBookingsWaiting extends Chain {
    public GetBookingsWaiting(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    public List<BookingDto> findBookings(long userId, Pageable page) {
        return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING, page).stream()
                .map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public State getState() {
        return State.WAITING;
    }
}
