package ru.practicum.shareit.booking.component;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetBookingsOwnerRejected extends Chain {
    public GetBookingsOwnerRejected(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    public List<BookingDto> processRequest(long userId, State state) {
        if (state == State.REJECTED) {
            return bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(userId, Status.REJECTED).stream()
                    .map(bookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (next == null) {
            return new ArrayList<>();
        }
        return next.processRequest(userId, state);
    }
}
