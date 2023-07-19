package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.LogExecution;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@LogExecution
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto dto) {
        return requestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@Positive @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return requestClient.getAllByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@Positive @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @Positive @RequestParam(required = false, defaultValue = "1") Integer size) {
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@Positive @PathVariable Long requestId,
                                          @Positive @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return requestClient.getById(requestId, userId);
    }
}
