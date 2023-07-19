package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoForItem nextBooking;
    BookingDtoForItem lastBooking;
    List<CommentDto> comments;
}
