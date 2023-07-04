package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotation.LogExecution;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@LogExecution
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
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

    @PatchMapping(value = "{userid}")
    public ResponseEntity<User> update(@PathVariable Long userid, @RequestBody User user) {
        return ResponseEntity.ok(service.update(userid, user));
    }


    @DeleteMapping("{userid}")
    public ResponseEntity<Void> deleteById(@PathVariable Long userid) {
        service.deleteById(userid);
        return ResponseEntity.noContent().build();
    }
}
