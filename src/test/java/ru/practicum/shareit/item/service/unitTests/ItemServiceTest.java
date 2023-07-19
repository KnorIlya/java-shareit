package ru.practicum.shareit.item.service.unitTests;

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
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemService itemService;

    static ItemShortDto itemShortDto;
    static Booking booking;
    static User user;
    static Item item;

    @BeforeAll
    static void before() {
        itemShortDto = TestValueBuilder.createAvailableItemShortDto("Mouse", "Mouse for PC");
        user = TestValueBuilder.createUserWithId(1L);
        booking = TestValueBuilder.createBookingWithId();
        item = TestValueBuilder.createItemWithId();

    }

    @Nested
    class Get {
        @Test
        void shouldReturnItemDto() {

            when(itemRepository.findById(Mockito.any()))
                    .thenReturn(Optional.of(item));

            when(commentRepository.findAllByItem_Id(Mockito.anyLong()))
                    .thenReturn(List.of());

            when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Mockito.anyLong(),
                    Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Mockito.anyLong(),
                    Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            ItemDto itemDto = itemService.getById(1L, 1L);

            Assertions.assertEquals(itemDto.getName(), item.getName());
            Assertions.assertNotNull(itemDto.getLastBooking());
            Assertions.assertNotNull(itemDto.getNextBooking());
            Assertions.assertNotNull(itemDto.getComments());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenItemAbsent() {
            when(itemRepository.findById(Mockito.any()))
                    .thenReturn(Optional.empty());

            Throwable thrown = Assertions.assertThrows(Exception.class, () -> itemService.getById(1L, 1L));

            Assertions.assertTrue(thrown instanceof NotFoundException);
        }
    }

    @Nested
    class GetAll {
        @Test
        void shouldReturnNoEmptyList() {
            Page<Item> page = new PageImpl<>(List.of(item), PageRequest.of(0, 1), 0);

            when(itemRepository.findAllByUserIdOrderByIdAsc(Mockito.any(), Mockito.any()))
                    .thenReturn(page);

            when(commentRepository.findAllByItem_Id(Mockito.anyLong()))
                    .thenReturn(List.of());

            when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Mockito.anyLong(),
                    Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Mockito.anyLong(),
                    Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            List<ItemDto> itemDto = itemService.getAllByUserId(1L, 1, 1);

            Assertions.assertEquals(itemDto.size(), 1);
        }

        @Test
        void shouldReturnNoEmptyListWhenPageIsNotExist() {
            Page<Item> page = new PageImpl<>(List.of(item), PageRequest.of(8, 1), 0);

            when(itemRepository.findAllByUserIdOrderByIdAsc(Mockito.any(), Mockito.any()))
                    .thenReturn(page);

            when(commentRepository.findAllByItem_Id(Mockito.anyLong()))
                    .thenReturn(List.of());

            when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Mockito.anyLong(),
                    Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Mockito.anyLong(),
                    Mockito.any(), Mockito.any()))
                    .thenReturn(booking);

            List<ItemDto> itemDto = itemService.getAllByUserId(1L, 1, 1);

            Assertions.assertEquals(itemDto.size(), 1);
        }

        @Test
        void shouldReturnEmptyListWhenItemNotFound() {
            Page<Item> page = new PageImpl<>(List.of(), PageRequest.of(0, 1), 0);

            when(itemRepository.findAllByUserIdOrderByIdAsc(Mockito.any(), Mockito.any()))
                    .thenReturn(page);

            List<ItemDto> itemDto = itemService.getAllByUserId(1L, 1, 1);

            Assertions.assertTrue(itemDto.isEmpty());
        }
    }

    @Nested
    class GetByText {
        @Test
        void shouldReturnNoEmptyList() {
            Page<Item> page = new PageImpl<>(List.of(item), PageRequest.of(0, 1), 0);

            when(itemRepository.findByText(Mockito.any(), Mockito.any()))
                    .thenReturn(page);

            List<ItemShortDto> itemDto = itemService.getItemsByText("text", 1, 1);

            Assertions.assertEquals(itemDto.size(), 1);
        }

        @Test
        void shouldReturnNoEmptyListWhenPageIsNotExist() {
            Page<Item> page = new PageImpl<>(List.of(item), PageRequest.of(8, 1), 0);

            when(itemRepository.findByText(Mockito.any(), Mockito.any()))
                    .thenReturn(page);

            List<ItemShortDto> itemDto = itemService.getItemsByText("text", 1, 1);

            Assertions.assertEquals(itemDto.size(), 1);
        }

        @Test
        void shouldReturnEmptyListWhenItemNotFound() {
            Page<Item> page = new PageImpl<>(List.of(), PageRequest.of(0, 1), 0);

            when(itemRepository.findByText(Mockito.any(), Mockito.any()))
                    .thenReturn(page);

            List<ItemShortDto> itemDto = itemService.getItemsByText("text", 1, 1);

            Assertions.assertTrue(itemDto.isEmpty());
        }
    }

    @Nested
    class Update {
        @Test
        void shouldUpdateItem() {
            Item itemForUpdate = TestValueBuilder.createItemWithId();

            Item newItem = TestValueBuilder.createItemWithId();
            newItem.setName("update");
            newItem.setDescription("updated");

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(itemForUpdate));

            when(itemRepository.save(Mockito.any()))
                    .thenReturn(itemForUpdate);

            itemService.update(1L, newItem, 1L);

            Assertions.assertEquals(itemForUpdate.getName(), "update");
            Assertions.assertEquals(itemForUpdate.getDescription(), "updated");
        }

        @Test
        void shouldUpdateItemWithOnlyNewName() {
            Item itemForUpdate = TestValueBuilder.createItemWithId();

            Item newItem = TestValueBuilder.createItemWithId();
            newItem.setName("update");

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(itemForUpdate));

            when(itemRepository.save(Mockito.any()))
                    .thenReturn(itemForUpdate);

            itemService.update(1L, newItem, 1L);

            Assertions.assertEquals(itemForUpdate.getName(), "update");
            Assertions.assertEquals(itemForUpdate.getDescription(), item.getDescription());
        }

        @Test
        void shouldThrowPermissionExceptionWhenUserIdNotEquals() {
            Item itemForUpdate = TestValueBuilder.createItemWithId();

            Item newItem = TestValueBuilder.createItemWithId();
            newItem.setName("update");

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.of(itemForUpdate));

            Throwable thrown = Assertions.assertThrows(Exception.class,
                    () -> itemService.update(1L, newItem, 99L));

            Assertions.assertTrue(thrown instanceof PermissionException);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenUserAbsent() {
            Item newItem = TestValueBuilder.createItemWithId();
            newItem.setName("update");

            when(itemRepository.findById(Mockito.anyLong()))
                    .thenReturn(Optional.empty());

            Throwable thrown = Assertions.assertThrows(Exception.class,
                    () -> itemService.update(1L, newItem, 99L));

            Assertions.assertTrue(thrown instanceof NotFoundException);
        }
    }
}
