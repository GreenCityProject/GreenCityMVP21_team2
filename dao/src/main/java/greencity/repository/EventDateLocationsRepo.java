package greencity.repository;

import greencity.entity.EventDateLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface EventDateLocationsRepo extends JpaRepository<EventDateLocation, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value ="DELETE FROM event_date_locations WHERE event_id = :eventId")
    void deleteAllEventDateLocationsByEventId(@Param("eventId") Long eventId);

    List<EventDateLocation> findAllByEventId(Long eventId);
}
