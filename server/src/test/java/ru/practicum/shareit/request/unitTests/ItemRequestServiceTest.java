package ru.practicum.shareit.request.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    static User user;
    static ItemRequest itemRequest;
    static ItemRequestDto itemRequestDto;

    @BeforeAll
    static void before() {
        user = TestValueBuilder.createUserWithId(1L);
        itemRequest = TestValueBuilder.createItemRequest(user);
        itemRequestDto = ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .build();
    }

    @Nested
    class Create {
        @Test
        void shouldReturnItemRequestDtoWhenInputIsValid() {
            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRequestRepository.save(Mockito.any()))
                    .thenReturn(itemRequest);

            SimpleItemRequestDto simpleItemRequestDto = itemRequestService.create(1L, itemRequestDto);

            Assertions.assertEquals(1L, simpleItemRequestDto.getId());
            Assertions.assertEquals(itemRequest.getDescription(), simpleItemRequestDto.getDescription());
        }
    }

    @Nested
    class GetAllByOwnerId {
        @Test
        void shouldReturnListItemRequestDtoWhenInputIsValid() {
            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(Mockito.anyLong()))
                    .thenReturn(List.of(itemRequest));

            List<ItemRequestDtoForClient> allByOwnerId = itemRequestService.getAllByOwnerId(1L);

            Assertions.assertFalse(allByOwnerId.isEmpty());
        }
    }

    @Nested
    class GetById {
        @Test
        void shouldGetItemRequestWhenInputIsValid() {
            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRequestRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(itemRequest));

            ItemRequestDtoForClient request = itemRequestService.getById(1L, 1L);

            Assertions.assertEquals(itemRequest.getId(), request.getId());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenItemRequestNotFound() {
            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRequestRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> itemRequestService.getById(1L, 1L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class GetAll {
        @Test
        void shouldReturnNoEmptyListWhenInputIsValid() {
            Page<ItemRequest> content = new PageImpl<>(List.of(itemRequest));

            when(itemRequestRepository.findAllAnotherRequest(Mockito.anyLong(), Mockito.any()))
                    .thenReturn(content);

            List<Item> items = Collections.emptyList();
            when(itemRepository.findAllByRequestId(Mockito.anyLong()))
                    .thenReturn(items);

            List<ItemRequestDtoForClient> itemRequests = itemRequestService.getAll(1L, 0, 1);

            Assertions.assertFalse(itemRequests.isEmpty());
        }
    }
}
