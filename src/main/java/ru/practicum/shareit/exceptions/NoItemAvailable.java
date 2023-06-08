package ru.practicum.shareit.exceptions;

public class NoItemAvailable extends RuntimeException {
    public NoItemAvailable(String message) {
        super(message);
    }
}
