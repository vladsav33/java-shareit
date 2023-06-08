package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.component.Chain;
import ru.practicum.shareit.booking.component.GetBookingsAll;
import ru.practicum.shareit.booking.component.GetBookingsCurrent;
import ru.practicum.shareit.booking.component.GetBookingsFuture;
import ru.practicum.shareit.booking.component.GetBookingsOwnerAll;
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
import ru.practicum.shareit.exceptions.AlreadyApproved;
import ru.practicum.shareit.exceptions.NoItemAvailable;
import ru.practicum.shareit.exceptions.NoSuchBooking;
import ru.practicum.shareit.exceptions.NoSuchItem;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.exceptions.UnknownState;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongUser;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

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
        User booker = userRepository.findById(userId).orElseThrow(() -> new NoSuchUser("Booker was not found"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NoSuchItem("No item to book"));
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
            throw new WrongUser("User is not an owner of this item for this booking");
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

    public List<BookingDto> getBookingsByUser(long userId, String state) {
        Chain chain = Chain.link(new GetBookingsAll(bookingRepository, bookingMapper),
                   new GetBookingsRejected(bookingRepository, bookingMapper),
                   new GetBookingsWaiting(bookingRepository, bookingMapper),
                   new GetBookingsPast(bookingRepository, bookingMapper),
                   new GetBookingsFuture(bookingRepository, bookingMapper),
                   new GetBookingsCurrent(bookingRepository, bookingMapper));
        List<BookingDto> bookings = chain.processRequest(userId, stringToState(state));

        if (bookings.isEmpty()) {
            throw new NoSuchBooking("Bookings were not found");
        }
        return bookings;
    }

    public List<BookingDto> getBookingsByItemsOfUser(long userId, String state) {
        Chain chain = Chain.link(new GetBookingsOwnerAll(bookingRepository, bookingMapper),
                new GetBookingsOwnerRejected(bookingRepository, bookingMapper),
                new GetBookingsOwnerWaiting(bookingRepository, bookingMapper),
                new GetBookingsOwnerPast(bookingRepository, bookingMapper),
                new GetBookingsOwnerFuture(bookingRepository, bookingMapper),
                new GetBookingsOwnerCurrent(bookingRepository, bookingMapper));
        List<BookingDto> bookings = chain.processRequest(userId, stringToState(state));

        if (bookings.isEmpty()) {
            throw new NoSuchBooking("Bookings were not found");
        }
        return bookings;
    }

    private State stringToState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new UnknownState("UNSUPPORTED_STATUS");
        }
    }
}
