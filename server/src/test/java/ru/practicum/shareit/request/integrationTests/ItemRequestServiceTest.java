package ru.practicum.shareit.request.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    static User user;
    static SimpleItemRequestDto itemRequestDto;

    @BeforeAll
    static void beforeAll() {
        user = TestValueBuilder.createUserWithoutId();
        itemRequestDto = TestValueBuilder.createSimpleItemRequestDto();
        itemRequestDto.setId(null);
    }

    @Nested
    class Create {
        @Test
        void shouldCreateItemRequest() {
            Long userId = userService.create(user).getId();
            ItemRequestDto dto = ItemRequestDto.builder()
                    .description(itemRequestDto.getDescription())
                    .build();

            SimpleItemRequestDto simpleItemRequestDto = itemRequestService.create(userId, dto);

            Assertions.assertNotNull(simpleItemRequestDto.getId());
        }
    }

    @Nested
    class GetAllByOwnerId {
        @Test
        void shouldReturnOwnersRequests() {
            Long userId = userService.create(user).getId();

            ItemRequestDto dto = ItemRequestDto.builder()
                    .description(itemRequestDto.getDescription())
                    .build();
            itemRequestService.create(userId, dto);


            List<ItemRequestDtoForClient> allByOwnerId = itemRequestService.getAllByOwnerId(userId);

            Assertions.assertEquals(1, allByOwnerId.size());
        }
    }

    @Nested
    class GetAll {
        @Test
        void shouldReturnOwnersRequests() {
            Long userId = userService.create(user).getId();

            ItemRequestDto dto = ItemRequestDto.builder()
                    .description(itemRequestDto.getDescription())
                    .build();
            itemRequestService.create(userId, dto);


            List<ItemRequestDtoForClient> allByOwnerId = itemRequestService.getAll(userId + 1, 0, 1);

            Assertions.assertEquals(1, allByOwnerId.size());
        }

        @Test
        void shouldReturnOwnersRequestsWhenPageIsEmpty() {
            Long userId = userService.create(user).getId();

            ItemRequestDto dto = ItemRequestDto.builder()
                    .description(itemRequestDto.getDescription())
                    .build();
            itemRequestService.create(userId, dto);
            itemRequestService.create(userId, dto);
            itemRequestService.create(userId, dto);


            List<ItemRequestDtoForClient> allByOwnerId = itemRequestService.getAll(userId + 1, 99, 1);

            Assertions.assertEquals(1, allByOwnerId.size());
        }
    }
}
