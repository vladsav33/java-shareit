package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    private ItemServiceImpl itemService;
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

        UserServiceImpl userService = new UserServiceImpl(UserMapper.INSTANCE, userRepository);
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
}