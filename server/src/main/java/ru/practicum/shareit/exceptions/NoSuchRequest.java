package ru.practicum.shareit.exceptions;

public class NoSuchRequest extends RuntimeException {
    public NoSuchRequest(String message) {
        super(message);
    }
}
