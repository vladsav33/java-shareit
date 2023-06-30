package ru.practicum.shareit.booking.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.variables.Variables.HEADER;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    public final BookingService bookingService;

    @PostMapping()
    public BookingDto createBooking(@RequestHeader(HEADER) long userId, @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(HEADER) long userId, @PathVariable long bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseBody
    public BookingDto getBookingById(@RequestHeader(HEADER) long userId, @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    @ResponseBody
    public List<BookingDto> getBookingsByUser(@RequestHeader(HEADER) long userId,
                                              @RequestParam String state,
                                              @RequestParam int from,
                                              @RequestParam int size) {
        Pageable page = PageRequest.of(from / size, size);
        return bookingService.getBookingsByUser(userId, state, page);
    }

    @GetMapping("/owner")
    @ResponseBody
    public List<BookingDto> getBookingsByItemsOfUser(@RequestHeader(HEADER) long userId,
                                                     @RequestParam String state,
                                                     @RequestParam int from,
                                                     @RequestParam int size) {
        Pageable page = PageRequest.of(from / size, size);
        return bookingService.getBookingsByItemsOfUser(userId, state, page);
    }
}
