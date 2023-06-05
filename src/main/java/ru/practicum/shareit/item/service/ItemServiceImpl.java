package ru.practicum.shareit.item.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.NoSuchItem;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongUser;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Getter
@Setter
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemMapper itemMapper, CommentMapper commentMapper, UserService userService, ItemRepository itemRepository,
                           CommentRepository commentRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public ItemDto createItem(long userId, ItemDto itemDto) {
        if (!userService.checkUserExists(userId)) {
            log.error("User {} was not found", userId);
            throw new NoSuchUser("No such user");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Validation error for the user {}, no available data", userId);
            throw new ValidationException("Validation error");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        itemRepository.save(item);
        log.info("Item {} was created", item);
        return itemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemUpdate) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItem("Item was not found"));
        if (item.getOwner() != userId) {
            log.error("Wrong user {} to update item {}", userId, itemId);
            throw new WrongUser("Wrong user for the update");
        }

        if (itemUpdate.getName() != null) {
            item.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null) {
            item.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getOwner() != null) {
            item.setOwner(itemUpdate.getOwner());
        }
        if (itemUpdate.getAvailable() != null) {
            item.setAvailable(itemUpdate.getAvailable());
        }
        itemRepository.save(item);
        log.info("Item {} was updated", item);
        return itemMapper.toItemDto(item);
    }

    public ItemDto getItemById(long itemId, long userId) {
        log.info("Item {} was retrieved", itemId);
        ItemDto itemDto = itemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItem("Item was not found")));
        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                        .map(commentMapper::toCommentDto).collect(Collectors.toList()));

        if (itemDto.getOwner() != userId) {
            return itemDto;
        }
        List<Booking> bookings = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(itemId,
                LocalDateTime.now(), Status.REJECTED);
        if (!bookings.isEmpty()) {
            LastNextBookingDto bookingDto = getLastNextBookingDto(bookings);
            itemDto.setLastBooking(bookingDto);
        }
        bookings = bookingRepository.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(itemId,
                LocalDateTime.now(), Status.REJECTED);
        if (!bookings.isEmpty()) {
            LastNextBookingDto bookingDto = getLastNextBookingDto(bookings);
            itemDto.setNextBooking(bookingDto);
        }
        return itemDto;
    }

    public List<ItemDto> getItemsByUser(long userId) {
        log.info("Items were retrieved for the user {}", userId);

        List<ItemDto> itemsDto = itemRepository.findAllByOwnerIsOrderById(userId).stream()
                .map(itemMapper::toItemDto).collect(Collectors.toList());
        itemsDto.forEach(itemDto -> itemDto
                        .setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                                .map(commentMapper::toCommentDto).collect(Collectors.toList())));
        itemsDto.forEach(itemDto -> {
            List<Booking> bookings = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(itemDto.getId(),
                    LocalDateTime.now(), Status.REJECTED);
            log.info("Current time is {}, bookings.size is {}", LocalDateTime.now(), bookings.size());
            if (!bookings.isEmpty()) {
                LastNextBookingDto bookingDto = getLastNextBookingDto(bookings);
                itemDto.setLastBooking(bookingDto);
            }
            bookings = bookingRepository.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(itemDto.getId(),
                    LocalDateTime.now(), Status.REJECTED);
            log.info("Current time is {}, bookings.size is {}", LocalDateTime.now(), bookings.size());
            if (!bookings.isEmpty()) {
                LastNextBookingDto bookingDto = getLastNextBookingDto(bookings);
                itemDto.setNextBooking(bookingDto);
            }
        });
        return itemsDto;
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            log.info("Empty search string");
            return new ArrayList<>();
        }
        log.info("Items were retrieved for the search text {}", text);
        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);

        if (comment.getText().isBlank()) {
            throw new ValidationException("Text cannot be empty");
        }
        if (bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("User doesn't have any bookings to write comments");
        }

        comment.setAuthor(userRepository.getById(userId));
        comment.setCreated(LocalDateTime.now());
        comment.setItemId(itemId);
        commentDto = commentMapper.toCommentDto(commentRepository.save(comment));
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }

    public boolean isItemAvailable(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchItem("Item was not found"));
        return item.getAvailable();
    }

    private LastNextBookingDto getLastNextBookingDto(List<Booking> bookings) {
        return LastNextBookingDto.builder()
                .id(bookings.get(0).getId())
                .bookerId(bookings.get(0).getBooker().getId())
                .build();
    }
}
