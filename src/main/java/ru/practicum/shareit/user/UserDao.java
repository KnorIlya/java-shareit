package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper mapper;

    public User saveUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getName());
        params.put("email", user.getEmail());

        Number userId = insert.executeAndReturnKey(params);
        user.setId(userId.longValue());
        return user;
    }

    public void update(Long id, Map<String, Object> updates) {
        StringBuilder clause = new StringBuilder();
        List<Object> values = new ArrayList<>(updates.size());

        for (Map.Entry<String, Object> map : updates.entrySet()) {
            clause.append(map.getKey()).append(" = ?, ");
            values.add(map.getValue());
        }
        values.add(id);
        String query = clause.substring(0, clause.length() - 2);
        query = "update users set " + query + " where id = ?";
        if (jdbcTemplate.update(query, values.toArray(new Object[0])) == 0) {
            throw new NotFoundException("User not found");
        }
    }

    public User getById(Long id) {
        String query = "select id, email, name from users where id = ?";
        List<User> user = jdbcTemplate.query(query, mapper, id);

        if (user.isEmpty()) {
            throw new NotFoundException("Entity not found");
        }
        return Optional.of(user.get(0)).get();
    }

    public List<User> getAll() {
        String query = "select id, email, name from users";
        return jdbcTemplate.query(query, mapper);
    }

    public void delete(Long id) {
        String query = "delete from users where id = ?";
        jdbcTemplate.update(query, id);
    }

    public void emailValidation(String email) {
        String query = "select id, email, name from users where email = ?";
        List<User> user = jdbcTemplate.query(query, mapper, email);
        if (!user.isEmpty()) {
            throw new RuntimeException("Email already exists");
        }
    }
}
