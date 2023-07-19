package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapperTest {

    @Test
    void shouldCreateItemShortDtoFromEntity() {
        Item item = TestValueBuilder.createItemWithId();

        ItemShortDto dto = ItemMapper.toShortDto(item);

        Assertions.assertEquals(item.getId(), dto.getId());
        Assertions.assertEquals(item.getName(), dto.getName());
        Assertions.assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void shouldCreateEntityFromItemShortDto() {
        ItemShortDto dto = TestValueBuilder.createAvailableItemShortDto("Item", "Item Desc");

        Item item = ItemMapper.toEntity(dto);

        Assertions.assertEquals(item.getId(), dto.getId());
        Assertions.assertEquals(item.getName(), dto.getName());
        Assertions.assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void shouldCreateItemDtoWithBookings() {
        Item item = TestValueBuilder.createItemWithId();

        List<CommentDto> comments = List.of(TestValueBuilder.createCommentDto());

        BookingDtoForItem prevBooking = TestValueBuilder.createBookingDtoForItem();
        BookingDtoForItem nextBooking = TestValueBuilder.createBookingDtoForItem();


        ItemDto dto = ItemMapper.toDto(item, prevBooking, nextBooking, comments);

        Assertions.assertEquals(item.getId(), dto.getId());
        Assertions.assertEquals(prevBooking.getId(), dto.getLastBooking().getId());
        Assertions.assertEquals(nextBooking.getId(), dto.getLastBooking().getId());
        Assertions.assertEquals(comments, dto.getComments());
    }
}
