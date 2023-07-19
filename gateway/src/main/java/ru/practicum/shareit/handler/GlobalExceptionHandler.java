package ru.practicum.shareit.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException exception) {
        return new ResponseEntity<>(new ErrorMessage(String.format("Unknown state: %s", exception.getValue())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Value
    public static class ErrorMessage {
        @JsonProperty("error")
        String messages;

        public ErrorMessage(String message) {
            this.messages = message;
        }

    }
}
