package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@Import(PersistenceConfig.class)
public class RequestRepositoryTest {
    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    private ItemRequest request;
    private LocalDateTime created;
    private User user;
    private Pageable page;

    @BeforeEach
    void initTest() {
        created = LocalDateTime.of(2022, 12, 20, 11, 30, 40);
        user = User.builder().name("name").email("name@mail.com").build();
        userRepository.save(user);
        request = ItemRequest.builder().description("description").requestor(user.getId()).created(created).build();
        page = PageRequest.of(0, 20);
    }

    @Test
    void testCreateRequest() {
        assertEquals(request.getId(), 0);
        repository.save(request);
        assertNotEquals(request.getId(), 0);
    }

    @Test
    void testGetOwnRequests() {
        repository.save(request);
        List<ItemRequest> requests = repository.findAllByRequestorIs(user.getId());
        assertEquals(requests.get(0).getRequestor(), user.getId());
    }

    @Test
    void testGetAllRequests() {
        repository.deleteAll();
        repository.save(request);
        User booker = User.builder().name("booker").email("booker@mail.com").build();
        userRepository.save(booker);
        request = ItemRequest.builder().description("description").requestor(booker.getId()).created(created).build();
        repository.save(request);

        List<ItemRequest> requests = repository.findAllByRequestorIsNot(user.getId(), page);
        assertEquals(requests.get(0).getRequestor(), booker.getId());
    }

    @Test
    void testGetRequestById() {
        repository.save(request);
        ItemRequest requestToGet = repository.findById(request.getId()).orElseThrow();
        assertEquals(requestToGet.getId(), request.getId());
    }
}
