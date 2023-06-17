package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService service;
    private UserDto booker;
    private UserDto owner;
    private ItemDto item;
    private BookingDto booking;

    @BeforeEach
    void initTest() {
        owner = UserDto.builder().name("owner").email("owner@mail.com").build();
        booker = UserDto.builder().name("booker").email("booker@mail.com").build();
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);

        item = ItemDto.builder().name("name").description("description").owner(owner.getId()).available(true).build();
        item = itemService.createItem(owner.getId(), item);

        booking = BookingDto.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                .booker(booker).item(item).itemId(item.getId()).status(Status.WAITING).build();
    }

    @Test
    void testCreateBooking() {
        booking = service.createBooking(booker.getId(), booking);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingToCheck = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(bookingToCheck.getId(), notNullValue());
        assertThat(bookingToCheck.getStart(), equalTo(booking.getStart()));
        assertThat(bookingToCheck.getEnd(), equalTo(booking.getEnd()));
        assertThat(UserMapper.INSTANCE.toUserDto(bookingToCheck.getBooker()), equalTo(booking.getBooker()));
        assertThat(bookingToCheck.getStatus(), equalTo(booking.getStatus()));
        assertThat(ItemMapper.INSTANCE.toItemDto(bookingToCheck.getItem()), equalTo(booking.getItem()));
    }

    @Test
    void testUpdateBooking() {
        booking = service.createBooking(booker.getId(), booking);
        booking = service.updateBooking(booking.getItem().getOwner(), booking.getId(), true);
        booking.setStatus(Status.APPROVED);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingToCheck = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(bookingToCheck.getId(), notNullValue());
        assertThat(bookingToCheck.getStart(), equalTo(booking.getStart()));
        assertThat(bookingToCheck.getEnd(), equalTo(booking.getEnd()));
        assertThat(UserMapper.INSTANCE.toUserDto(bookingToCheck.getBooker()), equalTo(booking.getBooker()));
        assertThat(bookingToCheck.getStatus(), equalTo(booking.getStatus()));
        assertThat(ItemMapper.INSTANCE.toItemDto(bookingToCheck.getItem()), equalTo(booking.getItem()));
    }
}
