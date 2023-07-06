package ru.practicum.shareit.booking.unitTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    static BookingDto dto;
    static Booking booking;

    @BeforeAll
    static void before() {
        dto = TestValueBuilder.createBookingDto();
        booking = TestValueBuilder.createBookingWithId();
    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Nested
    class Create {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.save(Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            String content = mvc.perform(post("/bookings")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(booking), content);
        }
    }

    @Nested
    class Update {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.update(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
                    .thenReturn(booking);

            String content = mvc.perform(patch("/bookings/1")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .param("approved", "true"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(booking), content);
        }
    }

    @Nested
    class Get {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.getById(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(booking);

            String content = mvc.perform(get("/bookings/1")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(booking), content);
        }
    }

    @Nested
    class GetAll {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.findAll(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                    .thenReturn(List.of(booking));

            mvc.perform(get("/bookings")
                            .header("X-Sharer-User-Id", 1)
                            .param("state", "ALL")
                            .param("from", "0")
                            .param("size", "1")
                            .param("owner", "true")
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }
    }

    @Nested
    class GetAllByOwner {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.findAll(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                    .thenReturn(List.of(booking));

            mvc.perform(get("/bookings/owner")
                            .header("X-Sharer-User-Id", 1)
                            .param("state", "ALL")
                            .param("from", "0")
                            .param("size", "1")
                            .param("owner", "false")
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }
    }
}
