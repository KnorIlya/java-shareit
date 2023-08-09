package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.PermissionException;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldThrowPermissionException() {
        PermissionException exception = new PermissionException("exception");
        ResponseEntity<GlobalExceptionHandler.ErrorInfo> response = handler.permissionException(exception);
        Assertions.assertEquals(response.getStatusCode().value(), 403);
    }

    @Test
    void shouldThrowBadRequestException() {
        BadRequestException exception = new BadRequestException("exception");
        ResponseEntity<GlobalExceptionHandler.ErrorInfo> response = handler.badRequestExceptionHandler(exception);
        Assertions.assertEquals(response.getStatusCode().value(), 400);
    }

    @Test
    void shouldThrowConversionFailedException() {
        RuntimeException exception = new RuntimeException();
        ResponseEntity<GlobalExceptionHandler.ErrorInfo> response = handler.conversionFailedExceptionHandler(exception);
        Assertions.assertEquals(response.getStatusCode().value(), 400);
    }
}
