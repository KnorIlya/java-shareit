package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {
    private final UserService service;

    @Nested
    class Create {
        @Test
        public void shouldCreateUser() {
            User user = TestValueBuilder.createUserWithoutId();
            user.setId(null);
            User createdUser = service.create(user);

            Assertions.assertEquals(createdUser.getId(), 1L);
        }

        @Test
        public void shouldThrowValidationExceptionWhenEmailAbsent() {
            User user = TestValueBuilder.createUserWithoutId();
            user.setEmail(null);

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> service.create(user));
            Assertions.assertTrue(thrown instanceof DataIntegrityViolationException);
        }

        @Test
        public void shouldThrowRuntimeExceptionWhenEmailDuplicate() {
            User user = TestValueBuilder.createUserWithoutId();
            Throwable thrown = Assertions.assertThrows(Exception.class, () -> {
                service.create(user);
                service.create(user);
            });
            Assertions.assertTrue(thrown instanceof RuntimeException);
        }
    }

    @Nested
    class Update {
        @Test
        public void shouldUpdateUser() {
            Map<String, Object> updates = new HashMap<>(1);
            updates.put("email", "updated@user.ru");

            service.create(TestValueBuilder.createUserWithId(1L));
            User updatedUser = service.update(1L, updates);

            Assertions.assertEquals(updatedUser.getEmail(), "updated@user.ru");
        }

        @Test
        public void shouldThrowDuplicateExceptionWithSameEmail() {
            User user = TestValueBuilder.createUserWithoutId();
            User userForUpdate = TestValueBuilder.createUserWithoutId();
            userForUpdate.setEmail("user2@user.ru");

            service.create(user);
            service.create(userForUpdate);

            Map<String, Object> updates = new HashMap<>(1);
            updates.put("email", user.getEmail());

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> service.update(2L, updates));
            Assertions.assertTrue(thrown instanceof DuplicateKeyException);
        }


    }

    @Nested
    class Get {
        @Test
        public void shouldGetUser() {
            User created = service.create(TestValueBuilder.createUserWithoutId());
            User found = service.getById(1L);

            Assertions.assertEquals(created.getEmail(), found.getEmail());
        }

        @Test
        public void shouldGetAll() {
            service.create(TestValueBuilder.createUserWithoutId());

            List<User> all = service.getAll();

            Assertions.assertFalse(all.isEmpty());
        }

        @Test
        public void shouldThrowNotFoundExceptionWhenIdFalse() {
            Throwable thrown = Assertions.assertThrows(Exception.class, () -> service.getById(2L));

            Assertions.assertTrue(thrown instanceof NotFoundException);
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() {
            service.create(TestValueBuilder.createUserWithoutId());
            service.deleteById(1L);

            List<User> all = service.getAll();

            Assertions.assertTrue(all.isEmpty());
        }
    }
}