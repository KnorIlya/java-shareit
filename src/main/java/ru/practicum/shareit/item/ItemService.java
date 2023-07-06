package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public ItemShortDto create(Long userId, ItemShortDto dto) {
        Item item = ItemMapper.toEntity(dto);
        Long requestId = dto.getRequestId();
        if (requestId != null) {
            item.setRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request not found")));
        }
        item.setUser(userService.getById(userId));
        Item savedItem = itemRepository.save(item);
        dto.setId(savedItem.getId());
        dto.setRequestId(requestId);

        return dto;
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
        return CommentMapper.toDto(savedComment);
    }

    private void commentValidation(Long userId, Long itemId) {
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

    public List<ItemDto> getAllByUserId(Long id, Integer from, Integer size) {
        Page<Item> content = itemRepository.findAllByUserIdOrderByIdAsc(id, PageRequest.of(from, size));
        if (content.isEmpty() && content.getTotalPages() != 0) {
            content = itemRepository.findAllByUserIdOrderByIdAsc(id, getLastPage(content));
        }
        return content.stream()
                .map(item -> createItemDto(item, id))
                .collect(Collectors.toList());
    }

    public List<ItemShortDto> getItemsByText(String text, Integer from, Integer size) {
        Page<Item> content = itemRepository.findByText(text, PageRequest.of(from, size));
        if (content.isEmpty() && content.getTotalPages() != 0) {
            content = itemRepository.findByText(text, getLastPage(content));
        }

        return content.stream()
                .map(ItemMapper::toShortDto)
                .collect(Collectors.toList());
    }

    public ItemShortDto update(Long id, Item item, Long userId) {
        return itemRepository.findById(id).map(model -> {
            if (Objects.equals(model.getUser().getId(), userId)) {
                String[] nulls = getNullPropertyNames(item);

                BeanUtils.copyProperties(item, model, nulls);
                return  ItemMapper.toShortDto(itemRepository.save(model));
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
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        BookingDtoForItem prev = null;
        BookingDtoForItem next = null;
        if (item.getUser().getId().equals(userId)) {

            prev = BookingMapper.toBookingDtoForItem(
                    bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                            item.getId(),
                            EStatus.APPROVED,
                            now
                    ));
            next = BookingMapper.toBookingDtoForItem(
                    bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(),
                            EStatus.APPROVED,
                            now
                    ));
        }

        return ItemMapper.toDto(item, prev, next, comments);
    }

    private Pageable getLastPage(Page<Item> content) {
        int from = content.getTotalPages() - 1;
        int size = content.getSize();
        return PageRequest.of(from, size);
    }
}
