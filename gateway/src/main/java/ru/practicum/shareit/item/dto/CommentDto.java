package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private long itemId;
    private long authorId;
    private LocalDateTime created;
    private String authorName;
}
