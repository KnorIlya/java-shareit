package ru.practicum.shareit;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class TestValueBuilder {
    public static User createUserWithId(Long id) {
        return User.builder()
                .id(id)
                .name("User")
                .email("user@user.com")
                .build();
    }

    public static User createUserWithoutId() {
        return User.builder()
                .name("User")
                .email("user@user.com")
                .build();
    }

    public static User createCustomUserWithoutId(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .build();
    }

    public static Item createAvailableItemWithoutUser(String name, String description) {
        return Item.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }
}
