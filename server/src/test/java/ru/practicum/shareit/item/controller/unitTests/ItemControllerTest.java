package ru.practicum.shareit.item.controller.unitTests;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService service;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private static ItemShortDto shortDto;

    private static CommentDto commentDto;

    @BeforeAll
    static void before() {
        shortDto = TestValueBuilder.createAvailableItemShortDto("Item", "Item desc");
        commentDto = TestValueBuilder.createCommentDto();
    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Nested
    class Comment {
        @SneakyThrows
        @Test
        void should200_WhenInputIsValid() {
            when(service.addComment(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(commentDto);

            String content = mvc.perform(post("/items/1/comment")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(commentDto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(commentDto), content);
        }
    }

    @Nested
    class Create {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            when(service.create(Mockito.anyLong(), Mockito.any()))
                    .thenReturn(shortDto);

            String content = mvc.perform(post("/items")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(shortDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(shortDto), content);
        }
    }

    @Nested
    class Update {
        @SneakyThrows
        @Test
        void should200_WhenInputIsValid() {
            when(service.update(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                    .thenReturn(shortDto);

            String content = mvc.perform(patch("/items/1")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(shortDto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(shortDto), content);
        }
    }

    @Nested
    class GetById {
        @SneakyThrows
        @Test
        void should201_WhenInputIsValid() {
            ItemDto dto = TestValueBuilder.createItemDto();

            when(service.getById(Mockito.anyLong(), Mockito.anyLong()))
                    .thenReturn(dto);

            String content = mvc.perform(get("/items/1")
                            .header("X-Sharer-User-Id", 1)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Assertions.assertEquals(mapper.writeValueAsString(dto), content);
        }
    }

//    @Nested
//    class GetByText {
//        @SneakyThrows
//        @Test
//        void should201_WhenInputIsValid() {
//            mvc.perform(get("/items?text = test")
//                            .header("X-Sharer-User-Id", 1)
//                            .contentType(APPLICATION_JSON)
//                            .content(mapper.writeValueAsString(shortDto)))
//                    .andExpect(status().isOk());
//        }
//    }

//    @Nested
//    class GetAllByUserId {
//        @SneakyThrows
//        @Test
//        void should201_WhenInputIsValid() {
//            ItemDto dto = TestValueBuilder.createItemDto();
//
//            when(service.getAllByUserId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
//                    .thenReturn(List.of(dto));
//
//            mvc.perform(get("/items")
//                            .header("X-Sharer-User-Id", 1)
//                            .contentType(APPLICATION_JSON)
//                            .content(mapper.writeValueAsString(shortDto)))
//                    .andExpect(status().isOk());
//        }
//    }
}
