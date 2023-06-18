package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService service;
    private UserDto user;
    private long userId;

    @BeforeEach
    void initTest() {
        userId = 1;
        user = UserDto.builder().id(userId).name("name").email("name@mail.com").build();
    }

    @Test
    void testCreateUser() {
        UserDto userToCreate = service.createUser(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userToCheck = query.setParameter("email", userToCreate.getEmail()).getSingleResult();

        assertThat(userToCheck.getId(), notNullValue());
        assertThat(userToCheck.getName(), equalTo(userToCreate.getName()));
        assertThat(userToCheck.getEmail(), equalTo(userToCreate.getEmail()));
    }

/*    @Test
//    @Rollback(false)
    void testCreateUserNonUniqueEmail() {
        UserDto user1 = UserDto.builder().name("name").email("name@mail.com").build();
        UserDto user2 = UserDto.builder().name("name").email("name@mail.com").build();

        UserDto userToCreate = service.createUser(user1);
        assertThatThrownBy(() -> {
            service.createUser(user1);
        }).isInstanceOf(DuplicateEmail.class);
    }*/

    @Test
    void testUpdateUser() {
        UserDto userToCreate = service.createUser(user);
        user.setName("updated name");
        userToCreate = service.updateUser(userId, user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userToCheck = query.setParameter("email", userToCreate.getEmail()).getSingleResult();

        assertThat(userToCheck.getId(), notNullValue());
        assertThat(userToCheck.getName(), equalTo(userToCreate.getName()));
        assertThat(userToCheck.getEmail(), equalTo(userToCreate.getEmail()));
    }

    @Test
    void testDeleteUser() {
        userId = 1;
        user = UserDto.builder().name("name").email("name@mail.com").build();
        UserDto userToCreate = service.createUser(user);

        service.deleteUser(userToCreate.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);

        assertThatThrownBy(() -> {
            User userToCheck = query.setParameter("email", userToCreate.getEmail()).getSingleResult();
        }).isInstanceOf(NoResultException.class);
    }
}
