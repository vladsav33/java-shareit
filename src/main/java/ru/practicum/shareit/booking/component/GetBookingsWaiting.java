package ru.practicum.shareit.booking.component;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetBookingsWaiting extends Chain {
    public GetBookingsWaiting(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    public List<BookingDto> processRequest(long userId, State state) {
        if (state == State.WAITING) {
            return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING).stream()
                    .map(bookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (next == null) {
            return new ArrayList<>();
        }
        return next.processRequest(userId, state);
    }
}
