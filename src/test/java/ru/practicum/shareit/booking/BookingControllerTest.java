package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    User user;
    Item item;
    long userId;
    long itemId;
    long bookingId;

    @BeforeEach
    void initTest() {
        userId = 1;
        itemId = 1;
        bookingId = 1;
        user = User.builder().id(userId).name("name").email("mail@email.com").build();
        item = Item.builder().id(itemId).name("name").description("description").available(true).owner(userId).build();
    }

    @Test
    @SneakyThrows
    void createBooking() {
        BookingDto bookingToCreate = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user).status(Status.APPROVED).build();
        when(bookingService.createBooking(userId, bookingToCreate)).thenReturn(bookingToCreate);

        String response = mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(bookingService).createBooking(userId, bookingToCreate);
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        BookingDto bookingToUpdate = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(2))
                .item(item).booker(user).status(Status.APPROVED).build();
        when(bookingService.updateBooking(userId, bookingId, true)).thenReturn(bookingToUpdate);

        String response = mockMvc.perform(patch("/bookings/" + bookingId + "?approved=true")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(bookingService).updateBooking(userId, bookingId, true);
        assertEquals(objectMapper.writeValueAsString(bookingToUpdate), response);
    }

    @Test
    @SneakyThrows
    void getBookingById() {
        BookingDto bookingToCreate = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user).status(Status.APPROVED).build();
        when(bookingService.getBookingById(userId, bookingId)).thenReturn(bookingToCreate);

        String response = mockMvc.perform(get("/bookings/" + bookingId).header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(bookingService).getBookingById(userId, bookingId);
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @Test
    @SneakyThrows
    void getBookingsByUser() {
        BookingDto bookingToCreate = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user).status(Status.APPROVED).build();
        when(bookingService.getBookingsByUser(userId, State.ALL)).thenReturn(List.of(bookingToCreate));

        String response = mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(bookingService).getBookingsByUser(userId, State.ALL);
        assertEquals(objectMapper.writeValueAsString(List.of(bookingToCreate)), response);
    }

    @Test
    @SneakyThrows
    void getBookingsByItemsOfUser() {
        BookingDto bookingToCreate = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user).status(Status.APPROVED).build();
        when(bookingService.getBookingsByItemsOfUser(userId, State.ALL)).thenReturn(List.of(bookingToCreate));

        String response = mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(bookingService).getBookingsByItemsOfUser(userId, State.ALL);
        assertEquals(objectMapper.writeValueAsString(List.of(bookingToCreate)), response);
    }
}