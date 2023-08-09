package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping()
    public ResponseEntity<SimpleItemRequestDto> create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                       @RequestBody ItemRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoForClient>> getAllOwnerRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getAllByOwnerId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoForClient>> getAll(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                                @RequestParam Integer from,
                                                                @RequestParam Integer size) {
        return ResponseEntity.ok(service.getAll(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoForClient> getById(@PathVariable Long requestId,
                                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getById(requestId, userId));
    }
}
