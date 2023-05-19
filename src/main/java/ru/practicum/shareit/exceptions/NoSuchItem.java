package ru.practicum.shareit.exceptions;

public class NoSuchItem extends RuntimeException{
    public NoSuchItem(String message) {
        super(message);
    }
}
