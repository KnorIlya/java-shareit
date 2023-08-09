package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.LogExecution;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@LogExecution
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto dto) {
        return userClient.create(dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@Positive @PathVariable Long userId) {
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Positive @PathVariable Long userId,
                                         @RequestBody UserDto dto) {
        return userClient.update(userId, dto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Positive @PathVariable Long userId) {
        return userClient.deleteById(userId);
    }
}
