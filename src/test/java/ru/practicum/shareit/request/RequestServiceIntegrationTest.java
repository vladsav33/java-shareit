package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class RequestServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemRequestService service;
    private UserDto user;
    private ItemRequestDto request;
    private long userId;
    private long requestId;

    @BeforeEach
    void initTest() {
        userId = 1;
        requestId = 1;
    }

    @Test
    void testCreateRequest() {
        user = UserDto.builder().id(userId).name("name").email("name@mail.com").build();
        UserDto userToCreate = userService.createUser(user);

        request = ItemRequestDto.builder().id(requestId).created(LocalDateTime.now()).requestor(userId)
                .description("Request 1").items(null).build();
        ItemRequestDto requestToCreate = service.createRequest(userId, request);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest requestToCheck = query.setParameter("id", requestToCreate.getId()).getSingleResult();

        assertThat(requestToCheck.getId(), notNullValue());
        assertThat(requestToCheck.getRequestor(), equalTo(requestToCreate.getRequestor()));
        assertThat(requestToCheck.getDescription(), equalTo(requestToCreate.getDescription()));
        assertThat(requestToCheck.getCreated(), equalTo(requestToCreate.getCreated()));
    }
}
