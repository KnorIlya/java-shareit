package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional
    public User create(User user) {
        return repository.save(user);
    }


    @Transactional
    public User update(Long id, User newUser) {
        return repository.findById(id).map(model -> {
            String[] nulls = getNullPropertyNames(newUser);

            BeanUtils.copyProperties(newUser, model, nulls);
            return repository.save(model);
        }).orElseThrow(() -> new NotFoundException("Entity not found"));
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }


    @Transactional
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entity not found"));
    }

    @Transactional
    public List<User> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
