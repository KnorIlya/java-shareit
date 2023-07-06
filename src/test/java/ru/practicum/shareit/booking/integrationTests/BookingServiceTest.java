package ru.practicum.shareit.booking.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceTest {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    static User firstUser;
    static User secondUser;
    static Item item;
    static BookingDto bookingDto;

    @BeforeAll
    static void before() {
        firstUser = TestValueBuilder.createUserWithoutId();

        secondUser = TestValueBuilder.createUserWithoutId();
        secondUser.setEmail("secondUser@mail.ru");

        item = TestValueBuilder.createItemWithId();
        item.setId(null);

        bookingDto = TestValueBuilder.createBookingDto();
    }

    @Nested
    class Create {
        @Test
        void shouldSaveBooking() {
            User user1 = userService.create(firstUser);
            User user2 = userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Booking save = bookingService.save(bookingDto, user2.getId());

            Assertions.assertNotNull(save.getId());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenUserAbsent() {
            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto, 99L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenUserEqualOwner() {
            User user1 = userService.create(firstUser);
            userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto,
                    user1.getId()));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenTimeIsNotValid() {
            User user1 = userService.create(firstUser);
            userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());
            bookingDto.setEnd(LocalDateTime.now().minusDays(20));
            bookingDto.setStart(LocalDateTime.now().minusDays(20));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto,
                    user1.getId()));

            Assertions.assertTrue(throwable instanceof BadRequestException);
        }
    }

    @Nested
    class Update {
        @Test
        void shouldApproveBooking() {
            User user1 = userService.create(firstUser);
            User user2 = userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Booking save = bookingService.save(bookingDto, user2.getId());

            Booking update = bookingService.update(save.getId(), true, user1.getId());

            Assertions.assertEquals(EStatus.APPROVED, update.getStatus());
        }

        @Test
        void shouldRejectedBooking() {
            User user1 = userService.create(firstUser);
            User user2 = userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Booking save = bookingService.save(bookingDto, user2.getId());

            Booking update = bookingService.update(save.getId(), false, user1.getId());

            Assertions.assertEquals(EStatus.REJECTED, update.getStatus());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenNoItemOwner() {
            User user1 = userService.create(firstUser);
            User user2 = userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Booking save = bookingService.save(bookingDto, user2.getId());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.update(save.getId(), false, user2.getId()));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenBookingAbsent() {
            User user1 = userService.create(firstUser);
            User user2 = userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.update(999L, false, user2.getId()));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class Get {
        @Test
        void shouldGetBookingById() {
            User user1 = userService.create(firstUser);
            User user2 = userService.create(secondUser);
            item.setUser(user1);
            Item createdItem = itemRepository.save(item);
            bookingDto.setItemId(createdItem.getId());

            Booking save = bookingService.save(bookingDto, user2.getId());

            Booking update = bookingService.getById(save.getId(), user1.getId());

            Assertions.assertNotNull(update);
        }
    }
}
