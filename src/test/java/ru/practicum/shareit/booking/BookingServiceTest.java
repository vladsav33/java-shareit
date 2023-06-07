package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingServiceTest {
    private BookingServiceImpl bookingService;
    private ItemService itemService = Mockito.mock(ItemService.class);
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
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

    BookingServiceTest() {
    }

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
        booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        bookingService = new BookingServiceImpl(BookingMapper.INSTANCE, bookingRepository, itemService, userRepository, itemRepository);
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
}