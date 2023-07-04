package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.EStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemMapper mapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    public ItemShortDto create(Long userId, Item item) {
        item.setUser(userService.getById(userId));
        return mapper.toShortDto(itemRepository.save(item));
    }

    public CommentDto addComment(CommentDto comment, Long userId, Long itemId) {

        commentValidation(userId, itemId);

        Comment savedComment = commentRepository.save(
                Comment.builder()
                        .text(comment.getText())
                        .created(LocalDateTime.now())
                        .user(userService.getById(userId))
                        .item(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found")))
                        .build()
        );
        return commentMapper.toDto(savedComment);
    }

    public void commentValidation(Long userId, Long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId,
                userId,
                EStatus.APPROVED,
                LocalDateTime.now()
        );
        if (booking == null) {
            throw new BadRequestException("You're don't book this item");
        }
    }

    public ItemDto getById(Long id, Long userId) {
        return itemRepository.findById(id).map(item -> createItemDto(item, userId))
                .orElseThrow(() -> new NotFoundException("Entity not found"));
    }

    public List<ItemDto> getAllByUserId(Long id) {
        return itemRepository.findAllByUserIdOrderByIdAsc(id).stream()
                .map(item -> createItemDto(item,id))
                .collect(Collectors.toList());
    }

    public List<Item> getItemsByText(String text) {
        return itemRepository.findByText(text);
    }

    public Item update(Long id, Item item, Long userId) {
        return itemRepository.findById(id).map(model -> {
            if (Objects.equals(model.getUser().getId(), userId)) {
                String[] nulls = getNullPropertyNames(item);

                BeanUtils.copyProperties(item, model, nulls);
                return itemRepository.save(model);
            } else {
                throw new PermissionException("Insufficient rights to execute the operation");
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

    private ItemDto createItemDto(Item item, Long userId) {

        LocalDateTime now = LocalDateTime.now();
        List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId()).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        BookingDtoForItem prev = null;
        BookingDtoForItem next = null;
        if (item.getUser().getId().equals(userId)) {

             prev = bookingMapper.toBookingDtoForItem(
                     bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                    item.getId(),
                    EStatus.APPROVED,
                    now
            ));
             next = bookingMapper.toBookingDtoForItem(
                     bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                    item.getId(),
                    EStatus.APPROVED,
                    now
            ));
        }

        return mapper.toDto(item,prev, next, comments);
    }
}
