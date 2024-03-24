package greencity.repository;

import greencity.entity.EventsImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventsImagesRepo extends JpaRepository<EventsImages, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value ="DELETE FROM events_images WHERE event_id = :eventId")
    void deleteAllEventsImagesByEventId(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value ="DELETE FROM events_images WHERE event_id = :eventId AND link = :link")
    void deleteAllEventsImagesByEventIdAndLink(@Param("eventId") Long eventId, @Param("link") String link);
}
