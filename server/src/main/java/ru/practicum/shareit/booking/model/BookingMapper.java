package ru.practicum.shareit.booking.model;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBooking(BookingDto bookingDto);

    BookingDto toBookingDto(Booking booking);
}
