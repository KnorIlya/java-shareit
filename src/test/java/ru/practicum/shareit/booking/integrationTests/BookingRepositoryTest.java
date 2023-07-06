package ru.practicum.shareit.booking.integrationTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setup() {
        user = userRepository.save(TestValueBuilder.createUserWithoutId());

        item = TestValueBuilder.createItemWithId();
        item.setUser(user);
        item.setId(null);
        itemRepository.save(item);


    }

    @Test
    void findAllCurrentBookingsByUserId() {
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusHours(10))
                .status(EStatus.APPROVED)
                .build();

        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllCurrentBookingsByUserId(user.getId(),
                LocalDateTime.now(),
                PageRequest.of(0, 2));

        Assertions.assertEquals(1, bookings.getTotalElements());
    }
}
