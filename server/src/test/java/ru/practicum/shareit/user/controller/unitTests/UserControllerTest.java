package ru.practicum.shareit.user.controller.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User savedUser;
    private User expectedUser;

    @BeforeEach
    void beforeEach() {
        savedUser = TestValueBuilder.createUserWithId(1L);
        expectedUser = TestValueBuilder.createUserWithId(1L);
    }

    @Test
    void getAllUsersThenResponseStatusOKWithCollectionsInBody() {
        List<User> expectedUsers = List.of(new User());

        when(userService.getAll())
                .thenReturn(expectedUsers);

        ResponseEntity<List<User>> response = userController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
    }

    @Test
    void getByIdThenResponseStatusOKWithUserInBody() {
        when(userService.getById(Mockito.anyLong()))
                .thenReturn(expectedUser);

        ResponseEntity<User> response = userController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    void createUserThenResponseStatusCreateWithUserInBody() {
        when(userService.create(savedUser)).thenReturn(expectedUser);

        ResponseEntity<User> response = userController.create(savedUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    void deleteThenResponseStatusNoContentWithEmptyBody() {
        ResponseEntity<Void> response = userController.deleteById(0L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteById(0L);
    }

    @Test
    void updateThenResponseStatusOKWithUserInBody() {
        when(userService.update(0L, savedUser)).thenReturn(expectedUser);

        ResponseEntity<User> response = userController.update(0L, savedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }
}
