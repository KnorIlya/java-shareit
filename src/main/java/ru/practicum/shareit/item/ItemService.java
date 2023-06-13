package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemMapper mapper;

    public ItemDto create(Item item) {
        userDao.getById(item.getUserId());
        return mapper.EntityToDto(itemDao.save(item));
    }

    public ItemDto getById(Long id) {
        return mapper.EntityToDto(itemDao.getById(id));
    }

    public List<ItemDto> getAllByUserId(Long id) {
        return itemDao.getAllByUserId(id).stream()
                .map(mapper::EntityToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsByText(String text) {
        return itemDao.getItemsByText(text).stream()
                .map(mapper::EntityToDto)
                .collect(Collectors.toList());
    }

    public ItemDto update(Long id, Map<String, Object> updates, Long userId) {
        if (!itemDao.ownerValidation(id, userId)) {
            throw new PermissionException("Invalid access rights");
        }
        return mapper.EntityToDto(itemDao.update(id, updates));
    }
}
