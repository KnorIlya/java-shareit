package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody Item item,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        item.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(item));
    }

    @GetMapping("{itemid}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long itemid) {
        return ResponseEntity.ok(service.getById(itemid));
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDto>> getItemsByText(@RequestParam String text) {
        if (text.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        text = text.toLowerCase();
        return ResponseEntity.ok(service.getItemsByText(text));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long id) {
        return ResponseEntity.ok(service.getAllByUserId(id));
    }

    @PatchMapping(value = "{itemid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> update(@PathVariable Long itemid, @RequestBody Map<String, Object> updates,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.update(itemid, updates, userId));
    }
}
