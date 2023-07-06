package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    Long id;
    @NotBlank(message = "Comment cannot be empty")
    String text;
    String authorName;
    LocalDateTime created;
}
