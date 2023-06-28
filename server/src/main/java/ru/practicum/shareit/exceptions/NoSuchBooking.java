package ru.practicum.shareit.exceptions;

public class NoSuchBooking extends RuntimeException {
    public NoSuchBooking(String message) {
        super(message);
    }
}
