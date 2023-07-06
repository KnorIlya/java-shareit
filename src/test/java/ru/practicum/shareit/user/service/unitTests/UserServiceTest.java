package ru.practicum.shareit.user.service.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository repository;
    @InjectMocks
    UserService service;

    @Nested
    class Update {
        @Test
        void shouldUpdateUserWithAllFields() {
            User user = TestValueBuilder.createUserWithId(1L);
            User userForUpdate = TestValueBuilder.createCustomUserWithoutId("updated@generalUser.ru", "updated");
            userForUpdate.setId(1L);

            when(repository.findById(1L))
                    .thenReturn(Optional.of(user));
            when(repository.save(user))
                    .thenReturn(userForUpdate);

            service.update(1L, userForUpdate);

            Assertions.assertEquals(user.getEmail(), "updated@generalUser.ru");
            Assertions.assertEquals(user.getName(), "updated");
        }

        @Test
        void shouldUpdateUserWithoutEmail() {
            User user = TestValueBuilder.createUserWithId(1L);
            User userForUpdate = TestValueBuilder.createCustomUserWithoutId(null, "updated");
            userForUpdate.setId(1L);

            when(repository.findById(1L))
                    .thenReturn(Optional.of(user));

            when(repository.save(user))
                    .thenReturn(userForUpdate);

            service.update(1L, userForUpdate);

            Assertions.assertEquals(user.getEmail(), "user@user.com");
            Assertions.assertEquals(user.getName(), "updated");
        }

        @Test
        void shouldThrowNotFoundExceptionWhenUserAbsent() {
            when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.getById(1L));

            Assertions.assertNotNull(exception);
        }
    }
}