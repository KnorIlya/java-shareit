package ru.practicum.shareit.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.EState;


@Component
public class StringToEnumConverter implements Converter<String, EState> {
    @Override
    public EState convert(String s) {
            return EState.valueOf(s.toUpperCase());
    }
}

