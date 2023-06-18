package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {
    @Test
    void testMapper() {
        BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
        BookingDto bookingDto = null;
        Booking booking = null;
        assertNull(mapper.toBooking(bookingDto));
        assertNull(mapper.toBookingDto(booking));
    }
}
