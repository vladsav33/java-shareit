package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@Import(PersistenceConfig.class)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User booker;
    private Booking booking;
    private Pageable page;

    @BeforeEach
    void initTest() {
        booker = User.builder().name("booker").email("booker@mail.com").build();
        User owner = User.builder().name("owner").email("owner@mail.com").build();
        userRepository.save(booker);
        userRepository.save(owner);
        Item item = Item.builder().name("name").description("description").available(true).owner(owner.getId()).build();
        itemRepository.save(item);
        booking = Booking.builder().booker(booker)
                .start(LocalDateTime.of(2022, 12, 20, 11, 30, 40))
                .end(LocalDateTime.of(2022, 12, 21, 12, 30, 40))
                .item(item).status(Status.APPROVED).build();
        page = PageRequest.of(0, 20);
    }

    @Test
    void testCreateBooking() {
        assertEquals(booking.getId(), 0);
        repository.save(booking);
        assertNotEquals(booking.getId(), 0);
    }

    @Test
    void testGetBookingById() {
        repository.save(booking);
        Booking bookingToGet = repository.findById(booking.getId()).orElseThrow();
        assertEquals(bookingToGet.getId(), booking.getId());
    }

    @Test
    void testGetBookingsByUser() {
        repository.save(booking);
        List<Booking> bookings = repository.findByBookerIdOrderByStartDesc(booking.getBooker().getId(), page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByUserAndStatus() {
        repository.save(booking);
        List<Booking> bookings = repository.findByBookerIdAndStatusIsOrderByStartDesc(booking.getBooker().getId(),
                Status.APPROVED, page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByUserFuture() {
        repository.save(booking);
        List<Booking> bookings = repository.findByBookerIdAndStartAfterOrderByStartDesc(booking.getBooker().getId(),
                LocalDateTime.of(2022, 12, 19, 23, 11, 30), page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByUserPast() {
        repository.save(booking);
        List<Booking> bookings = repository.findByBookerIdAndEndBeforeOrderByStartDesc(booking.getBooker().getId(),
                LocalDateTime.of(2022, 12, 22, 23, 11, 30), page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByUserCurrent() {
        repository.save(booking);
        List<Booking> bookings = repository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(booking.getBooker().getId(),
                LocalDateTime.of(2022, 12, 20, 23, 11, 30),
                LocalDateTime.of(2022, 12, 20, 23, 11, 30),
                page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByOwner() {
        repository.save(booking);
        List<Booking> bookings = repository.findByItemOwnerOrderByStartDesc(booking.getItem().getOwner(), page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByOwnerAndStatus() {
        repository.save(booking);
        List<Booking> bookings = repository.findByItemOwnerAndStatusIsOrderByStartDesc(booking.getItem().getOwner(),
                Status.APPROVED, page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByOwnerFuture() {
        repository.save(booking);
        List<Booking> bookings = repository.findByItemOwnerAndStartAfterOrderByStartDesc(booking.getItem().getOwner(),
                LocalDateTime.of(2022, 12, 19, 23, 11, 30), page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByOwnerPast() {
        repository.save(booking);
        List<Booking> bookings = repository.findByItemOwnerAndEndBeforeOrderByStartDesc(booking.getItem().getOwner(),
                LocalDateTime.of(2022, 12, 22, 23, 11, 30), page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByOwnerCurrent() {
        repository.save(booking);
        List<Booking> bookings = repository
                .findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(booking.getItem().getOwner(),
                        LocalDateTime.of(2022, 12, 20, 23, 11, 30),
                        LocalDateTime.of(2022, 12, 20, 23, 11, 30),
                        page);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByItemLast() {
        repository.save(booking);
        List<Booking> bookings = repository.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(booking.getItem().getId(),
                LocalDateTime.of(2022, 12, 24, 23, 11, 30), Status.REJECTED);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void testGetBookingsByItemNext() {
        repository.save(booking);
        List<Booking> bookings = repository.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(booking.getItem().getId(),
                LocalDateTime.of(2022, 12, 19, 23, 11, 30), Status.REJECTED);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }
}
