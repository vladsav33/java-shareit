package ru.practicum.shareit.exceptions;

public class WrongUser extends RuntimeException {
    public WrongUser(String message) {
        super(message);
    }
}
