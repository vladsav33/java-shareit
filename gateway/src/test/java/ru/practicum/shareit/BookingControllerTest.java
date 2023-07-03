package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.variables.Variables.HEADER;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Mock
    private BookingClient client;
    @InjectMocks
    private BookingController controller;
    private long userId;
    private long bookingId;
    private BookItemRequestDto bookingToCreate;
    private ResponseEntity<Object> entity;

    @BeforeEach
    void initTest() {
        userId = 1;
        long itemId = 1;
        bookingId = 1;
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        bookingToCreate = BookItemRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        entity = new ResponseEntity<>(bookingToCreate, HttpStatus.OK);
    }

    @Test
    @SneakyThrows
    void getBookings() {
        when(client.getBookings(userId, BookingState.ALL, 0, 10)).thenReturn(entity);

        String response = mockMvc.perform(get("/bookings/").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).getBookings(userId, BookingState.ALL, 0, 10);
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @Test
    @SneakyThrows
    void bookItem() {
        when(client.bookItem(userId,
                objectMapper.readValue(objectMapper.writeValueAsString(bookingToCreate), BookItemRequestDto.class)))
                .thenReturn(entity);

        String response = mockMvc.perform(post("/bookings").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).bookItem(userId,
                objectMapper.readValue(objectMapper.writeValueAsString(bookingToCreate), BookItemRequestDto.class));
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @Test
    @SneakyThrows
    void getBooking() {
        when(client.getBooking(userId, bookingId)).thenReturn(entity);

        String response = mockMvc.perform(get("/bookings/" + bookingId).header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).getBooking(userId, bookingId);
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        when(client.updateBooking(userId, bookingId, true)).thenReturn(entity);

        String response = mockMvc.perform(patch("/bookings/" + bookingId + "?approved=true")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).updateBooking(userId, bookingId, true);
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }

    @Test
    @SneakyThrows
    void getBookingsByItemsOfUser() {
        when(client.getBookingsByItemsOfUser(userId, BookingState.ALL, 0, 20)).thenReturn(entity);

        String response = mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=20").header(HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(client).getBookingsByItemsOfUser(userId, BookingState.ALL, 0, 20);
        assertEquals(objectMapper.writeValueAsString(bookingToCreate), response);
    }
}