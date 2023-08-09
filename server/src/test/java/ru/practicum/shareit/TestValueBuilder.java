package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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

    public static ItemShortDto createAvailableItemShortDto(String name, String description) {
        return ItemShortDto.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }

    public static Item createItemWithId() {
        return Item.builder()
                .id(1L)
                .user(createUserWithId(1L))
                .name("Mouse")
                .description("Mouse for PC")
                .available(true)
                .build();
    }

    public static Booking createBookingWithId() {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(createItemWithId())
                .booker(createUserWithId(1L))
                .status(EStatus.APPROVED)
                .build();
    }

    public static ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Item")
                .available(true)
                .build();
    }

    public static BookingDto createBookingDto() {
        return BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(5))
                .build();
    }

    public static CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .text("Comment")
                .authorName("Arthur")
                .build();
    }

    public static SimpleItemRequestDto createSimpleItemRequestDto() {
        return SimpleItemRequestDto.builder()
                .id(1L)
                .description("Item request")
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDtoForClient createItemRequestDtoForClient(List<ItemShortDto> items) {
        return ItemRequestDtoForClient.builder()
                .id(1L)
                .description("Item request")
                .created(LocalDateTime.now())
                .items(items)
                .build();
    }

    public static ItemRequest createItemRequest(User user) {
        return ItemRequest.builder()
                .id(1L)
                .description("Item request")
                .created(LocalDateTime.now())
                .user(user)
                .build();
    }

    public static BookingDtoForItem createBookingDtoForItem() {
        return BookingDtoForItem.builder()
                .id(1L)
                .bookerId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
    }

    public static Comment createCommentWithId(User user, Item item) {
        return Comment.builder()
                .id(1L)
                .text("Text")
                .created(LocalDateTime.now())
                .user(user)
                .item(item)
                .build();
    }
}
