package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForClient;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public SimpleItemRequestDto create(Long userId, ItemRequestDto dto) {
        User requester = userService.getById(userId);
        ItemRequest request = itemRequestRepository.save(ItemRequestMapper.toEntity(dto, requester));

        return ItemRequestMapper.toSimpleDto(request);
    }

    public List<ItemRequestDtoForClient> getAllByOwnerId(Long ownerId) {
        userService.getById(ownerId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(ownerId);

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toDto(itemRequest, getItemFromRequest(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    private List<ItemShortDto> getItemFromRequest(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return items.stream()
                .map(item -> {
                    ItemShortDto dto = ItemMapper.toShortDto(item);
                    dto.setRequestId(requestId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ItemRequestDtoForClient> getAll(Long userId, Integer from, Integer size) {
        Page<ItemRequest> content = itemRequestRepository.findAllAnotherRequest(userId,
                PageRequest.of(from, size));

        if (content.isEmpty() && content.getTotalPages() != 0) {
            content = itemRequestRepository.findAllAnotherRequest(userId, getLastPage(content));
        }

        return content.getContent().stream()
                .map(itemRequest -> ItemRequestMapper.toDto(itemRequest, getItemFromRequest(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    public ItemRequestDtoForClient getById(Long id, Long userId) {
        userService.getById(userId);

        return ItemRequestMapper.toDto(
                itemRequestRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Request not found")),
                getItemFromRequest(id));
    }

    private Pageable getLastPage(Page<ItemRequest> content) {
        int from = content.getTotalPages() - 1;
        int size = content.getSize();

        return PageRequest.of(from, size);
    }
}
