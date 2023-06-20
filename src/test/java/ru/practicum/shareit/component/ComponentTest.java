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
import ru.practicum.shareit.booking.dto.BookingDto;
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
    private User booker;
    private long bookerId;
    private Pageable page;

    @BeforeEach
    void initTest() {
        bookerId = 1;
        long itemId = 1;
        long ownerId = 1;
        long bookingId = 1;
        page = PageRequest.of(0, 20);
        booker = User.builder().id(bookerId).name("booker").email("booker@mail.com").build();
        Item item = Item.builder().id(itemId).name("name").description("description").owner(ownerId).available(true).build();
        booking = Booking.builder().id(bookingId).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                .booker(booker).item(item).status(Status.WAITING).build();
    }

    @Test
    void testGetBookingsCurrent() {
        GetBookingsCurrent bookingsCurrent = new GetBookingsCurrent(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        assertChecks(bookingsCurrent.findBookings(booker.getId(), page), bookingsCurrent.getState(), State.CURRENT);
    }

    @Test
    void testGetBookingsFuture() {
        GetBookingsFuture bookingsFuture = new GetBookingsFuture(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(Long.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        assertChecks(bookingsFuture.findBookings(booker.getId(), page), bookingsFuture.getState(), State.FUTURE);
    }

    @Test
    void testGetBookingsPast() {
        GetBookingsPast bookingsPast = new GetBookingsPast(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        assertChecks(bookingsPast.findBookings(booker.getId(), page), bookingsPast.getState(), State.PAST);
    }

    @Test
    void testGetBookingWaiting() {
        GetBookingsWaiting bookingsWaiting = new GetBookingsWaiting(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(bookerId, Status.WAITING, page))
                .thenReturn(List.of(booking));
        assertChecks(bookingsWaiting.findBookings(booker.getId(), page), bookingsWaiting.getState(), State.WAITING);
    }


    @Test
    void testGetBookingsRejected() {
        GetBookingsRejected bookingsRejected = new GetBookingsRejected(bookingRepository, mapper);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(bookerId, Status.REJECTED, page))
                .thenReturn(List.of(booking));
        assertChecks(bookingsRejected.findBookings(booker.getId(), page), bookingsRejected.getState(), State.REJECTED);
    }

    @Test
    void testGetBookingsOwnerCurrent() {
        GetBookingsOwnerCurrent bookingsOwnerCurrent = new GetBookingsOwnerCurrent(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking));
        assertChecks(bookingsOwnerCurrent.findBookings(booker.getId(), page), bookingsOwnerCurrent.getState(), State.CURRENT);
    }

    @Test
    void testGetBookingsOwnerPast() {
        GetBookingsOwnerPast bookingsOwnerPast = new GetBookingsOwnerPast(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        assertChecks(bookingsOwnerPast.findBookings(booker.getId(), page), bookingsOwnerPast.getState(), State.PAST);
    }

    @Test
    void testGetBookingsOwnerFuture() {
        GetBookingsOwnerFuture bookingsOwnerFuture = new GetBookingsOwnerFuture(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(booking));
        assertChecks(bookingsOwnerFuture.findBookings(booker.getId(), page), bookingsOwnerFuture.getState(), State.FUTURE);
    }

    @Test
    void testGetBookingsOwnerWaiting() {
        GetBookingsOwnerWaiting bookingsOwnerWaiting = new GetBookingsOwnerWaiting(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(bookerId, Status.WAITING, page))
                .thenReturn(List.of(booking));
        assertChecks(bookingsOwnerWaiting.findBookings(booker.getId(), page), bookingsOwnerWaiting.getState(), State.WAITING);
    }

    @Test
    void testGetBookingsOwnerRejected() {
        GetBookingsOwnerRejected bookingsOwnerRejected = new GetBookingsOwnerRejected(bookingRepository, mapper);
        when(bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(bookerId, Status.REJECTED, page))
                .thenReturn(List.of(booking));
        assertChecks(bookingsOwnerRejected.findBookings(booker.getId(), page), bookingsOwnerRejected.getState(), State.REJECTED);
    }

    private void assertChecks(List<BookingDto> bookingList, State stateReceive, State stateCheck) {
        List<Booking> bookings = bookingList.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(stateReceive, stateCheck);
    }
}
