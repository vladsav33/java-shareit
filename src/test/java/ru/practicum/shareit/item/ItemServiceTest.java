package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.NoSuchItem;
import ru.practicum.shareit.exceptions.NoSuchUser;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongUser;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    private ItemServiceImpl itemService;
    UserServiceImpl userService;
    private Item item;
    private ItemDto itemDto;
    private long itemId;
    private long userId;
    private UserDto userDto;

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @BeforeEach
    public void initTest() {
        itemId = 1;
        userId = 1;

        userService = new UserServiceImpl(UserMapper.INSTANCE, userRepository);
        itemService = new ItemServiceImpl(ItemMapper.INSTANCE, commentMapper, userService, itemRepository, commentRepository, bookingRepository, userRepository);

        item = Item.builder().id(itemId).name("name").description("description").available(true).owner(userId).build();
        itemDto = ItemDto.builder().id(itemId).name("name").description("description").available(true).build();
        userDto = UserDto.builder().name("name").email("name@mail.com").build();
        userService.createUser(userDto);
        when(userService.checkUserExists(any(Long.class))).thenReturn(true);
    }

    @Test
    void createItem() {
        when(itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto))).thenReturn(ItemMapper.INSTANCE.toItem(itemDto));
        ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        assertEquals(ItemMapper.INSTANCE.toItemDto(item), itemToCreate);
    }

    @Test
    void createItemNoUser() {
        when(userService.checkUserExists(any(Long.class))).thenReturn(false);
        assertThatThrownBy(() -> {
            ItemDto itemToCreate = itemService.createItem(99, itemDto);
        }).isInstanceOf(NoSuchUser.class).hasMessage("No such user");
    }

    @Test
    void createItemNoAvailable() {
        itemDto = ItemDto.builder().id(itemId).name("name").description("description").build();
        assertThatThrownBy(() -> {
            ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        }).isInstanceOf(ValidationException.class).hasMessage("Validation error");
    }

    @Test
    void updateItem() {
        ItemDto itemUpdate = ItemDto.builder().name("name updated").build();
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(item));

        ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        itemToCreate.setName("name updated");
        ItemDto itemActual = itemService.updateItem(userId, itemId, itemUpdate);
        assertEquals(itemToCreate, itemActual);
    }

    @Test
    void updateItemWrongUser() {
        item = Item.builder().id(itemId).name("name").description("description").owner(99L).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        assertThatThrownBy(() -> {
            ItemDto itemToCreate = itemService.updateItem(userId, itemId, itemDto);
        }).isInstanceOf(WrongUser.class).hasMessage("Wrong user for the update");
    }

    @Test
    void getItemById() {
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());

        ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        itemToCreate.setComments(new ArrayList<>());

        ItemDto itemActual = itemService.getItemById(itemId, userId);
        assertEquals(itemToCreate, itemActual);
    }

    @Test
    void getItemByIdNotFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> {
            ItemDto itemActual = itemService.getItemById(itemId, userId);
        }).isInstanceOf(NoSuchItem.class).hasMessage("Item was not found");
    }

    @Test
    void getItemsByUser() {
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findAllByOwnerIsOrderById(userId)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());

        ItemDto itemToCreate = itemService.createItem(userId, itemDto);
        itemToCreate.setComments(new ArrayList<>());

        List<ItemDto> itemActual = itemService.getItemsByUser(userId);
        assertEquals(List.of(itemToCreate), itemActual);
    }

    @Test
    void searchItems() {
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.search("description")).thenReturn(List.of(item));

        ItemDto itemToCreate = itemService.createItem(userId, itemDto);

        List<ItemDto> itemActual = itemService.searchItems("description");
        assertEquals(List.of(itemToCreate), itemActual);
    }

    @Test
    void searchItemsBlank() {
          List<ItemDto> itemActual = itemService.searchItems("");
          assertTrue(itemActual.isEmpty());

    }

    @Test
    void createComment() {
        long bookingId = 1;
        BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
        Comment comment = Comment.builder()
                .created(LocalDateTime.of(2021, 12, 1, 12, 11, 10))
                .itemId(1).text("Comment").author(UserMapper.INSTANCE.toUser(userDto)).build();
        BookingDto bookingDto = BookingDto.builder().id(bookingId).start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .booker(userDto).item(itemDto).status(Status.APPROVED).build();

        when(userRepository.getById(userDto.getId())).thenReturn(UserMapper.INSTANCE.toUser(userDto));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingMapper.toBooking(bookingDto)));
        CommentDto commentToCreate = itemService.createComment(userDto.getId(), comment.getItemId(),
                commentMapper.toCommentDto(comment));
        assertEquals(commentToCreate, commentMapper.toCommentDto(comment));
    }

    @Test
    void createCommentNoText() {
        Comment comment = Comment.builder()
                .created(LocalDateTime.of(2021, 12, 1, 12, 11, 10))
                .itemId(1).text("").author(UserMapper.INSTANCE.toUser(userDto)).build();
        assertThatThrownBy(() -> {
            CommentDto commentToCreate = itemService.createComment(userDto.getId(), comment.getItemId(),
                    commentMapper.toCommentDto(comment));
        }).isInstanceOf(ValidationException.class).hasMessage("Text cannot be empty");
    }

    @Test
    void createCommentNoBookings() {
        Comment comment = Comment.builder()
                .created(LocalDateTime.of(2021, 12, 1, 12, 11, 10))
                .itemId(1).text("Text").author(UserMapper.INSTANCE.toUser(userDto)).build();
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        assertThatThrownBy(() -> {
            CommentDto commentToCreate = itemService.createComment(userDto.getId(), comment.getItemId(),
                    commentMapper.toCommentDto(comment));
        }).isInstanceOf(ValidationException.class).hasMessage("User doesn't have any bookings to write comments");
    }
}