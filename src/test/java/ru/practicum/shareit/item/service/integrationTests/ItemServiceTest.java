package ru.practicum.shareit.item.service.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceTest {
    private final UserService userService;
    private final ItemService itemService;

    static User user;
    static ItemShortDto itemShortDto;

    @BeforeAll
    static void beforeAll() {
        user = TestValueBuilder.createUserWithoutId();
        itemShortDto = TestValueBuilder.createAvailableItemShortDto("Item", "Item Desc");

    }

    @Nested
    class Create {
        @Test
        void shouldCreateNewItemWithIdWhenInputIsValid() {
            Long userId = userService.create(user).getId();
            ItemShortDto dto = itemService.create(userId, itemShortDto);

            Assertions.assertNotNull(dto.getId());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenRequestIdIsNotExist() {
            Long userId = userService.create(user).getId();

            ItemShortDto dto = TestValueBuilder.createAvailableItemShortDto("item", "item");
            dto.setRequestId(9999L);

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> itemService.create(userId, dto));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenUserAbsent() {
            ItemShortDto dto = TestValueBuilder.createAvailableItemShortDto("item", "item");
            dto.setRequestId(9999L);

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> itemService.create(999L, dto));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class GetAll {
        @Test
        void shouldReturnNotEmptyList() {
            Long userId = userService.create(user).getId();
            itemService.create(userId, itemShortDto);

            List<ItemDto> content = itemService.getAllByUserId(userId, 0, 1);

            Assertions.assertEquals(1, content.size());
        }

        @Test
        void shouldReturnNotEmptyListWhenPageIsNotExist() {
            Long userId = userService.create(user).getId();
            itemService.create(userId, itemShortDto);

            List<ItemDto> content = itemService.getAllByUserId(userId, 5, 10);

            Assertions.assertEquals(1, content.size());
        }

        @Test
        void shouldReturnEmptyListWhenItemNotExist() {
            Long userId = userService.create(user).getId();

            List<ItemDto> content = itemService.getAllByUserId(userId, 0, 1);

            Assertions.assertTrue(content.isEmpty());
        }
    }

    @Nested
    class GetById {
        @Test
        void shouldReturnItemWhenInputIsValid() {
            Long userId = userService.create(user).getId();
            ItemShortDto dto = itemService.create(userId,
                    itemShortDto);

            ItemDto itemDto = itemService.getById(dto.getId(), userId);

            Assertions.assertEquals(dto.getId(), itemDto.getId());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenItemNotExist() {
            Long userId = userService.create(user).getId();

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> itemService.getById(99L, userId));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class Update {
        @Test
        void shouldThrowPermissionException() {
            User newUser = TestValueBuilder.createUserWithoutId();
            newUser.setEmail("newUser@mail.ru");
            Long newUserId = userService.create(newUser).getId();

            Long userId = userService.create(user).getId();

            ItemShortDto dto = itemService.create(userId,
                    itemShortDto);

            Item newItem = Item.builder()
                    .name("Update")
                    .build();

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> itemService.update(dto.getId(),
                    newItem, newUserId));

            Assertions.assertTrue(throwable instanceof PermissionException);
        }
    }

}