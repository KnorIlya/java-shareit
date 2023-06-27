package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
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
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");

            ItemShortDto itemShortDto = itemService.create(1L, item);

            Assertions.assertEquals(itemShortDto.getId(), 1L);
        }

        @Test
        public void shouldThrowInvalidDataAccessApiUsageExceptionWhenUserIdAbsent() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(null, item));

            Assertions.assertTrue(thrown instanceof InvalidDataAccessApiUsageException);
        }

        @Test
        public void shouldThrowNotFoundExceptionWhenUserIdNotExist() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(999L, item));

            Assertions.assertTrue(thrown instanceof NotFoundException);
        }

        @Test
        public void shouldThrowViolationExceptionWhenNameAbsent() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser(null, "Hammer");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(1L, item));

            Assertions.assertTrue(thrown instanceof ConstraintViolationException);
        }

        @Test
        public void shouldThrowViolationExceptionWhenDescriptionAbsent() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", null);

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(1L, item));

            Assertions.assertTrue(thrown instanceof ConstraintViolationException);
        }

        @Test
        public void shouldThrowViolationExceptionWhenAvailableAbsent() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            item.setAvailable(null);

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.create(1L, item));

            Assertions.assertTrue(thrown instanceof ConstraintViolationException);
        }
    }

    @Nested
    class Update {

        @Test
        public void updateItem() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            itemService.create(1L, item);
            item.setName("Keyboard");

            Item update = itemService.update(1L, item, 1L);

            Assertions.assertEquals(itemService.getById(1L,1L).getName(), update.getName());
        }

        @Test
        public void shouldThrowPermissionExceptionWhenUserIdAbsent() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            itemService.create(1L, item);
            item.setName("Keyboard");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.update(1L, item, null));

            Assertions.assertTrue(thrown instanceof PermissionException);
        }

        @Test
        public void shouldThrowPermissionExceptionWhenNotOwnerUserId() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            itemService.create(1L, item);
            item.setName("Keyboard");

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.update(1L, item, 99L));

            Assertions.assertTrue(thrown instanceof PermissionException);
        }
    }

    @Nested
    class Get {

        @Test
        public void shouldGetItem() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            itemService.create(1L, item);

            Assertions.assertEquals(itemService.getById(1L, 1L).getId(), 1L);
        }

        @Test
        public void shouldGetTwoItemsByUser() {
            Item item1 = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            Item item2 = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");

            itemService.create(1L, item1);
            itemService.create(1L, item2);

            Assertions.assertEquals(itemService.getAllByUserId(1L).size(), 2);
        }

        @Test
        public void shouldGetOneItemByName() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Metal");
            itemService.create(1L, item);

            List<Item> items = itemService.getItemsByText("HaM");

            Assertions.assertEquals(items.size(), 1);
        }

        @Test
        public void shouldGetOneItemByDescription() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Metal");
            itemService.create(1L, item);

            List<Item> items = itemService.getItemsByText("Met");

            Assertions.assertEquals(items.size(), 1);
        }

        @Test
        public void shouldGetTwoItemsByDescriptionAndName() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            itemService.create(1L, item);
            Item item1 = TestValueBuilder.createAvailableItemWithoutUser("Lamp", "Metal");
            itemService.create(1L, item1);

            List<Item> items = itemService.getItemsByText("Me");

            Assertions.assertEquals(items.size(), 2);
        }

        @Test
        public void shouldReturnEmptyWhenTextBlank() {
            Item item = TestValueBuilder.createAvailableItemWithoutUser("Hammer", "Hammer");
            itemService.create(1L, item);
            Item item1 = TestValueBuilder.createAvailableItemWithoutUser("Lamp", "Metal");
            itemService.create(1L, item1);

            List<Item> items = itemService.getItemsByText(" ");

            Assertions.assertTrue(items.isEmpty());
        }
    }
}