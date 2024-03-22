package greencity.repository;

import greencity.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n JOIN FETCH n.receiver where n.id=?1")
    Optional<Notification> findByIdFetchUser(Long id);
}
