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

    public static Item createAvailableItem(Long userId, String name, String description) {
        return Item.builder()
                .userId(userId)
                .name(name)
                .description(description)
                .available(true)
                .build();
    }
}
