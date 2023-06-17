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
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private long itemId;
    private BookingDto bookingDto;
    private Booking booking;
    private UserDto ownerDto;
    private User booker;
    private UserDto bookerDto;
    private ItemDto itemDto;
    private Item item;
    private BookingDto bookingToCreate;
    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);
    private Pageable page;

    @BeforeEach
    void initTest() {
        bookingId = 1;
        ownerId = 1;
        bookerId = 2;
        itemId = 1;
        ownerDto = UserDto.builder().id(ownerId).name("owner").email("owner@mail.com").build();
        bookerDto = UserDto.builder().id(bookerId).name("booker").email("booker@mail.com").build();
        itemDto = ItemDto.builder().id(itemId).name("name").description("description").owner(ownerId).available(true).build();
        booker = User.builder().id(bookerId).name("booker").email("booker@mail.com").build();
        item = Item.builder().id(itemId).name("name").description("description").owner(ownerId).available(true).build();
        bookingDto = BookingDto.builder().id(bookingId).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                .booker(bookerDto).item(itemDto).status(Status.WAITING).build();
        booking = mapper.toBooking(bookingDto);
        bookingService = new BookingServiceImpl(mapper, bookingRepository, itemService, userRepository, itemRepository);
        page = PageRequest.of(0, 20);
    }

    @Test
    void createBooking() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        assertEquals(bookingToCreate, bookingDto);
    }

    @Test
    void updateBooking() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(mapper.toBooking(bookingToCreate)));
        BookingDto bookingToUpdate = bookingService.updateBooking(ownerId, bookingId, true);
        bookingDto.setStatus(Status.APPROVED);
        assertEquals(bookingToUpdate, bookingDto);
    }

    @Test
    void getBookingById() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(mapper.toBooking(bookingToCreate)));
        BookingDto bookingToGet = bookingService.getBookingById(bookerId, bookingId);
        assertEquals(bookingToGet, bookingDto);
    }

    @Test
    void getBookingsByUser() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId, page))
                .thenReturn(List.of(mapper.toBooking(bookingToCreate)));
        List<BookingDto> bookingToGet = bookingService.getBookingsByUser(bookerId, "ALL", page);
        assertEquals(bookingToGet, List.of(bookingDto));
    }

    @Test
    void getBookingsByItemsOfUser() {
        when(itemService.isItemAvailable(any(Long.class))).thenReturn(true);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        bookingToCreate = bookingService.createBooking(bookerId, bookingDto);
        when(bookingRepository.findByItemOwnerOrderByStartDesc(ownerId, page))
                .thenReturn(List.of(mapper.toBooking(bookingToCreate)));
        List<BookingDto> bookingToGet = bookingService.getBookingsByItemsOfUser(ownerId, "ALL", page);
        assertEquals(bookingToGet, List.of(bookingDto));
    }
}