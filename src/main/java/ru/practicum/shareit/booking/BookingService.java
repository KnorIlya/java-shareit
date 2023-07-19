package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public Booking save(BookingDto bookingDto, Long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        User user = userService.getById(userId);
        Booking booking = BookingMapper.toEntity(bookingDto, user, item);
        bookingValidation(booking);
        return bookingRepository.save(booking);
    }

    private void bookingValidation(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item's unavailable");
        }
        if (!bookingDateValidation(booking.getStart(), booking.getEnd())) {
            throw new BadRequestException("Incorrect booking time");
        }
        if (Objects.equals(booking.getItem().getUser().getId(), booking.getBooker().getId())) {
            throw new NotFoundException("You can't book that");
        }
    }

    private boolean bookingDateValidation(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return false;
        } else return !start.isEqual(end) &&
                !end.isBefore(start) &&
                !start.isBefore(LocalDateTime.now()) &&
                !end.isBefore(LocalDateTime.now());
    }

    public Booking update(Long id, Boolean approved, Long userId) {
        Booking booking = new Booking();
        if (approved) {
            booking.setStatus(EStatus.APPROVED);
        } else {
            booking.setStatus(EStatus.REJECTED);
        }
        return bookingRepository.findById(id).map(model -> {

            if (Objects.equals(model.getItem().getUser().getId(), userId)) {
                if (model.getStatus().equals(EStatus.APPROVED)) {
                    throw new BadRequestException("Booking was approved");
                }
                String[] nulls = getNullPropertyNames(booking);

                BeanUtils.copyProperties(booking, model, nulls);
                return bookingRepository.save(model);
            } else {
                throw new NotFoundException("Insufficient rights to execute the operation");
            }
        }).orElseThrow(() -> new NotFoundException("Entity not found"));

    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public Booking getById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Entity not found"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getUser().getId().equals(userId)) {
            return booking;
        } else {
            throw new NotFoundException("Insufficient rights to execute the operation");
        }
    }

    public List<Booking> findAll(Long userId, EState state, Integer from, Integer size, boolean owner) {
        LocalDateTime now = LocalDateTime.now();
        userService.getById(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        if (!owner) {
            return findAllByUserIdAndState(userId, state, pageRequest, now);
        } else {
            return findAllByOwnerIdAndState(userId, state, pageRequest, now);
        }
    }

    private List<Booking> findAllByUserIdAndState(Long userId, EState state, Pageable pageable, LocalDateTime time) {
        Page<Booking> content;
        switch (state) {
            case PAST:
                content = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        time,
                        pageable);
                break;
            case CURRENT:
                content = bookingRepository.findAllCurrentBookingsByUserId(userId,
                        time,
                        pageable);
                break;
            case FUTURE:
                content = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        time,
                        pageable);
                break;
            case WAITING:
                content = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                        EStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                content = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                        EStatus.REJECTED,
                        pageable);
                break;
            default:
                content = bookingRepository.findAllByBookerIdOrderByStartDesc(userId,
                        pageable);
                break;
        }
        if (content.isEmpty() && content.getTotalPages() != 0) {
            return findAllByUserIdAndState(userId, state, getLastPage(content), time);
        } else {
            return content.getContent();
        }
    }

    private List<Booking> findAllByOwnerIdAndState(Long userId, EState state, Pageable pageable, LocalDateTime time) {
        Page<Booking> content;
        switch (state) {
            case PAST:
                content = bookingRepository.findAllByItemUserIdAndEndBeforeOrderByStartDesc(userId,
                        time,
                        pageable);
                break;
            case CURRENT:
                content = bookingRepository.findAllCurrentBookingsByOwnerId(userId,
                        time,
                        pageable);
                break;
            case FUTURE:
                content = bookingRepository.findAllByItemUserIdAndStartAfterOrderByStartDesc(userId,
                        time,
                        pageable);
                break;
            case WAITING:
                content = bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId,
                        EStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                content = bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId,
                        EStatus.REJECTED,
                        pageable);
                break;
            default:
                content = bookingRepository.findAllByItemUserIdOrderByStartDesc(userId,
                        pageable);
        }

        if (content.isEmpty() && content.getTotalPages() != 0) {
            return findAllByOwnerIdAndState(userId, state, getLastPage(content), time);
        } else {
            return content.getContent();
        }
    }

    private Pageable getLastPage(Page<Booking> content) {
        int from = content.getTotalPages() - 1;
        int size = content.getSize();
        return PageRequest.of(from, size);
    }
}
