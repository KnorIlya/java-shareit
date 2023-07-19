package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.LogExecution;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@LogExecution
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemShortDto> create(@Valid @RequestBody ItemShortDto dto,
                                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, dto));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDto comment,
                                             @PathVariable Long itemId) {
        return ResponseEntity.ok().body(service.addComment(comment, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        return ResponseEntity.ok(service.getById(itemId, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemShortDto>> getItemsByText(@RequestParam String text,
                                                             @Min(0) @RequestParam(required = false, defaultValue = "0") Integer from,
                                                             @Min(1) @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (text.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(service.getItemsByText(text, from, size));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long id,
                                                        @Min(0) @RequestParam(required = false, defaultValue = "0") Integer from,
                                                        @Min(1) @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(service.getAllByUserId(id, from, size));
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<ItemShortDto> update(@PathVariable Long itemId, @RequestBody Item item,
                                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.update(itemId, item, userId));
    }
}
