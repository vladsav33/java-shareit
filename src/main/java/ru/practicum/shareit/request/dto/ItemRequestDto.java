package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.variables.Variables.DATE_FORMAT;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private long requestor;
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime created;
    private List<ItemDto> items;
}
