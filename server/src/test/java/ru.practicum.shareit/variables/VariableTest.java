package ru.practicum.shareit.variables;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariableTest {
    @Test
    void testVariable() {
        assertEquals("X-Sharer-User-Id", Variables.HEADER);
    }
}
