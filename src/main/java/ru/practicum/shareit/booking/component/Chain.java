package ru.practicum.shareit.booking.component;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class Chain {
    public Chain next;
    public final BookingRepository bookingRepository;
    public final BookingMapper bookingMapper;

    public abstract State getState();

    public abstract List<BookingDto> findBookings(long userId);

    public List<BookingDto> processRequest(long userId, State state) {
        if (state == getState()) {
            return findBookings(userId);
        }
        if (next == null) {
            return new ArrayList<>();
        }
        return next.processRequest(userId, state);
    }

    public static Chain link(Chain item1, Chain... item2) {
        Chain head = item1;
        for (Chain item : item2) {
            head.next = item;
            head = item;
        }
        return item1;
    }
}
