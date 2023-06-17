package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long userId, Status status, Pageable page);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime date, Pageable page);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date, Pageable page);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long userId, LocalDateTime dateStart,
                                                                        LocalDateTime dateEnd, Pageable page);

    List<Booking> findByItemOwnerOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findByItemOwnerAndStatusIsOrderByStartDesc(Long userId, Status status, Pageable page);

    List<Booking> findByItemOwnerAndStartAfterOrderByStartDesc(Long userId, LocalDateTime date, Pageable page);

    List<Booking> findByItemOwnerAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date, Pageable page);

    List<Booking> findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime dateStart,
                                                                           LocalDateTime dateEnd, Pageable page);

    List<Booking> findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(Long itemId, LocalDateTime date, Status status);

    List<Booking> findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(Long itemId, LocalDateTime date, Status status);
}
