package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.LogExecution;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EState;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
@LogExecution
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseEntity<Booking> create(@Valid @RequestBody BookingDto bookingDto,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> update(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userid) {
        return ResponseEntity.ok(service.update(bookingId, approved, userid));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getById(@PathVariable Long bookingId,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long userid) {
        return ResponseEntity.ok(service.getById(bookingId, userid));
    }

    @GetMapping()
    public ResponseEntity<List<Booking>> getAllByBooker(@RequestParam(required = false) EState state,
                                                        @RequestHeader(name = "X-Sharer-User-Id") Long userid) {
        return ResponseEntity.ok(service.findAllByUserIdAndState(userid, state));
    }

    @GetMapping("owner")
    public ResponseEntity<List<Booking>> getAllByOwner(@RequestParam(required = false) EState state,
                                                       @RequestHeader(name = "X-Sharer-User-Id") Long owner) {
        return ResponseEntity.ok(service.findAllByOwnerIdAndState(owner, state));
    }
}
