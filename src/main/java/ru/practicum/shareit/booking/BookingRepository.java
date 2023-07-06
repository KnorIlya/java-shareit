package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EStatus;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, EStatus status, Pageable pageable);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end > ?2 " +
            "and b.start < ?2 " +
            "order by b.start desc")
    Page<Booking> findAllCurrentBookingsByUserId(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(Long userId, EStatus status, Pageable pageable);

    Page<Booking> findAllByItemUserIdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByItemUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    Page<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.user.id = ?1 " +
            "and b.end > ?2 " +
            "and b.start < ?2 " +
            "order by b.start desc")
    Page<Booking> findAllCurrentBookingsByOwnerId(Long userId, LocalDateTime dateTime, Pageable pageable);

    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, EStatus status, LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, EStatus status, LocalDateTime start);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, EStatus status, LocalDateTime end);
}
