package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(PersistenceConfig.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;
    private User user;

    @BeforeEach
    void initTest() {
        user = User.builder().name("name").email("name@mail.com").build();
    }

    @Test
    void testCreateUser() {
        assertEquals(user.getId(), 0);
        repository.save(user);
        assertNotEquals(user.getId(), 0);
    }

    @Test
    void testUpdateUser() {
        repository.save(user);
        User userToFind = repository.findById(user.getId()).orElseThrow();
        assertEquals(user.getId(), userToFind.getId());
    }

    @Test
    void testDeleteUser() {
        repository.save(user);
        User userToFind = repository.findById(user.getId()).orElseThrow();
        assertEquals(user.getId(), userToFind.getId());
        repository.deleteById(user.getId());
        userToFind = repository.findById(user.getId()).orElse(null);
        assertNull(userToFind);
    }

    @Test
    void testGetAllUsers() {
        repository.save(user);
        List<User> usersToFind = repository.findAll();
        assertTrue(usersToFind.size() > 0);
    }
}
