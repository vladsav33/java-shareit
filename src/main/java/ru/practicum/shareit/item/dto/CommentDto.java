package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDto {
    private long id;
    private String text;
    private long itemId;
    private long authorId;
    private LocalDateTime created;
    private String authorName;
}
