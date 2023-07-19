package ru.practicum.shareit.booking.unitTests;

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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EState;
import ru.practicum.shareit.booking.model.EStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    BookingService bookingService;

    private static Item item;
    private static User user;
    private static Booking booking;
    private static BookingDto bookingDto;

    @BeforeAll
    static void before() {
        item = TestValueBuilder.createItemWithId();
        user = TestValueBuilder.createUserWithId(1L);
        booking = TestValueBuilder.createBookingWithId();
        bookingDto = TestValueBuilder.createBookingDto();
    }

    @Nested
    class Create {
        @Test
        void shouldCreateBooking() {
            User newUser = TestValueBuilder.createUserWithId(2L);
            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(newUser);

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(item));

            when(bookingRepository.save(Mockito.any()))
                    .thenReturn(booking);

            Booking foundBooking = bookingService.save(bookingDto, 1L);

            Assertions.assertNotNull(foundBooking);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenOwnerBooking() {
            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(item));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto, 1L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenStartIsPast() {
            BookingDto newBookingDto = TestValueBuilder.createBookingDto();
            newBookingDto.setStart(LocalDateTime.now().minusDays(1));

            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(item));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto, 1L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenEndIsPast() {
            BookingDto newBookingDto = TestValueBuilder.createBookingDto();
            newBookingDto.setEnd(LocalDateTime.now().minusDays(1));

            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(item));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto, 1L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenItemUnavailable() {
            Item newItem = TestValueBuilder.createItemWithId();
            newItem.setAvailable(false);

            when(userService.getById(Mockito.anyLong()))
                    .thenReturn(user);

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(newItem));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto, 1L));

            Assertions.assertTrue(throwable instanceof BadRequestException);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenItemAbsent() {

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.save(bookingDto, 1L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class Update {
        @Test
        void shouldApproveBooking() {
            Booking newBooking = TestValueBuilder.createBookingWithId();
            newBooking.setStatus(EStatus.WAITING);

            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(newBooking));

            when(bookingRepository.save(Mockito.any()))
                    .thenReturn(newBooking);

            Booking update = bookingService.update(1L, true, 1L);

            Assertions.assertEquals(EStatus.APPROVED, update.getStatus());
        }

        @Test
        void shouldRejectedBooking() {
            Booking newBooking = TestValueBuilder.createBookingWithId();
            newBooking.setStatus(EStatus.WAITING);

            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(newBooking));

            when(bookingRepository.save(Mockito.any()))
                    .thenReturn(newBooking);

            Booking update = bookingService.update(1L, false, 1L);

            Assertions.assertEquals(EStatus.REJECTED, update.getStatus());
        }

        @Test
        void shouldBadRequestExceptionWhenBookingAlreadyApproved() {
            Booking newBooking = TestValueBuilder.createBookingWithId();
            newBooking.setStatus(EStatus.APPROVED);

            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(newBooking));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.update(1L,
                    true, 1L));

            Assertions.assertTrue(throwable instanceof BadRequestException);
        }

        @Test
        void shouldNotFoundExceptionWhenNotBooker() {
            Booking newBooking = TestValueBuilder.createBookingWithId();
            newBooking.setStatus(EStatus.APPROVED);

            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(newBooking));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.update(1L,
                    true, 2L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldNotFoundExceptionWhenBookingAbsent() {
            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.update(1L,
                    true, 2L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class GetById {
        @Test
        void shouldGetBooking() {
            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(booking));

            Booking found = bookingService.getById(1L, 1L);

            Assertions.assertNotNull(found);
        }

        @Test
        void throwBadRequestExceptionWhenBookingAbsent() {
            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService
                    .getById(1L, 1L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenNotBooker() {
            when(bookingRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(booking));

            Throwable throwable = Assertions.assertThrows(Exception.class, () -> bookingService.getById(1L, 5L));

            Assertions.assertTrue(throwable instanceof NotFoundException);
        }
    }

    @Nested
    class FindAllByOwner {
        @Test
        void shouldReturnListPastBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByItemUserIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.PAST, 0, 1, true);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListCurrentBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllCurrentBookingsByOwnerId(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.CURRENT, 0, 1, true);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListFutureBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByItemUserIdAndStartAfterOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.FUTURE, 0, 1, true);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListWaitingBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.WAITING, 0, 1, true);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListRejectedBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.REJECTED, 0, 1, true);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListAllBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByItemUserIdOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.ALL, 0, 1, true);

            Assertions.assertEquals(1, all.size());
        }
    }

    @Nested
    class FindAllByUser {
        @Test
        void shouldReturnListPastBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.PAST, 0, 1, false);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListCurrentBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllCurrentBookingsByUserId(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.CURRENT, 0, 1, false);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListFutureBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.FUTURE, 0, 1, false);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListWaitingBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.WAITING, 0, 1, false);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListRejectedBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.REJECTED, 0, 1, false);

            Assertions.assertEquals(1, all.size());
        }

        @Test
        void shouldReturnListAllBookings() {
            Page<Booking> bookings = new PageImpl<>(List.of(booking));

            when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyLong(),
                    Mockito.any()))
                    .thenReturn(bookings);

            List<Booking> all = bookingService.findAll(1L, EState.ALL, 0, 1, false);

            Assertions.assertEquals(1, all.size());
        }
    }
}
