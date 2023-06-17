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
    private UserDto user;
    private UserDto userToCreate;
    private ItemDto item;
    private ItemDto itemToCreate;
    private long userId;
    private long itemId;

    @BeforeEach
    void initTest() {
        userId = 1;
        itemId = 1;
        user = UserDto.builder().name("name").email("name@mail.com").build();
        userToCreate = userService.createUser(user);
        item = ItemDto.builder().name("name").description("description").available(true).owner(userId).build();
        itemToCreate = itemService.createItem(userToCreate.getId(), item);
    }

    @Test
    void testCreateItem() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemToCheck = query.setParameter("id", itemToCreate.getId()).getSingleResult();

        assertThat(itemToCheck.getId(), notNullValue());
        assertThat(itemToCheck.getName(), equalTo(itemToCreate.getName()));
        assertThat(itemToCheck.getDescription(), equalTo(itemToCreate.getDescription()));
        assertThat(itemToCheck.getAvailable(), equalTo(itemToCreate.getAvailable()));
        assertThat(itemToCheck.getOwner(), equalTo(itemToCreate.getOwner()));
    }

    @Test
    void testUpdateItem() {
        item.setName("Updated name");
        itemToCreate = itemService.updateItem(userId, itemId, item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemToCheck = query.setParameter("id", itemId).getSingleResult();

        assertThat(itemToCheck.getId(), notNullValue());
        assertThat(itemToCheck.getName(), equalTo(itemToCreate.getName()));
        assertThat(itemToCheck.getDescription(), equalTo(itemToCreate.getDescription()));
        assertThat(itemToCheck.getAvailable(), equalTo(itemToCreate.getAvailable()));
        assertThat(itemToCheck.getOwner(), equalTo(itemToCreate.getOwner()));
    }
}
