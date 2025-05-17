package com.example.hena.event.repository;

import com.example.hena.event.entity.Event;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByHost_Id(Long hostId); // Find events by host ID

    List<Event> findByCreatedByAdminId(Long adminId); // Find events created by admin

    List<Event> findByEventDate(LocalDateTime eventDate); // Find events by date

    List<Event> findByCategory(String category); // Find events by category

    List<Event> findByEventDateAfter(LocalDateTime now);

    @Query("SELECT e FROM Event e JOIN e.rsvps r WHERE r.id = :userId")
    List<Event> findEventsByRsvpUserId(@Param("userId") Long userId);

    public List<Event> findByEventDateAndCategory(LocalDateTime eventDate, String category);
    // Find events by date and category
}
