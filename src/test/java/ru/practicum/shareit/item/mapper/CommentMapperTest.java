package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestValueBuilder;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapperTest {

    @Test
    void shouldReturnCommentDtoIfInputIsValid() {
        Item item = TestValueBuilder.createItemWithId();
        User user = TestValueBuilder.createUserWithId(1L);

        Comment comment = TestValueBuilder.createCommentWithId(user, item);

        CommentDto dto = CommentMapper.toDto(comment);

        Assertions.assertEquals(comment.getId(),dto.getId());
        Assertions.assertEquals(comment.getText(), dto.getText());
        Assertions.assertEquals(comment.getUser().getName(), dto.getAuthorName());
    }
}
