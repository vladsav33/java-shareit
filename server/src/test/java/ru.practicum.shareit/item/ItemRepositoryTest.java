package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@Import(PersistenceConfig.class)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    private Item item;

    @BeforeEach
    void initTest() {
        item = Item.builder().name("name").description("description").available(true).build();
    }

    @Test
    void testCreateItem() {
        assertEquals(item.getId(), 0);
        repository.save(item);
        assertNotEquals(item.getId(), 0);
    }

    @Test
    void testUpdateItem() {
        repository.save(item);
        Item itemToFind = repository.findById(item.getId()).orElseThrow();
        assertEquals(item.getId(), itemToFind.getId());
    }

    @Test
    void testGetItemByUser() {
        User user = User.builder().name("name").email("name@mail.com").build();
        userRepository.save(user);
        item = Item.builder().name("name").description("description").available(true).owner(user.getId()).build();
        repository.save(item);
        List<Item> itemsToFind = repository.findAllByOwnerIsOrderById(user.getId());
        assertEquals(itemsToFind.get(0).getOwner(), user.getId());
    }

    @Test
    void testSearchItems() {
        repository.save(item);
        List<Item> itemsToFind = repository.search("description");
        assertEquals(itemsToFind.get(0).getDescription(), "description");
    }
}
