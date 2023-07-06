package ru.practicum.shareit.user.controller.integrationTests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    private Long userId;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
    }

    @Nested
    class GetAll {
        @SneakyThrows
        @Test
        void shouldGetAllUsersWhenUrlIsValid() {
            mvc.perform(get("/users"))
                    .andExpect(status().isOk());

            verify(userService).getAll();
        }
    }

    @Nested
    class GetById {
        @SneakyThrows
        @Test
        void shouldInvokeGetByIdMethodWhenUrlIsValid() {
            mvc.perform(get("/users/{userId}", userId))
                    .andExpect(status().isOk());

            verify(userService).getById(userId);
        }

        @SneakyThrows
        @Test
        void shouldThrowNotFoundExceptionWhenUserIsAbsent() {
            when(userService.getById(userId)).thenThrow(NotFoundException.class);
            mvc.perform(get("/users/{userId}", userId))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class DeleteById {
        @SneakyThrows
        @Test
        void shouldInvokeMethodDeleteByIdAndReturnStatusNoContent() {
            mvc.perform(delete("/users/{userId}", userId))
                    .andExpect(status().isNoContent());
            verify(userService).deleteById(userId);
        }
    }
}
