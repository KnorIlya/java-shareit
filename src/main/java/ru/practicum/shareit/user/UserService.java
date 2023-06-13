package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public User create(User user) {
        userDao.emailValidation(user.getEmail());
        return userDao.saveUser(user);
    }

    public User update(Long id, Map<String, Object> updates) {
        userDao.update(id, updates);
        return userDao.getById(id);
    }

    public User getById(Long id) {
        return userDao.getById(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void deleteById(Long id) {
        userDao.delete(id);
    }

}
