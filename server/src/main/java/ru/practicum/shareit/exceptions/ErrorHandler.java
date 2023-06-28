package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controllers.BookingController;
import ru.practicum.shareit.item.controllers.ItemController;
import ru.practicum.shareit.request.controllers.ItemRequestController;
import ru.practicum.shareit.user.controllers.UserController;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class,
        BookingController.class, ItemRequestController.class})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(NoSuchUser.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NoSuchUser exception) {
        log.warn(exception.getMessage());
        return Map.of("Object not found", "No such user");
    }

    @ExceptionHandler(NoSuchItem.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NoSuchItem exception) {
        log.warn(exception.getMessage());
        return Map.of("Object not found", "No such item");
    }

    @ExceptionHandler(NoSuchRequest.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNorFound(final NoSuchRequest exception) {
        log.warn((exception.getMessage()));
        return Map.of("Object not found", "No such request");
    }

    @ExceptionHandler(NoSuchBooking.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NoSuchBooking exception) {
        log.warn(exception.getMessage());
        return Map.of("Object not found", "No such booking");
    }

    @ExceptionHandler(NoItemAvailable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoItemAvailable(final NoItemAvailable exception) {
        log.warn(exception.getMessage());
        return Map.of("Object not available", "This item is not available");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongUser(final WrongUser exception) {
        log.warn(exception.getMessage());
        return Map.of("Wrong user for the item update", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnknownState(final UnknownState exception) {
        log.warn(exception.getMessage());
        return Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAlreadyApproved(final AlreadyApproved exception) {
        log.warn(exception.getMessage());
        return Map.of("The booking was already approved", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationError(final ValidationException exception) {
        log.warn(exception.getMessage());
        return Map.of("Data validation error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalErrors(final RuntimeException exception) {
        log.warn(exception.getMessage());
        return Map.of("Internal Server Error", exception.getMessage());
    }
}