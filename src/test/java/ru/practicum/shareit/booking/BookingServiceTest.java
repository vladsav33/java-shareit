package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.AlreadyApproved;
import ru.practicum.shareit.exceptions.NoItemAvailable;
import ru.practicum.shareit.exceptions.NoSuchBooking;
import ru.practicum.shareit.exceptions.UnknownState;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongUser;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingServiceTest {
    private BookingServiceImpl bookingService;
    private final ItemService itemService = Mockito.mock(ItemService.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private long bookingId;
    private long ownerId;
    private long bookerId;
    private BookingDto bookingDto;
    private Booking booking;
    private User booker;
    private Item item;
    private BookingDto bookingToCreate;
    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
    private Pageable page;

    @BeforeEach
    void initTest() {
        bookingId = 1;
        ownerId = 1;
        bookerId = 2;
        long itemId = 1;
        UserDto ownerDto = UserDto.builder().id(ownerId).name("owner").email("owner@mail.com").build();
        UserDto bookerDto = UserDto.builder().id(bookerId).name("booker").email("booker@mail.com").build();
        ItemDto itemDto = ItemDto.builder().id(itemId).name("name").description("description").owner(ownerId).available(true).build();
        booker = User.builder().id(bookerId).name("booker").email("booker@mail.com").build();
        item = Item.builder().id(itemId).name("name").description("description").owner(ownerId).available(true).build();
        bookingDto = BookingDto.builder().id(bookingId).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                .booker(bookerDto).item(itemDto).status(Status.WAITING).build();
        booking = mapper.toBooking(bookingDto);
        bookingService = new BookingServiceImpl(mapper, bookingRepository, itemService, userRepository, itemRepository);
        page = PageRequest.of(0, 20);
    }

    @Test
    void createBookingValid() {
        whenReturn();
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        assertEquals(bookingToCreate, bookingDto);
    }

    @Test
    void createBookingNullStart() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        bookingDto.setStart(null);
        assertThatThrownBy(() -> {
            bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        }).isInstanceOf(ValidationException.class).hasMessage("Incorrect start or end dates");
    }

    @Test
    void createBookingOwnItem() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        item.setOwner(booker.getId());
        assertThatThrownBy(() -> {
            bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        }).isInstanceOf(WrongUser.class).hasMessage("Cannot book items already owned by the booker");
    }

    @Test
    void createBookingItemNotAvailable() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(false);
        assertThatThrownBy(() -> {
            bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        }).isInstanceOf(NoItemAvailable.class).hasMessage("This item is not available");
    }

    @Test
    void updateBooking() {
        whenReturn();
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(mapper.toBooking(bookingToCreate)));
        BookingDto bookingToUpdate = bookingService.updateBooking(ownerId, bookingId, true);
        bookingDto.setStatus(Status.APPROVED);
        assertEquals(bookingToUpdate, bookingDto);
    }

    @Test
    void updateBookingNotOwner() {
        bookingDto.getItem().setOwner(99L);
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(mapper.toBooking(bookingDto)));
        assertThatThrownBy(() -> {
            bookingService.updateBooking(ownerId, bookingId, true);
        }).isInstanceOf(WrongUser.class).hasMessage("User is not an owner of this item for this booking");
    }

    @Test
    void updateBookingAlreadyApproved() {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(mapper.toBooking(bookingDto)));
        assertThatThrownBy(() -> {
            bookingService.updateBooking(ownerId, bookingId, true);
        }).isInstanceOf(AlreadyApproved.class).hasMessage("This booking was already approved");
    }

    @Test
    void getBookingByIdValid() {
        whenReturn();
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(mapper.toBooking(bookingToCreate)));
        BookingDto bookingToGet = bookingService.getBookingById(bookerId, bookingId);
        assertEquals(bookingToGet, bookingDto);
    }

    @Test
    void getBookingsByUserNotFound() {
        bookingDto.getBooker().setId(99);
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId, page))
                .thenReturn(new ArrayList<>());
        assertThatThrownBy(() -> {
            bookingService.getBookingsByUser(bookerId, "ALL", page);
        }).isInstanceOf(NoSuchBooking.class).hasMessage("Bookings were not found");
    }

    @Test
    void getBookingsByUserInvalidState() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId, page))
                .thenReturn(new ArrayList<>());
        assertThatThrownBy(() -> {
            bookingService.getBookingsByUser(bookerId, "UNKNOWN", page);
        }).isInstanceOf(UnknownState.class).hasMessage("UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingsByUserValid() {
        whenReturn();
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId, page))
                .thenReturn(List.of(mapper.toBooking(bookingToCreate)));
        List<BookingDto> bookingToGet = bookingService.getBookingsByUser(bookerId, "ALL", page);
        assertEquals(bookingToGet, List.of(bookingDto));
    }

    @Test
    void getBookingsByItemsOfUser() {
        whenReturn();
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findByItemOwnerOrderByStartDesc(ownerId, page))
                .thenReturn(List.of(mapper.toBooking(bookingToCreate)));
        List<BookingDto> bookingToGet = bookingService.getBookingsByItemsOfUser(ownerId, "ALL", page);
        assertEquals(bookingToGet, List.of(bookingDto));
    }

    private void whenReturn() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    }
}