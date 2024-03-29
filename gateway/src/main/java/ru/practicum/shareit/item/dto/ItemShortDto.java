package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemShortDto {
    Long id;

    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    Boolean available;

    Long requestId;
}
