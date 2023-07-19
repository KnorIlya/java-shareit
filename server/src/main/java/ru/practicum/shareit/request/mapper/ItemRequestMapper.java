package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto dto, User requester) {
        return ItemRequest.builder()
                .user(requester)
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static SimpleItemRequestDto toSimpleDto(ItemRequest itemRequest) {
        return SimpleItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDtoForClient toDto(ItemRequest itemRequest, List<ItemShortDto> itemsDto) {
        return ItemRequestDtoForClient.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsDto)
                .build();
    }
}
