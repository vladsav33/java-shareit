package ru.practicum.shareit.request.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.variables.Variables.HEADER;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    public final ItemRequestService itemRequestService;
    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(HEADER) long userId, @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setRequestor(userId);
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(HEADER) long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(HEADER) long userId,
                                               @RequestParam(defaultValue = "0")  @Min(0) int from,
                                               @RequestParam (defaultValue = "20") @Min(1) int size) {
        Pageable page = PageRequest.of(from / size, size);
        return itemRequestService.getAllRequests(userId, page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
