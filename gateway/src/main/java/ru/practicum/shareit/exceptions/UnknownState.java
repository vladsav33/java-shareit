package ru.practicum.shareit.exceptions;

public class UnknownState extends RuntimeException {
    public UnknownState(String message) {
        super(message);
    }
}
