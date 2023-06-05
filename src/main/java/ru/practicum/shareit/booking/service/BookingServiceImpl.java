package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.AlreadyApproved;
import ru.practicum.shareit.exceptions.NoItemAvailable;
import ru.practicum.shareit.exceptions.NoSuchBooking;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongUser;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingMapper bookingMapper, BookingRepository bookingRepository,
                              ItemService itemService, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingMapper = bookingMapper;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        log.info("Looking for itemId {}", bookingDto.getItemId());
        if (!itemService.isItemAvailable(bookingDto.getItemId())) {
            throw new NoItemAvailable("This item is not available");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Incorrect start or end dates");
        }
        Booking booking = bookingMapper.toBooking(bookingDto);
        User booker = userRepository.findById(userId).get();
        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        if (booker.getId() == item.getOwner()) {
            throw new WrongUser("Cannot book items already owned by the booker");
        }
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        log.info("Creating booking with start date {} and end date {}", booking.getStart(), booking.getEnd());
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchBooking("Booking was not found"));
        if (booking.getItem().getOwner() != userId) {
            throw new WrongUser("User is not a booker for this booking");
        }
        if (approved && (booking.getStatus() == Status.APPROVED)) {
            throw new AlreadyApproved("This booking was already approved");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchBooking("Booking was not found"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new WrongUser("No bookings for this user");
        }
        return bookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getBookingsByUser(long userId, State state) {
        List<BookingDto> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (bookings.isEmpty()) {
            throw new NoSuchBooking("Bookings were not found");
        }
        return bookings;
    }

    public List<BookingDto> getBookingsByItemsOfUser(long userId, State state) {
        List<BookingDto> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerOrderByStartDesc(userId).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(userId, Status.REJECTED).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatusIsOrderByStartDesc(userId, Status.WAITING).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (bookings.isEmpty()) {
            throw new NoSuchBooking("Bookings were not found");
        }
        return bookings;
    }
}
