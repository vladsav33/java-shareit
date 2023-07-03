package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto userToCreate;
    private ItemDto item;
    private ItemDto itemToCreate;

    @BeforeEach
    void initTest() {
        UserDto user = UserDto.builder().name("name").email("name@mail.com").build();
        userToCreate = userService.createUser(user);
        item = ItemDto.builder().name("name").description("description").available(true).owner(userToCreate.getId()).build();
        itemToCreate = itemService.createItem(userToCreate.getId(), item);
    }

    @Test
    void testCreateItem() {
        assertChecks(itemToCreate, itemToCreate.getId());
    }

    @Test
    void testUpdateItem() {
        long itemId = 1;
        item.setName("Updated name");
        itemToCreate = itemService.updateItem(userToCreate.getId(), itemToCreate.getId(), item);

        assertChecks(itemToCreate, itemId);
    }

    private void assertChecks(ItemDto itemToCreate, long itemId) {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemToCheck = query.setParameter("id", itemId).getSingleResult();

        assertThat(itemToCheck.getId(), notNullValue());
        assertThat(itemToCheck.getName(), equalTo(itemToCreate.getName()));
        assertThat(itemToCheck.getDescription(), equalTo(itemToCreate.getDescription()));
        assertThat(itemToCheck.getAvailable(), equalTo(itemToCreate.getAvailable()));
        assertThat(itemToCheck.getOwner(), equalTo(itemToCreate.getOwner()));
    }
}
