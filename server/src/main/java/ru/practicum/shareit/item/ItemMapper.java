package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemShortDto toShortDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())

                .build();
    }

    public static Item toEntity(ItemShortDto dto) {
        return Item.builder()
                .name(dto.getName())
                .available(dto.getAvailable())
                .description(dto.getDescription())
                .build();
    }

    public static ItemDto toDto(Item item,
                         BookingDtoForItem prev,
                         BookingDtoForItem next,
                         List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .nextBooking(next)
                .lastBooking(prev)
                .comments(comments)
                .build();
    }
}
