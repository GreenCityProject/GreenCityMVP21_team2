package greencity.repository;

import greencity.entity.Events;
import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventsRepo extends JpaRepository<Events, Long>, JpaSpecificationExecutor<Events> {

    /**
     * Method returns event {@link Events} joined with followers and attenders.
     *
     * @param id id of event.
     * @return {@link Events}.
     */

    @Query("SELECT e From Events e JOIN FETCH e.eventAttender a WHERE e.id =?1")
    Optional<Events> findByIdWithAttenders(Long id);

    /**
     * Method returns all {@link Events} by page.
     *
     * @param page page of event.
     * @return all {@link Events} by page.
     */
    Page<Events> findAllByOrderByCreationDateDesc(Pageable page);

    /**
     * Method returns all users {@link Events} by page.
     *
     * @param user organizer of event.
     * @param page page of news.
     * @return all {@link Events} by page.
     */
    Page<Events> findAllByOrganizerOrderByCreationDateDesc(User user, Pageable page);

    /**
     * Method returns all {@link Events} where the given {@link User} is organizer and event attender.
     *
     * @param id organizer and attender of event.
     * @param page page of news.
     * @return all {@link Events} by page.
     */
    @Query(nativeQuery = true,
            value ="SELECT e.* FROM events e " +
                        "LEFT JOIN events_attenders a ON e.id = a.event_id " +
                        "WHERE a.user_id = :userId OR e.organizer_id = :userId " +
                        "ORDER BY e.creation_date DESC")
    Page<Events> findAllByOrganizerOrEventAttenderOrderByCreationDateDesc(@Param("userId") Long id, Pageable page);

    /**
     * Method returns all {@link Events} where the given {@link User} is an event attender.
     *
     * @param id the user who attended the event.
     * @param page          page of events.
     * @return all {@link Events} where the given user is an event attender.
     */
    @Query(nativeQuery = true,
            value =
                    "SELECT e.* FROM events e " +
                            "LEFT JOIN events_attenders a ON e.id = a.event_id " +
                            "WHERE a.user_id = :userId " +
                            "ORDER BY e.creation_date DESC"
    )
    Page<Events> findAllByEventAttenderOrderByCreationDateDesc(@Param("userId") Long id, Pageable page);
}
