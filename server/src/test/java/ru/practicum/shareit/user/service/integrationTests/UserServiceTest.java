package ru.practicum.shareit.user.service.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceTest {
    private final UserService service;
    static User user;

    @BeforeAll
    static void before() {
        user = TestValueBuilder.createUserWithoutId();
    }

    @Nested
    class Created {

        @Rollback
        @Test
        void shouldSaveUser() {
            User created = service.create(user);

            Assertions.assertNotNull(created.getId());
        }

        @Rollback
        @Test
        void shouldThrowInvalidDataAccessApiUsageExceptionWhenUserAbsent() {
            Throwable thrown = Assertions.assertThrows(Exception.class, () -> service.create(null));

            assertThat(thrown.getClass(), typeCompatibleWith(InvalidDataAccessApiUsageException.class));
        }

        @Rollback
        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.getById(99L));

            Assertions.assertNotNull(exception);
        }
    }

    @Nested
    class Update {
        @Rollback
        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.update(99L, user));

            Assertions.assertNotNull(exception);
        }
    }

    @Nested
    class Get {

        @Rollback
        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.getById(99L));

            Assertions.assertNotNull(exception);
        }
    }

    @Nested
    class GetAll {
        @Rollback
        @Test
        void shouldReturnEmptyList() {
            List<User> all = service.getAll();

            Assertions.assertTrue(all.isEmpty());
        }

        @Rollback
        @Test
        void shouldReturnListSizeOne() {
            service.create(user);

            List<User> all = service.getAll();

            Assertions.assertEquals(1, all.size());
        }
    }

    @Nested
    class Delete {
        @Rollback
        @Test
        void shouldDeleteById() {
            service.create(user);

            service.deleteById(1L);

            Assertions.assertTrue(service.getAll().isEmpty());
        }
    }
}
