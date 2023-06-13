package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(user));
    }

    @GetMapping("{userid}")
    public ResponseEntity<User> getById(@PathVariable Long userid) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getById(userid));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAll());
    }

    @PatchMapping(value = "{userid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@PathVariable Long userid, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(service.update(userid, updates));
    }

    @DeleteMapping("{userid}")
    public ResponseEntity<Void> deleteById(@PathVariable Long userid) {
        service.deleteById(userid);
        return ResponseEntity.noContent().build();
    }
}
