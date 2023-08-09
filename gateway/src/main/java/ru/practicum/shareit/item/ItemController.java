package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.LogExecution;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@LogExecution
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid
                                         @RequestBody ItemShortDto dto,
                                         @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemClient.create(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @Valid
                                                @RequestBody CommentDto dto,
                                                @Positive
                                                @PathVariable Long itemId) {
        return itemClient.createComment(dto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@Positive
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @Positive
                                          @PathVariable Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @RequestParam String text,
                                            @PositiveOrZero
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @Positive
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (text.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        return itemClient.getByText(text, userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero
                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @Positive
                                                 @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemClient.getAllByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId, @RequestBody Object item,
                                         @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemClient.update(itemId, userId, item);
    }
}
