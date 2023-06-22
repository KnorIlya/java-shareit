package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceTest {
    private final UserService userService;
    private final ItemService itemService;

    @BeforeEach
    void before() {
        userService.create(TestValueBuilder.createUserWithoutId());
    }

    @Nested
    class Create {
        @Test
        public void shouldSaveItem() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");

            ItemDto itemDto = itemService.create(item);

            Assertions.assertEquals(itemDto.getId(), 1L);
        }

        @Test
        public void shouldThrowNotFoundExceptionWhenUserIdAbsent() {
            Item item = TestValueBuilder.createAvailableItem(null, "Hammer", "Hammer");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(item));

            Assertions.assertTrue(thrown instanceof NotFoundException);
        }

        @Test
        public void shouldThrowNotFoundExceptionWhenUserIdNotExist() {
            Item item = TestValueBuilder.createAvailableItem(9999L, "Hammer", "Hammer");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(item));

            Assertions.assertTrue(thrown instanceof NotFoundException);
        }

        @Test
        public void shouldThrowViolationExceptionWhenNameAbsent() {
            Item item = TestValueBuilder.createAvailableItem(1L, null, "Hammer");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(item));

            Assertions.assertTrue(thrown instanceof DataIntegrityViolationException);
        }

        @Test
        public void shouldThrowViolationExceptionWhenDescriptionAbsent() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", null);

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(item));

            Assertions.assertTrue(thrown instanceof DataIntegrityViolationException);
        }

        @Test
        public void shouldThrowViolationExceptionWhenAvailableAbsent() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            item.setAvailable(null);

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(item));

            Assertions.assertTrue(thrown instanceof DataIntegrityViolationException);
        }
    }

    @Nested
    class Update {

        @Test
        public void updateItem() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            itemService.create(item);
            Map<String, Object> updated = new HashMap<>(1);
            updated.put("name", "Keyboard");

            ItemDto update = itemService.update(1L, updated, 1L);

            Assertions.assertEquals(itemService.getById(1L).getName(), update.getName());
        }

        @Test
        public void shouldThrowPermissionExceptionWhenUserIdAbsent() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            itemService.create(item);
            Map<String, Object> updated = new HashMap<>(1);
            updated.put("name", "Keyboard");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.update(1L, updated, null));

            Assertions.assertTrue(thrown instanceof PermissionException);
        }

        @Test
        public void shouldThrowPermissionExceptionWhenNotOwnerUserId() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            itemService.create(item);
            Map<String, Object> updated = new HashMap<>(1);
            updated.put("name", "Keyboard");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.update(1L, updated, 99L));

            Assertions.assertTrue(thrown instanceof PermissionException);
        }
    }

    @Nested
    class Get {

        @Test
        public void shouldGetItem() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            itemService.create(item);

            Assertions.assertEquals(itemService.getById(1L).getId(), 1L);
        }

        @Test
        public void shouldGetTwoItemsByUser() {
            Item item1 = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            Item item2 = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");

            itemService.create(item1);
            itemService.create(item2);

            Assertions.assertEquals(itemService.getAllByUserId(1L).size(), 2);
        }

        @Test
        public void shouldGetOneItemByName() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Metal");
            itemService.create(item);

            List<ItemDto> items = itemService.getItemsByText("HaM");

            Assertions.assertEquals(items.size(), 1);
        }

        @Test
        public void shouldGetOneItemByDescription() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Metal");
            itemService.create(item);

            List<ItemDto> items = itemService.getItemsByText("Met");

            Assertions.assertEquals(items.size(), 1);
        }

        @Test
        public void shouldGetTwoItemsByDescriptionAndName() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            itemService.create(item);
            Item item1 = TestValueBuilder.createAvailableItem(1L, "Lamp", "Metal");
            itemService.create(item1);

            List<ItemDto> items = itemService.getItemsByText("Me");

            Assertions.assertEquals(items.size(), 2);
        }

        @Test
        public void shouldReturnEmptyWhenTextBlank() {
            Item item = TestValueBuilder.createAvailableItem(1L, "Hammer", "Hammer");
            itemService.create(item);
            Item item1 = TestValueBuilder.createAvailableItem(1L, "Lamp", "Metal");
            itemService.create(item1);

            List<ItemDto> items = itemService.getItemsByText(" ");

            Assertions.assertTrue(items.isEmpty());
        }
    }
}