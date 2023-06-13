package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ItemDao {
    private final JdbcTemplate jdbcTemplate;
    private final ItemMapper mapper;

    public Item save(Item item) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("items")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", item.getName());
        params.put("description", item.getDescription());
        params.put("available", item.getAvailable());
        params.put("user_id", item.getUserId());

        Number id = insert.executeAndReturnKey(params);
        item.setId((Long) id);
        return item;
    }

    public Item getById(Long id) {
        String query = "select id, user_id, name, description, available from items where id = ?";
        List<Item> item = jdbcTemplate.query(query, mapper, id);

        if (item.isEmpty()) {
            throw new NotFoundException("Entity not found");
        }
        return Optional.of(item.get(0)).get();
    }

    public List<Item> getAllByUserId(Long id) {
        String query = "select id, user_id, name, description, available from items where user_id = ?";
        return jdbcTemplate.query(query, mapper, id);
    }

    public List<Item> getItemsByText(String text) {
        String query = "select id, user_id, name, description, available from items where available = true " +
                "and (" +
                "lower (name) like '%' || ? || '%' or " +
                "lower (description) like '%' || ? || '%'" +
                ")";
        return jdbcTemplate.query(query, mapper, text, text);
    }

    public Item update(Long id, Map<String, Object> updates) {
        StringBuilder clause = new StringBuilder();
        List<Object> values = new ArrayList<>(updates.size());

        for (Map.Entry<String, Object> map : updates.entrySet()) {
            clause.append(map.getKey()).append(" = ?, ");
            values.add(map.getValue());
        }

        values.add(id);
        String query = clause.substring(0, clause.length() - 2);
        query = "update items set " + query + " where id = ?";
        jdbcTemplate.update(query, values.toArray(new Object[0]));

        return getById(id);
    }

    public boolean ownerValidation(Long id, Long userId) {
        Item item = getById(id);
        return Objects.equals(item.getUserId(), userId);
    }
 }
