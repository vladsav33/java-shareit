package ru.practicum.shareit.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.component.GetBookingsCurrent;
import ru.practicum.shareit.booking.component.GetBookingsFuture;
import ru.practicum.shareit.booking.component.GetBookingsOwnerCurrent;
import ru.practicum.shareit.booking.component.GetBookingsOwnerFuture;
import ru.practicum.shareit.booking.component.GetBookingsOwnerPast;
import ru.practicum.shareit.booking.component.GetBookingsOwnerRejected;
import ru.practicum.shareit.booking.component.GetBookingsOwnerWaiting;
import ru.practicum.shareit.booking.component.GetBookingsPast;
import ru.practicum.shareit.booking.component.GetBookingsRejected;
import ru.practicum.shareit.booking.component.GetBookingsWaiting;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ComponentTest {
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
    private Booking booking;
    private Item item;
    private User booker;
    private long bookerId;
    private long itemId;
    private long ownerId;
    private long bookingId;
    private Pageable page;

    @BeforeEach
    void initTest() {
        bookerId = 1;
        itemId = 1;
        ownerId = 1;
        bookingId = 1;
        page = PageRequest.of(0, 20);
        booker = User.builder().id(bookerId).name("booker").email("booker@mail.com").build();
        item = Item.builder().id(itemId).name("name").description("description").owner(ownerId).available(true).build();
        booking = Booking.builder().id(bookingId).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                .booker(booker).item(item).status(Status.WAITING).build();
    }

    @Test
    void testGetBookingsCurrent() {
        GetBookingsCurrent bookingsCurrent = new GetBookingsCurrent(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookings = bookingsCurrent.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsCurrent.getState(), State.CURRENT);
    }

    @Test
    void testGetBookingsFuture() {
        GetBookingsFuture bookingsFuture = new GetBookingsFuture(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(Long.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookings = bookingsFuture.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsFuture.getState(), State.FUTURE);
    }

    @Test
    void testGetBookingsPast() {
        GetBookingsPast bookingsPast = new GetBookingsPast(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookings = bookingsPast.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsPast.getState(), State.PAST);
    }

    @Test
    void testGetBookingWaiting() {
        GetBookingsWaiting bookingsWaiting = new GetBookingsWaiting(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(bookerId, Status.WAITING, page))
                .thenReturn(List.of(booking));
        List<Booking> bookings = bookingsWaiting.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsWaiting.getState(), State.WAITING);
    }


    @Test
    void testGetBookingsRejected() {
        GetBookingsRejected bookingsRejected = new GetBookingsRejected(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(bookerId, Status.REJECTED, page))
                .thenReturn(List.of(booking));
        List<Booking> bookings = bookingsRejected.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsRejected.getState(), State.REJECTED);
    }

    @Test
    void testGetBookingsOwnerCurrent() {
        GetBookingsOwnerCurrent bookingsOwnerCurrent = new GetBookingsOwnerCurrent(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookings = bookingsOwnerCurrent.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsOwnerCurrent.getState(), State.CURRENT);
    }

    @Test
    void testGetBookingsOwnerPast() {
        GetBookingsOwnerPast bookingsOwnerPast = new GetBookingsOwnerPast(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookings = bookingsOwnerPast.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsOwnerPast.getState(), State.PAST);
    }

    @Test
    void testGetBookingsOwnerFuture() {
        GetBookingsOwnerFuture bookingsOwnerFuture = new GetBookingsOwnerFuture(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookings = bookingsOwnerFuture.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsOwnerFuture.getState(), State.FUTURE);
    }

    @Test
    void testGetBookingsOwnerWaiting() {
        GetBookingsOwnerWaiting bookingsOwnerWaiting = new GetBookingsOwnerWaiting(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(bookerId, Status.WAITING, page))
                .thenReturn(List.of(booking));
        List<Booking> bookings = bookingsOwnerWaiting.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsOwnerWaiting.getState(), State.WAITING);
    }

    @Test
    void testGetBookingsOwnerRejected() {
        GetBookingsOwnerRejected bookingsOwnerRejected = new GetBookingsOwnerRejected(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(bookerId, Status.REJECTED, page))
                .thenReturn(List.of(booking));
        List<Booking> bookings = bookingsOwnerRejected.findBookings(booker.getId(), page).stream()
                .map(mapper::toBooking).collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookingsOwnerRejected.getState(), State.REJECTED);
    }


}
