package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User testingUser;

    @Test
    void shouldNoEqualsWhenDifferentUser() {
        User user = null;
        testingUser = new User();

        Assertions.assertNotEquals(testingUser, user);
    }

    @Test
    void shouldNoEqualsWhenDifferentClass() {
        testingUser = new User();
        Object user = new Object();

        Assertions.assertNotEquals(testingUser, user);
    }

    @Test
    void shouldNotEqualsWhenIdIsDifferent() {
        testingUser = User.builder().id(1L).build();
        User user = User.builder().id(2L).name("Test").build();

        Assertions.assertNotEquals(testingUser, user);
    }
}
