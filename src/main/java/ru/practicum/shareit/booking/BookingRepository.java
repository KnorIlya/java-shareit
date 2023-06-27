package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, EStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end > ?2 " +
            "and b.start < ?2 " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsByBookerId(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(Long userId, EStatus status);

    List<Booking> findAllByItemUserIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    @Query(" select b from Booking b " +
            "where b.item.user.id = ?1 " +
            "and b.end > ?2 " +
            "and b.start < ?2 " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsByOwnerId(Long userId, LocalDateTime dateTime);

    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, EStatus status, LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, EStatus status, LocalDateTime start);

   Booking findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, EStatus status, LocalDateTime end);
}
