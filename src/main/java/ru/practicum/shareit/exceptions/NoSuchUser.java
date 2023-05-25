package ru.practicum.shareit.exceptions;

public class NoSuchUser extends RuntimeException {
    public NoSuchUser(String message) {
        super(message);
    }
}
