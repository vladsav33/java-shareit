package ru.practicum.shareit.exceptions;

public class AlreadyApproved extends RuntimeException {
    public AlreadyApproved(String message) {
        super(message);
    }
}
