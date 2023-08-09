package ru.practicum.shareit.item.controller.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private static User user;
    private static ItemShortDto itemShortDto;

    @BeforeAll
    static void beforeAll() {
        user = TestValueBuilder.createUserWithId(1L);
        itemShortDto = TestValueBuilder.createAvailableItemShortDto("item", "item");
        itemShortDto.setId(1L);
    }

    @Nested
    class Create {
        @SneakyThrows
        @Test
        void shouldCreateItemWhenInputIsValid() {
            when(itemService.create(user.getId(), itemShortDto)).thenReturn(itemShortDto);

            String result = mockMvc.perform(post("/items")
                            .header("X-Sharer-User-Id", user.getId().toString())
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(itemShortDto)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertEquals(objectMapper.writeValueAsString(itemShortDto), result);
        }

//        @SneakyThrows
//        @Test
//        void shouldThrowBadRequestExceptionWhenInputIsNotValid() {
//            ItemShortDto newDto = TestValueBuilder.createAvailableItemShortDto(null, null);
//
//            mockMvc.perform(post("/items")
//                            .header("X-Sharer-User-Id", user.getId().toString())
//                            .contentType("application/json")
//                            .content(objectMapper.writeValueAsString(newDto)))
//                    .andExpect(status().isBadRequest());
//
//            verify(itemService, never()).create(user.getId(), newDto);
//        }

        @SneakyThrows
        @Test
        void shouldThrowInternalServerErrorWhenHeaderAbsent() {

            mockMvc.perform(post("/items")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(itemShortDto)))
                    .andExpect(status().isInternalServerError());

            verify(itemService, never()).create(user.getId(), itemShortDto);
        }
    }

    @Nested
    class Update {
        @SneakyThrows
        @Test
        void shouldUpdateItemWhenInputIsValid() {

            when(itemService.update(Mockito.anyLong(), Mockito.any(), Mockito.anyLong())).thenReturn(itemShortDto);

            String result = mockMvc.perform(patch("/items/{itemId}", itemShortDto.getId().toString())
                            .header("X-Sharer-User-Id", user.getId().toString())
                            .contentType(MediaType.valueOf("application/json"))
                            .content(objectMapper.writeValueAsString(itemShortDto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertEquals(objectMapper.writeValueAsString(itemShortDto), result);
        }


        @SneakyThrows
        @Test
        void shouldThrowNotFoundStatusIfItemAbsent() {

            when(itemService.update(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                    .thenThrow(NotFoundException.class);

            mockMvc.perform(patch("/items/{itemId}", 1L)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(itemShortDto)))
                    .andExpect(status().isNotFound());
        }


        @SneakyThrows
        @Test
        void shouldThrowNotFoundExceptionWhenUserAbsent() {

            when(itemService.update(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                    .thenThrow(NotFoundException.class);

            mockMvc.perform(patch("/items/{itemId}", 1L)
                            .header("X-Sharer-User-Id", 999L)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(itemShortDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetByText {
//        @SneakyThrows
//        @Test
//        void shouldReturnEmptyListWhenInputValid() {
//            List<ItemShortDto> dtos = new ArrayList<>();
//
//            when(itemService.getItemsByText(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt()))
//                    .thenReturn(dtos);
//
//            mockMvc.perform(get("/items/search", 1L)
//                            .header("X-Sharer-User-Id", 1L)
//                            .param("text", " ")
//                            .contentType("application/json")
//                            .content(objectMapper.writeValueAsString(dtos)))
//                    .andExpect(status().isOk());
//        }
    }
}
