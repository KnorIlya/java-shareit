package ru.practicum.shareit.request.unitTests;

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
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;


    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private static SimpleItemRequestDto simpleDto;
    private static ItemRequestDtoForClient dtoForClient;

    @BeforeAll
    static void before() {
        simpleDto = TestValueBuilder.createSimpleItemRequestDto();
        List<ItemShortDto> items = List.of(TestValueBuilder.createAvailableItemShortDto("Item", "Desc"));
        dtoForClient = TestValueBuilder.createItemRequestDtoForClient(items);
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
            when(service.create(Mockito.anyLong(), Mockito.any()))
                    .thenReturn(simpleDto);

            String content = mvc.perform(post("/requests")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(simpleDto.getDescription())))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(simpleDto), content);
        }
    }

    @Nested
    class GetAllOwnerRequest {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            List<ItemRequestDtoForClient> requests = List.of(dtoForClient);

            when(service.getAllByOwnerId(Mockito.anyLong()))
                    .thenReturn(requests);

            String content = mvc.perform(get("/requests")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(requests), content);
        }
    }

    @Nested
    class GetAll {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            List<ItemRequestDtoForClient> requests = List.of(dtoForClient);

            when(service.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                    .thenReturn(requests);

            String content = mvc.perform(get("/requests/all")
                            .header("X-Sharer-User-Id", 1)
                            .param("from", "0")
                            .param("size", "1")
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(requests), content);
        }
    }

    @Nested
    class GetById {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.getById(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(dtoForClient);

            String content = mvc.perform(get("/requests/1")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(dtoForClient), content);
        }
    }
}
